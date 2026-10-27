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
@Table (name="savedjob")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class savedJob 
{
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column (nullable = false)
    private String title;

    private String company;
    private String joburl;

    @Column (length=10000)
    private String description;

    private LocalDateTime savedAt;

    @PrePersist 
    public void PrePersist()
    {
        savedAt=LocalDateTime.now();
    }


}
