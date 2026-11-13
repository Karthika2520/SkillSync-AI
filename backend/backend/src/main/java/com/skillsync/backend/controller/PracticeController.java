
package com.skillsync.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.skillsync.backend.dto.PracticeAnswerRequest;
import com.skillsync.backend.dto.PracticeQuestionResponse;
import com.skillsync.backend.service.PracticeService;

import jakarta.validation.Valid;
import java.util.List;
import com.skillsync.backend.dto.PracticeAttemptResponse;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "http://localhost:5173")
public class PracticeController {

    private final PracticeService pservice;

    public PracticeController(PracticeService pservice) {
        this.pservice = pservice;
    }

    // Generate an interview question based on weak skills
    @PostMapping("/{analysisId}/practice")
public ResponseEntity<List<PracticeQuestionResponse>> generateQuestions(
        @PathVariable Long analysisId,
        @RequestParam(required = false) String difficulty) {

    return ResponseEntity.ok(
            pservice.generateQuestion(analysisId, difficulty));
}

   @PostMapping("/practice-attempts/{attemptId}/submit")
public ResponseEntity<?> answersubmission(
        @PathVariable("attemptId") Long attemptId,
        @Valid @RequestBody PracticeAnswerRequest req) {

    return ResponseEntity.ok(pservice.submitanswer(attemptId, req));
}
    // GET all practice attempts for an analysis
    @GetMapping("/{analysisId}/practice-attempts")
    public ResponseEntity<List<PracticeAttemptResponse>> getPracticeHistory(
            @PathVariable Long analysisId) {

        List<PracticeAttemptResponse> response =
                pservice.getPracticeHistory(analysisId);

        return ResponseEntity.ok(response);
    }


    // GET one practice attempt
    @GetMapping("/practice-attempts/{attemptId}")
    public ResponseEntity<PracticeAttemptResponse> getPracticeAttempt(
            @PathVariable Long attemptId) {

        PracticeAttemptResponse response =
                pservice.getPracticeAttempt(attemptId);

        return ResponseEntity.ok(response);
    }


}
