package com.skillsync.backend.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsync.backend.entity.JobDescription;
import com.skillsync.backend.service.JobDescriptionService;

@RestController 
@RequestMapping("/api/jobs")
@CrossOrigin (origins="http://localhost:5173")
public class JobDescriptionController 
{
    private final JobDescriptionService ser;
    public JobDescriptionController(JobDescriptionService ser)
    {
        this.ser=ser;
    }

    @GetMapping
    public List<JobDescription> getAlljobs()
    {
        return ser.getall();
    }

    @PostMapping
    public ResponseEntity<JobDescription> createjob(@RequestBody JobDescription job)
    {
        JobDescription j=ser.create(job);
        return ResponseEntity.ok(j);
        
    }



}
