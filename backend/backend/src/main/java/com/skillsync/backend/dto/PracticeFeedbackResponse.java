
package com.skillsync.backend.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PracticeFeedbackResponse {

    private Long attemptId;
    private String skill;
    private String userAnswer;
    private List<String> coveredPoints;
    private List<String> missingPoints;
    private String explanation;
    private List<String> nextSteps;
    private String status;
}
