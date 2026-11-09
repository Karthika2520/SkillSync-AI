
package com.skillsync.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.skillsync.backend.dto.AnalysisRequestdto;
import com.skillsync.backend.dto.AnalysisResponsedto;
import com.skillsync.backend.service.AnalysisService;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisService ser;

    public AnalysisController(AnalysisService a) {
        this.ser = a;
    }

    @PostMapping
    public ResponseEntity<AnalysisResponsedto> createAnalysis(
            @RequestBody AnalysisRequestdto request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Please login first"
            );
        }

        AnalysisResponsedto d = ser.createAnalysis(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(d);
    }
    // Get all previous analyses
@GetMapping
public ResponseEntity<List<AnalysisResponsedto>> getAllAnalyses() {

    return ResponseEntity.ok(
            ser.getAllAnalyses()
    );
}


// Get one previous analysis by ID
@GetMapping("/{analysisId}")
public ResponseEntity<AnalysisResponsedto> getAnalysisById(
        @PathVariable Long analysisId) {

    return ResponseEntity.ok(
            ser.getAnalysisById(analysisId)
    );
}
}
