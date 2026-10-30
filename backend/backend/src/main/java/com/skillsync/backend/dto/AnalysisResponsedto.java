package com.skillsync.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.MatchResult;

import com.skillsync.backend.entity.Analysis.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 

public class AnalysisResponsedto 
{
    private Long id;
    private Long resumeId;
    private Long jobdescriptionId;
    private Status status;
    private BigDecimal matchScore;
    private LocalDateTime createdAt;
    
    private List<SkillEvidence> resumeSkills;
    private List<SkillEvidence> jobSkills;

    private MatchingResult matchingResult;

}
