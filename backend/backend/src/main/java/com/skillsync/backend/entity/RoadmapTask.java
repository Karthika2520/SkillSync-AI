package com.skillsync.backend.entity;

import java.time.LocalDateTime;

import com.skillsync.backend.enums.TaskPriority;
import com.skillsync.backend.enums.TaskStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name="roadmap")
@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
public class RoadmapTask 
{
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn (name="analysis_id",nullable = false)
    private Analysis analysis;
    
    @Column(nullable = false)
    private String title;

    @Column (columnDefinition = "TEXT")
    private String description;

    private String skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @Column(nullable = false)
    private Integer dayNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(nullable = false)
    private Integer estimatedMinutes;

    @Column(columnDefinition = "TEXT")
    private String completionCriteria;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();

        if (status == null) {
            status = TaskStatus.TODO;
        }

        if (priority == null) {
            priority = TaskPriority.MEDIUM;
        }
    }



    

}
