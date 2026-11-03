
package com.skillsync.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.skillsync.backend.entity.JobDescription;
import com.skillsync.backend.service.JobDescriptionService;

@RestController
@RequestMapping("/api/jobs")
public class JobDescriptionController {

    private final JobDescriptionService ser;

    public JobDescriptionController(JobDescriptionService ser) {
        this.ser = ser;
    }

    @GetMapping
    public ResponseEntity<List<JobDescription>> getAlljobs(
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Please login first"
            );
        }

        return ResponseEntity.ok(ser.getall());
    }

    @PostMapping
    public ResponseEntity<JobDescription> createjob(
            @RequestBody JobDescription job,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Please login first"
            );
        }

        if (job.getTitle() == null || job.getTitle().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Job title is required"
            );
        }

        JobDescription j = ser.create(job);

        return ResponseEntity.status(HttpStatus.CREATED).body(j);
    }
}
