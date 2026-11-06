
package com.skillsync.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.skillsync.backend.client.GroqClient;
import com.skillsync.backend.client.LLMProvider;
import com.skillsync.backend.dto.MatchingResult;
import com.skillsync.backend.dto.PracticeAnswerRequest;
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

import jakarta.validation.Valid;
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

    public PracticeQuestionResponse generateQuestion(
            Long analysisId, String difficulty) {

        // 1. Fetch analysis
        Analysis analysis = arepo.findById(analysisId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Analysis not found"));
        
        
        // 2. Read stored JSON
        String json = analysis.getResultsJson();

        if (json == null || json.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Analysis results are empty");
        }

        // 3. Convert JSON into Map
        Map<String, Object> data = objectMapper.readValue(
                json,
                new TypeReference<Map<String, Object>>() {}
        );

        // 4. Convert JSON values into skill lists
        List<SkillEvidence> resumeSkills = objectMapper.convertValue(
                data.get("ResumeSkills"),
                new TypeReference<List<SkillEvidence>>() {}
        );

        System.out.println("STEP 3: Resume skills extracted");
System.out.println("Resume skill count: " + resumeSkills.size());


        List<SkillEvidence> jobSkills = objectMapper.convertValue(
                data.get("JobSkills"),
                new TypeReference<List<SkillEvidence>>() {}
        );

       System.out.println("STEP 4: Job skills extracted");
System.out.println("Job skill count: " + jobSkills.size());



        // 5. Calculate matching result
        MatchingResult result =
                mser.calcuateMatch(resumeSkills, jobSkills);

        List<SkillMatchItem> missing = result.getMissingSkills();
        List<SkillMatchItem> partial = result.getPartialSkills();

        System.out.println("Missing skills: " + missing.size());
System.out.println("Partial skills: " + partial.size());
System.out.println("Matched skills: " + result.getMatchedSkills().size());

        System.out.println("STEP 5: Matching completed");

        if (missing.isEmpty() && partial.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No skill gaps found for practice");
        }

        // 6. Select weak skill
        SkillMatchItem selectedSkill;

        if (!missing.isEmpty()) {
            selectedSkill = missing.get(0);
        } else {
            selectedSkill = partial.get(0);
        }

        System.out.println("STEP 6: Selected skill: " + selectedSkill);

        // 7. Validate difficulty
        Difficulty selectedDifficulty;

        if (difficulty == null || difficulty.isBlank()) {
            selectedDifficulty = Difficulty.MEDIUM;
        } else {
            try {
                selectedDifficulty = Difficulty.valueOf(
                        difficulty.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Difficulty must be EASY, MEDIUM or HARD");
            }
        }

        // 8. Build AI prompt
        String prompt = """
                You are a technical interviewer.

                Generate one interview practice question for the following skill.

                Skill: %s
                Difficulty: %s

                Return ONLY valid JSON. Do not include Markdown or code fences.

                Use exactly this JSON structure:
                {
                  "question": "The interview question",
                  "expectedPoints": [
                    "Expected answer point 1",
                    "Expected answer point 2",
                    "Expected answer point 3"
                  ],
                  "hint": "A small hint without revealing the answer"
                }

                Requirements:
                1. Generate only one clear technical interview question.
                2. Match the requested difficulty.
                3. Expected points must contain 3 to 5 evaluation criteria.
                4. The hint must not reveal the complete answer.
                5. Return valid JSON only.
                """.formatted(
                        selectedSkill.getSkill(),
                        selectedDifficulty.name()
                );

        // 9. Generate using Gemini, fallback to Groq
        String aiResponse;

        try {
            System.out.println("STEP 7: Calling Gemini");
            aiResponse = llmProvider.generateContent(prompt);
            System.out.println("STEP 8: Gemini response received");
        } catch (Exception ex) {
            aiResponse = groqClient.generateContent(prompt);
        }

        if (aiResponse == null || aiResponse.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "AI providers returned an empty response");
        }

        // 10. Remove Markdown code fences if the AI adds them
        String cleanedResponse = aiResponse.trim();

        if (cleanedResponse.startsWith("```json")) {
            cleanedResponse = cleanedResponse.substring(7).trim();
        } else if (cleanedResponse.startsWith("```")) {
            cleanedResponse = cleanedResponse.substring(3).trim();
        }

        if (cleanedResponse.endsWith("```")) {
            cleanedResponse = cleanedResponse.substring(
                    0, cleanedResponse.length() - 3).trim();
        }

        // 11. Parse AI JSON
        Map<String, Object> aiData = objectMapper.readValue(
                cleanedResponse,
                new TypeReference<Map<String, Object>>() {}
        );

        String question = (String) aiData.get("question");
        String hint = (String) aiData.get("hint");

        if (question == null || question.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "AI did not generate a valid question");
        }

        List<String> expectedPoints = objectMapper.convertValue(
                aiData.get("expectedPoints"),
                new TypeReference<List<String>>() {}
        );

        if (expectedPoints == null || expectedPoints.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "AI did not generate expected answer points");
        }

        // 12. Create PracticeAttempt entity
        PracticeAttempt attempt = new PracticeAttempt();

        attempt.setAnalysis(analysis);
        attempt.setSkill(selectedSkill.getSkill());
        attempt.setQuestion(question);
        attempt.setDifficulty(selectedDifficulty);
        attempt.setExpectedPoints(
                objectMapper.writeValueAsString(expectedPoints));
        attempt.setHint(hint);
        attempt.setStatus(Status.GENERATED);

        // 13. Save in MySQL
        PracticeAttempt saved = prepo.save(attempt);

        // 14. Return response without expectedPoints
        return new PracticeQuestionResponse(
                saved.getId(),
                saved.getSkill(),
                saved.getQuestion(),
                saved.getDifficulty(),
                saved.getHint(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }

   
public PracticeFeedbackResponse submitanswer(
        Long attemptId, PracticeAnswerRequest request) {

    // 1. Retrieve the practice attempt
    PracticeAttempt attempt = prepo.findById(attemptId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Practice attempt not found"));
                    System.out.println("Reached");

    // 2. Prevent submitting an already evaluated attempt
    if (attempt.getStatus() != Status.GENERATED) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "This practice question has already been submitted");
    }

    String answer = request.getAnswer().trim();

    // 3. Save the user's answer
    attempt.setUserAnswer(answer);
    attempt.setSubmittedAt(LocalDateTime.now());
    attempt.setStatus(Status.SUBMITTED);
    prepo.save(attempt);

    // 4. Prepare the evaluation prompt
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

    // 5. Call Gemini, with Groq as fallback
    String aiResponse;

    try {
        aiResponse = llmProvider.generateContent(prompt);
    } catch (Exception geminiException) {
        try {
            aiResponse = groqClient.generateContent(prompt);
        } catch (Exception groqException) {
            attempt.setStatus(Status.FAILED);
            prepo.save(attempt);

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Both AI providers failed to evaluate the answer");
        }
    }

    if (aiResponse == null || aiResponse.isBlank()) {
        attempt.setStatus(Status.FAILED);
        prepo.save(attempt);

        throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "AI returned an empty evaluation");
    }

    // 6. Clean AI response
    String cleanedResponse = aiResponse.trim();

    if (cleanedResponse.startsWith("```json")) {
        cleanedResponse = cleanedResponse.substring(7).trim();
    } else if (cleanedResponse.startsWith("```")) {
        cleanedResponse = cleanedResponse.substring(3).trim();
    }

    if (cleanedResponse.endsWith("```")) {
        cleanedResponse = cleanedResponse.substring(
                0, cleanedResponse.length() - 3).trim();
    }

    // 7. Parse AI evaluation JSON
    Map<String, Object> feedbackData;

    try {
        feedbackData = objectMapper.readValue(
                cleanedResponse,
                new TypeReference<Map<String, Object>>() {});
    } catch (Exception ex) {
        attempt.setStatus(Status.FAILED);
        prepo.save(attempt);

        throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "AI returned an invalid evaluation format");
    }

    // 8. Extract evaluation fields
    List<String> coveredPoints = objectMapper.convertValue(
            feedbackData.get("coveredPoints"),
            new TypeReference<List<String>>() {});

    List<String> missingPoints = objectMapper.convertValue(
            feedbackData.get("missingPoints"),
            new TypeReference<List<String>>() {});

    String explanation = (String) feedbackData.get("explanation");

    List<String> nextSteps = objectMapper.convertValue(
            feedbackData.get("nextSteps"),
            new TypeReference<List<String>>() {});

    if (coveredPoints == null) coveredPoints = new ArrayList<>();
    if (missingPoints == null) missingPoints = new ArrayList<>();
    if (nextSteps == null) nextSteps = new ArrayList<>();

    if (explanation == null || explanation.isBlank()) {
        explanation = "Evaluation completed.";
    }

    // 9. Save feedback in the database
    attempt.setFeedback(
            objectMapper.writeValueAsString(feedbackData));

    attempt.setStatus(Status.EVALUATED);

    PracticeAttempt saved = prepo.save(attempt);

    // 10. Return feedback to frontend
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

}
