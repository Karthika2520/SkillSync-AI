package com.skillsync.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name="job_description")
@Getter
@Setter 
@AllArgsConstructor
@NoArgsConstructor 
public class JobDescription 
{
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String title;

    private String company;

    @Column (nullable = false,columnDefinition = "TEXT")
    private String description;

    private LocalDateTime createdAt;
    @PrePersist 
    public void onCreate()
    {
        createdAt=LocalDateTime.now();
    }

   }
