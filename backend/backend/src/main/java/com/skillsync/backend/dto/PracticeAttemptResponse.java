
package com.skillsync.backend.dto;

import java.time.LocalDateTime;

import com.skillsync.backend.enums.Difficulty;
import com.skillsync.backend.enums.Status;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PracticeAttemptResponse {

    private Long id;
    private Long analysisId;
    private String skill;
    private String question;
    private Difficulty difficulty;
    private String hint;
    private String userAnswer;
    private String feedback;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;
}
