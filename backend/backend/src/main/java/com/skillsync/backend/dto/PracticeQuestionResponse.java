package com.skillsync.backend.dto;

import java.time.LocalDateTime;

import com.skillsync.backend.enums.Difficulty;
import com.skillsync.backend.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class PracticeQuestionResponse 
{
    private Long id;
    private String skill;
    private String question;
    private Difficulty difficulty;
    private String hint;
    private Status status;
    private LocalDateTime createdAt;

}
