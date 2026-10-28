package com.skillsync.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

}
