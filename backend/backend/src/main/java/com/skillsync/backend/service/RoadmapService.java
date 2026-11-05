package com.skillsync.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.skillsync.backend.dto.MatchingResult;
import com.skillsync.backend.dto.RoadmapTaskResponse;
import com.skillsync.backend.dto.SkillEvidence;
import com.skillsync.backend.dto.SkillMatchItem;

import com.skillsync.backend.entity.Analysis;
import com.skillsync.backend.entity.RoadmapTask;

import com.skillsync.backend.enums.TaskPriority;
import com.skillsync.backend.enums.TaskStatus;

import com.skillsync.backend.repository.AnalysisRepository;
import com.skillsync.backend.repository.RoadmapRepository;

@Service
public class RoadmapService {

    private final AnalysisRepository arepo;
    private final RoadmapRepository rrepo;
    private final SkillExtractionService skillser;
    private final MatchingService mser;

    public RoadmapService(
            AnalysisRepository arepo,
            RoadmapRepository rrepo,
            SkillExtractionService skillser,
            MatchingService mser) {

        this.arepo = arepo;
        this.rrepo = rrepo;
        this.skillser = skillser;
        this.mser = mser;
    }

    // Generate roadmap
    @Transactional
    public List<RoadmapTaskResponse> generateRoadmap(
            Long analysisId, boolean replace) {

        Analysis ana = arepo.findById(analysisId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Analysis not found"));

        boolean alreadyExists =
                rrepo.existsByAnalysis_Id(analysisId);

        if (alreadyExists && !replace) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A roadmap already exists. Confirm replacement to generate a new one.");
        }

        List<SkillEvidence> resumeskills =
                skillser.extractSkills(ana.getResume().getExtractedText());

        List<SkillEvidence> jobskills =
                skillser.extractSkills(ana.getJob().getDescription());

        MatchingResult res =
                mser.calcuateMatch(resumeskills, jobskills);

        if (res.getMissingSkills().isEmpty()
                && res.getPartialSkills().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No skill gaps found. A preparation roadmap is not required");
        }

        if (alreadyExists) {
            rrepo.deleteByAnalysis_Id(analysisId);
        }

        List<RoadmapTask> tasks = new ArrayList<>();

        // Create HIGH priority tasks for missing skills
        for (SkillMatchItem s : res.getMissingSkills()) {

            tasks.add(createSkillTask(
                    ana,
                    s.getSkill(),
                    TaskPriority.HIGH,
                    "Learn the fundamentals of " + s.getSkill()
                            + ". Study the core concepts, understand practical examples, "
                            + "and solve practice questions.",
                    "Explain the main concepts of " + s.getSkill()
                            + " and solve at least 3 related questions.",
                    90,
                    tasks.size() + 1
            ));
        }

        // Create MEDIUM priority tasks for partially matched skills
        for (SkillMatchItem s : res.getPartialSkills()) {

            tasks.add(createSkillTask(
                    ana,
                    s.getSkill(),
                    TaskPriority.MEDIUM,
                    "Strengthen your understanding of " + s.getSkill()
                            + ". Revise important concepts, practise implementation, "
                            + "and identify areas where your knowledge is incomplete.",
                    "Complete 2 practical exercises and explain the topic "
                            + "in an interview-style answer.",
                    60,
                    tasks.size() + 1
            ));
        }

        // General revision tasks
        String[] revisionTitles = {
                "Apply Your Skills",
                "Solve Coding Problems",
                "Build a Mini Implementation",
                "Revise Important Concepts",
                "Practice Technical Questions",
                "Mock Interview",
                "Final Revision and Self Assessment"
        };

        String[] revisionDescriptions = {
                "Apply the skills studied so far through a small practical exercise.",
                "Solve coding or technical questions related to your identified skill gaps.",
                "Build a small implementation using the skills covered in this roadmap.",
                "Review your notes and revisit concepts that were difficult.",
                "Answer technical interview questions related to your preparation topics.",
                "Conduct a mock interview and explain your solutions aloud.",
                "Review your progress and identify any remaining weak areas."
        };

        String[] revisionCriteria = {
                "Complete one practical exercise.",
                "Solve at least 3 practice questions.",
                "Complete and test one small implementation.",
                "Summarize the important concepts in your own words.",
                "Answer at least 5 technical questions.",
                "Complete a 20-minute mock interview.",
                "Review all seven days and list the concepts you can explain confidently."
        };

        // Identify days that already have skill tasks
        boolean[] dayHasTask = new boolean[8];

        for (RoadmapTask t : tasks) {

            if (t.getDayNumber() <= 7) {
                dayHasTask[t.getDayNumber()] = true;
            }
        }

        int revisionId = 0;

        // Fill remaining days with general revision tasks
        for (int i = 1; i <= 7; i++) {

            if (!dayHasTask[i]) {

                RoadmapTask task = new RoadmapTask();

                task.setAnalysis(ana);
                task.setTitle(revisionTitles[revisionId]);
                task.setDescription(revisionDescriptions[revisionId]);
                task.setSkill("General Preparation");
                task.setPriority(TaskPriority.MEDIUM);
                task.setDayNumber(i);
                task.setStatus(TaskStatus.TODO);
                task.setEstimatedMinutes(60);
                task.setCompletionCriteria(revisionCriteria[revisionId]);

                tasks.add(task);

                revisionId++;
            }
        }

        // Save tasks and convert them into DTOs
        List<RoadmapTask> savedTasks = rrepo.saveAll(tasks);

        return savedTasks.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Create an individual skill task
    private RoadmapTask createSkillTask(
            Analysis analysis,
            String skill,
            TaskPriority priority,
            String description,
            String completionCriteria,
            int estimatedMinutes,
            int taskNumber) {

        RoadmapTask task = new RoadmapTask();

        task.setAnalysis(analysis);
        task.setTitle("Improve " + skill);
        task.setDescription(description);
        task.setSkill(skill);
        task.setPriority(priority);
        task.setDayNumber(((taskNumber - 1) % 7) + 1);
        task.setStatus(TaskStatus.TODO);
        task.setEstimatedMinutes(estimatedMinutes);
        task.setCompletionCriteria(completionCriteria);

        return task;
    }

    // Convert RoadmapTask entity into response DTO
    private RoadmapTaskResponse convertToResponse(RoadmapTask task) {

        return new RoadmapTaskResponse(
                task.getId(),
                task.getAnalysis().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getSkill(),
                task.getPriority(),
                task.getDayNumber(),
                task.getStatus(),
                task.getEstimatedMinutes(),
                task.getCompletionCriteria(),
                task.getCreatedAt(),
                task.getCompletedAt()
        );
    }

    // Get all roadmap tasks for an analysis
    @Transactional(readOnly = true)
    public List<RoadmapTaskResponse> getRoadmap(Long id) {

        return rrepo.findByAnalysis_IdOrderByDayNumberAscIdAsc(id)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Update roadmap task status
    @Transactional
    public RoadmapTaskResponse updateTaskStatus(
            Long taskId, TaskStatus newStatus) {

        RoadmapTask task = rrepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Roadmap task not found"));

        task.setStatus(newStatus);

        if (newStatus == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }

        RoadmapTask updatedTask = rrepo.save(task);

        return convertToResponse(updatedTask);
    }
}