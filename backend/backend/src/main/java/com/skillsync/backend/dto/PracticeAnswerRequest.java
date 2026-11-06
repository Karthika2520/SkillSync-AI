package com.skillsync.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class PracticeAnswerRequest 
{
    @NotBlank 
    @Size(max=10000,message = "Answer cannot exceeds 10000 characters")
    private String answer;
}
