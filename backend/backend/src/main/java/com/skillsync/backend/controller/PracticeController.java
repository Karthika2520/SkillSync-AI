
package com.skillsync.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.skillsync.backend.dto.PracticeAnswerRequest;
import com.skillsync.backend.dto.PracticeQuestionResponse;
import com.skillsync.backend.service.PracticeService;

import jakarta.validation.Valid;

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
    public ResponseEntity<PracticeQuestionResponse> generateQuestion(
            @PathVariable Long analysisId,
            @RequestParam(required = false) String difficulty) {

        PracticeQuestionResponse response =
                pservice.generateQuestion(analysisId, difficulty);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/practice-attempt/{analysisId}/submit")
    public ResponseEntity<?> answersubmission(@PathVariable Long id,@Valid  @RequestBody PracticeAnswerRequest req)
    {
        return ResponseEntity.ok(pservice.submitanswer(id,req));
    }
}
