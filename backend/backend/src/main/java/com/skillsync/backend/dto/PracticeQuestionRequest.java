package com.skillsync.backend.dto;

import com.skillsync.backend.enums.Difficulty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class PracticeQuestionRequest 
{
    private Difficulty difficulty=Difficulty.MEDIUM;

}
