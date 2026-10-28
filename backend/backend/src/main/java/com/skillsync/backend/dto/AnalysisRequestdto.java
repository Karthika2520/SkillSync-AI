package com.skillsync.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//information that react only sends to backend
@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
public class AnalysisRequestdto 
{
    @NotNull (message = "Resume ID is required")
    @Positive (message = "Resume ID must be positive")
    private Long resumeId;

    @NotNull (message = "Job Description  ID is required")
    @Positive (message = "Job Description ID must be positive")
    private Long jobdescriptionId;


}
