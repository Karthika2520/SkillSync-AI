package com.skillsync.backend.controller;

import com.skillsync.backend.service.GeminiSkillService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsync.backend.client.GeminiClient;
import com.skillsync.backend.dto.SkillEvidence;

@RestController 
@RequestMapping("/api/test")
public class testController 
{
    private final GeminiSkillService geminiSkillService;
    private final GeminiClient g;
    public testController(GeminiClient g, GeminiSkillService geminiSkillService)
    {
        this.g=g;
        this.geminiSkillService = geminiSkillService;
    }

    @GetMapping
    public String tester()
    {
        return g.generateContent("Say hello and introduce yourself in one sentence.");
    }
    @GetMapping("/skill")
    public List<SkillEvidence> getSkill()
    {
        String t="""
            Developed backend applications using Java and Spring Boot.
            Used Redis caching and Kafka messaging.
            """;
            return geminiSkillService.discoverSkills(t);
    }

}
