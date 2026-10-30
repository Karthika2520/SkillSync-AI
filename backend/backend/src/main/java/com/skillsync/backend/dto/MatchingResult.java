package com.skillsync.backend.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class MatchingResult 
{
    private BigDecimal matchScore;
    private List<SkillMatchItem> matchedSkills;
    private List<SkillMatchItem> partialSkills;
    private List<SkillMatchItem> missingSkills;
    private String summary;   

}
