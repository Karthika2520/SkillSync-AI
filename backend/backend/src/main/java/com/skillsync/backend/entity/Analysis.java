package com.skillsync.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table (name="analysis")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class Analysis 
{
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY,optional=false)
    @JoinColumn (name="resume_id",nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)//fetch=lazt it helps->it doent allow all related resumes to fetch ..it will fetch only when user specifically ask
    @JoinColumn (name="job_description_id",nullable = false)
    private JobDescription job;

    public enum Status{
        PENDING,
        COMPLETED,
        FAILED
    };
    @Enumerated (EnumType.STRING)
    @Column (nullable = false)
    private Status status;

    @Column (precision = 5,scale = 2) //precision->total no. of digits,scale=digits after decimal
    private BigDecimal matchScore;

    @Column (columnDefinition = "TEXT")
    private String resultsJson;

    @Column (nullable = false,updatable=false)
    private LocalDateTime createdAt;

    @PrePersist 
    public void PrePersist()
    {
        createdAt=LocalDateTime.now();
        if(status==null)
        {
            status=Status.PENDING;
        }
    }
}
