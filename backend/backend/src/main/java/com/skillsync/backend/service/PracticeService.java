
package com.skillsync.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.skillsync.backend.client.GroqClient;
import com.skillsync.backend.client.LLMProvider;

import com.skillsync.backend.dto.MatchingResult;
import com.skillsync.backend.dto.PracticeAnswerRequest;
import com.skillsync.backend.dto.PracticeAttemptResponse;
import com.skillsync.backend.dto.PracticeFeedbackResponse;
import com.skillsync.backend.dto.PracticeQuestionResponse;
import com.skillsync.backend.dto.SkillEvidence;
import com.skillsync.backend.dto.SkillMatchItem;

import com.skillsync.backend.entity.Analysis;
import com.skillsync.backend.entity.PracticeAttempt;

import com.skillsync.backend.enums.Difficulty;
import com.skillsync.backend.enums.Status;

import com.skillsync.backend.repository.AnalysisRepository;
import com.skillsync.backend.repository.PracticeAttemptRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class PracticeService {

    private final AnalysisRepository arepo;
    private final PracticeAttemptRepository prepo;
    private final MatchingService mser;
    private final ObjectMapper objectMapper;
    private final LLMProvider llmProvider;
    private final GroqClient groqClient;

    public PracticeService(
            AnalysisRepository arepo,
            PracticeAttemptRepository prepo,
            MatchingService mser,
            ObjectMapper objectMapper,
            LLMProvider llmProvider,
            GroqClient groqClient) {

        this.arepo = arepo;
        this.prepo = prepo;
        this.mser = mser;
        this.objectMapper = objectMapper;
        this.llmProvider = llmProvider;
        this.groqClient = groqClient;
    }


    // =====================================================
    // 1. GENERATE INTERVIEW QUESTION
    // =====================================================

   
public List<PracticeQuestionResponse> generateQuestion(
        Long analysisId, String difficulty) {

    Analysis analysis = arepo.findById(analysisId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Analysis not found"));

    String json = analysis.getResultsJson();

    if (json == null || json.isBlank()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Analysis results are empty");
    }

    Map<String, Object> data = objectMapper.readValue(
            json, new TypeReference<Map<String, Object>>() {});

    List<SkillEvidence> resumeSkills = objectMapper.convertValue(
            data.get("ResumeSkills"),
            new TypeReference<List<SkillEvidence>>() {});

    List<SkillEvidence> jobSkills = objectMapper.convertValue(
            data.get("JobSkills"),
            new TypeReference<List<SkillEvidence>>() {});

    MatchingResult result =
            mser.calcuateMatch(resumeSkills, jobSkills);

    List<SkillMatchItem> weakSkills = new ArrayList<>();
    weakSkills.addAll(result.getMissingSkills());
    weakSkills.addAll(result.getPartialSkills());

    if (weakSkills.isEmpty()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No skill gaps found for practice");
    }

    Difficulty selectedDifficulty;

    try {
        selectedDifficulty = (difficulty == null || difficulty.isBlank())
                ? Difficulty.MEDIUM
                : Difficulty.valueOf(difficulty.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Difficulty must be EASY, MEDIUM or HARD");
    }

    // Select up to five weak skills.
    List<String> skills = weakSkills.stream()
            .limit(5)
            .map(SkillMatchItem::getSkill)
            .toList();

    String prompt = """
            Generate exactly 5 different technical interview questions.

            Skills: %s
            Difficulty: %s

            Distribute questions across the given skills.
            Use only the given skills.
            Avoid duplicate questions.
            Expected points: 3 per question.

            Return ONLY valid JSON:

            {
              "questions": [
                {
                  "skill": "Java",
                  "question": "Question text",
                  "expectedPoints": ["Point 1", "Point 2", "Point 3"],
                  "hint": "Short hint"
                }
              ]
            }
            """.formatted(skills, selectedDifficulty);

    System.out.println("Generating 5 practice questions...");

    Map<String, Object> aiData = generateJsonWithFallback(prompt);

    List<Map<String, Object>> questions = objectMapper.convertValue(
            aiData.get("questions"),
            new TypeReference<List<Map<String, Object>>>() {});

    if (questions == null || questions.size() < 5) {
        throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "AI generated fewer than 5 questions");
    }

    List<PracticeQuestionResponse> responses = new ArrayList<>();

    for (int i = 0; i < 5; i++) {

        Map<String, Object> questionData = questions.get(i);

        String skill = (String) questionData.get("skill");
        String question = (String) questionData.get("question");
        String hint = (String) questionData.get("hint");

        if (skill == null || !skills.contains(skill)) {
            skill = skills.get(i % skills.size());
        }

        if (question == null || question.isBlank()) {
            continue;
        }

        List<String> expectedPoints = objectMapper.convertValue(
                questionData.get("expectedPoints"),
                new TypeReference<List<String>>() {});

        if (expectedPoints == null || expectedPoints.isEmpty()) {
            continue;
        }

        PracticeAttempt attempt = new PracticeAttempt();

        attempt.setAnalysis(analysis);
        attempt.setSkill(skill);
        attempt.setQuestion(question);
        attempt.setDifficulty(selectedDifficulty);
        attempt.setExpectedPoints(
                objectMapper.writeValueAsString(expectedPoints));
        attempt.setHint(hint);
        attempt.setStatus(Status.GENERATED);

        PracticeAttempt saved = prepo.save(attempt);

        responses.add(new PracticeQuestionResponse(
                saved.getId(),
                saved.getSkill(),
                saved.getQuestion(),
                saved.getDifficulty(),
                saved.getHint(),
                saved.getStatus(),
                saved.getCreatedAt()
        ));
    }

    if (responses.size() < 5) {
        throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Could not generate 5 valid practice questions");
    }

    return responses;
}




    // =====================================================
    // 2. SUBMIT ANSWER AND GENERATE FEEDBACK
    // =====================================================

    public PracticeFeedbackResponse submitanswer(
            Long attemptId,
            PracticeAnswerRequest request) {

        // 1. Retrieve practice attempt
        PracticeAttempt attempt = prepo.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Practice attempt not found"));

        System.out.println("Practice attempt found: " + attemptId);


        // 2. Prevent submitting an already evaluated attempt
        if (attempt.getStatus() != Status.GENERATED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This practice question has already been submitted");
        }


        // 3. Get user's answer
        String answer = request.getAnswer().trim();


        // 4. Save user's answer
        attempt.setUserAnswer(answer);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setStatus(Status.SUBMITTED);

        prepo.save(attempt);


        // 5. Prepare evaluation prompt
        String prompt = """
                You are a technical interview evaluator.

                Evaluate the candidate's answer using the expected answer points.

                Skill: %s
                Difficulty: %s

                Question:
                %s

                Expected answer points:
                %s

                Candidate's answer:
                %s

                Return ONLY valid JSON using this exact structure:

                {
                  "coveredPoints": [
                    "Concept correctly explained"
                  ],
                  "missingPoints": [
                    "Important concept not explained"
                  ],
                  "explanation": "Overall evaluation of the answer",
                  "nextSteps": [
                    "What the candidate should improve"
                  ]
                }

                Rules:
                1. Evaluate only technical correctness and relevance.
                2. Do not mark a point covered unless the answer demonstrates it.
                3. Do not penalize different but technically correct wording.
                4. Keep feedback constructive and beginner-friendly.
                5. Return valid JSON only, without Markdown or code fences.
                """.formatted(
                attempt.getSkill(),
                attempt.getDifficulty(),
                attempt.getQuestion(),
                attempt.getExpectedPoints(),
                answer
        );


        // 6. Generate evaluation using Gemini with Groq fallback
        System.out.println("Generating answer evaluation");

        Map<String, Object> feedbackData;

        try {

            feedbackData = generateJsonWithFallback(prompt);

        } catch (ResponseStatusException ex) {

            // Both providers failed
            attempt.setStatus(Status.FAILED);
            prepo.save(attempt);

            throw ex;
        }


        // 7. Extract covered points
        List<String> coveredPoints = objectMapper.convertValue(
                feedbackData.get("coveredPoints"),
                new TypeReference<List<String>>() {}
        );


        // 8. Extract missing points
        List<String> missingPoints = objectMapper.convertValue(
                feedbackData.get("missingPoints"),
                new TypeReference<List<String>>() {}
        );


        // 9. Extract explanation
        String explanation =
                (String) feedbackData.get("explanation");


        // 10. Extract next steps
        List<String> nextSteps = objectMapper.convertValue(
                feedbackData.get("nextSteps"),
                new TypeReference<List<String>>() {}
        );


        // 11. Handle null values
        if (coveredPoints == null) {
            coveredPoints = new ArrayList<>();
        }

        if (missingPoints == null) {
            missingPoints = new ArrayList<>();
        }

        if (nextSteps == null) {
            nextSteps = new ArrayList<>();
        }

        if (explanation == null || explanation.isBlank()) {
            explanation = "Evaluation completed.";
        }


        // 12. Save feedback in database
        attempt.setFeedback(
                objectMapper.writeValueAsString(feedbackData));

        attempt.setStatus(Status.EVALUATED);

        PracticeAttempt saved = prepo.save(attempt);


        // 13. Return feedback response
        return new PracticeFeedbackResponse(
                saved.getId(),
                saved.getSkill(),
                saved.getUserAnswer(),
                coveredPoints,
                missingPoints,
                explanation,
                nextSteps,
                saved.getStatus().name()
        );
    }



    // =====================================================
    // 3. GEMINI + GROQ FALLBACK
    // =====================================================

    private Map<String, Object> generateJsonWithFallback(
            String prompt) {

        // First try Gemini
        try {

            System.out.println("Trying Gemini...");

            String response =
                    llmProvider.generateContent(prompt);

            Map<String, Object> result = parseAiJson(response);

            System.out.println("Gemini succeeded.");

            return result;

        } catch (Exception geminiException) {

            System.err.println(
                    "Gemini failed: " + geminiException.getMessage());

            System.out.println("Switching to Groq...");
        }


        // If Gemini fails, try Groq
        try {

            String response =
                    groqClient.generateContent(prompt);

            Map<String, Object> result = parseAiJson(response);

            System.out.println("Groq succeeded.");

            return result;

        } catch (Exception groqException) {

            System.err.println(
                    "Groq failed: " + groqException.getMessage());

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Both Gemini and Groq failed. Check backend logs.",
                    groqException
            );
        }
    }



    // =====================================================
    // 4. CLEAN AND PARSE AI JSON
    // =====================================================

    private Map<String, Object> parseAiJson(String response) {

        if (response == null || response.isBlank()) {
            throw new IllegalStateException(
                    "AI returned an empty response");
        }

        String cleanedResponse = response.trim();


        // Remove Markdown JSON code fences
        if (cleanedResponse.startsWith("```json")) {

            cleanedResponse =
                    cleanedResponse.substring(7).trim();

        } else if (cleanedResponse.startsWith("```")) {

            cleanedResponse =
                    cleanedResponse.substring(3).trim();
        }


        if (cleanedResponse.endsWith("```")) {

            cleanedResponse = cleanedResponse.substring(
                    0,
                    cleanedResponse.length() - 3
            ).trim();
        }


        // Convert JSON string into Map
        return objectMapper.readValue(
                cleanedResponse,
                new TypeReference<Map<String, Object>>() {}
        );
    }



    // =====================================================
    // 5. GET PRACTICE HISTORY
    // =====================================================

    @Transactional(readOnly = true)
    public List<PracticeAttemptResponse> getPracticeHistory(
            Long analysisId) {

        // Check whether analysis exists
        arepo.findById(analysisId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Analysis not found"));


        // Retrieve all attempts
        List<PracticeAttempt> attempts =
                prepo.findByAnalysis_IdOrderByCreatedAtDesc(
                        analysisId);


        // Convert entities into DTOs
        return attempts.stream()
                .map(this::convertToResponse)
                .toList();
    }



    // =====================================================
    // 6. GET ONE PRACTICE ATTEMPT
    // =====================================================

    @Transactional(readOnly = true)
    public PracticeAttemptResponse getPracticeAttempt(
            Long attemptId) {

        PracticeAttempt attempt = prepo.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Practice attempt not found"));

        return convertToResponse(attempt);
    }



    // =====================================================
    // 7. CONVERT ENTITY INTO RESPONSE DTO
    // =====================================================

    private PracticeAttemptResponse convertToResponse(
            PracticeAttempt attempt) {

        return new PracticeAttemptResponse(
                attempt.getId(),
                attempt.getAnalysis().getId(),
                attempt.getSkill(),
                attempt.getQuestion(),
                attempt.getDifficulty(),
                attempt.getHint(),
                attempt.getUserAnswer(),
                attempt.getFeedback(),
                attempt.getStatus(),
                attempt.getCreatedAt(),
                attempt.getSubmittedAt()
        );
    }

}
