package com.skillsync.backend.dto;

import java.time.LocalDateTime;

import com.skillsync.backend.enums.TaskPriority;
import com.skillsync.backend.enums.TaskStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapTaskResponse {

    private Long id;
    private Long analysisId;
    private String title;
    private String description;
    private String skill;
    private TaskPriority priority;
    private Integer dayNumber;
    private TaskStatus status;
    private Integer estimatedMinutes;
    private String completionCriteria;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}