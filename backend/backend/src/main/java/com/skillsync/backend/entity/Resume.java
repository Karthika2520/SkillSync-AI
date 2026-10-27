package com.skillsync.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name="resume")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class Resume 
{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;
    
    @Column(nullable = false)
    private String contentType;
    

    @Lob  // large object->tells the database this field will going to store large text 
    @Column (columnDefinition = "LONGTEXT") // forces the db to use largetext datatype for this field...normal text will stores 255 characers where longtext will stores 4gb of text
    private String extractedText;

    private LocalDateTime uploadedAt;

    @PrePersist 
    public void PrePersist() //jpa lifecycle hook->it will when file uploaded or new row inserted into table
    {
        uploadedAt=LocalDateTime.now();
    }



}
