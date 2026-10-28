package com.skillsync.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsync.backend.dto.AnalysisRequestdto;
import com.skillsync.backend.dto.AnalysisResponsedto;
import com.skillsync.backend.service.AnalysisService;

@RestController 
@RequestMapping("/api/analysis")
@CrossOrigin ("http://localhost:5173")
public class AnalysisController 
{
    private final AnalysisService ser;
    public AnalysisController(AnalysisService a)
    {
        this.ser=a;
    }

    @PostMapping
    public ResponseEntity<AnalysisResponsedto> createAnalysis(@RequestBody AnalysisRequestdto request)
    {
        AnalysisResponsedto d=ser.createAnalysis(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(d);
    }


}
