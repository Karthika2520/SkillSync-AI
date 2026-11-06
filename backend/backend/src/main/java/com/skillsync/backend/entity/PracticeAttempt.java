package com.skillsync.backend.entity;

import java.time.LocalDateTime;
import com.skillsync.backend.enums.Difficulty;
import com.skillsync.backend.enums.Status;

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
@Table(name="practiceattempt")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class PracticeAttempt 
{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn (name="analysis_id",nullable = false)
    private Analysis analysis;

    @Column(nullable = false)
    private String skill;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String question;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
    
    @Column(columnDefinition = "TEXT")
    private String expectedPoints;
    
    @Column (columnDefinition="TEXT")
    private String hint;
    @Column (columnDefinition="TEXT")
    private String userAnswer=null;
    @Column (columnDefinition="TEXT")
    private String feedback=null;

    @Enumerated (EnumType.STRING)
    private Status status=Status.GENERATED;
    
    @Column (nullable = false,updatable=false)
    private LocalDateTime createdAt;

    
    private LocalDateTime submittedAt;

    @PrePersist 
    public void PrePersist()
    {
        createdAt=LocalDateTime.now();
        status=Status.GENERATED;
    }






}
