
package com.skillsync.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.skillsync.backend.dto.SkillEvidence;

class SkillExtractionServiceTest {

    private GeminiSkillService geminiSkillService;
    private GroqSkillService groqSkillService;
    private SkillExtractionService skillExtractionService;

    @BeforeEach
    void setUp() {
        geminiSkillService = mock(GeminiSkillService.class);
        groqSkillService = mock(GroqSkillService.class);

        skillExtractionService = new SkillExtractionService(
                geminiSkillService,
                groqSkillService
        );
    }

    // Test 1: Extract skills
    @Test
    void testExtractSkills() {

        when(geminiSkillService.discoverSkills(anyString()))
                .thenReturn(List.of());

        when(groqSkillService.discoverSkills(anyString()))
                .thenReturn(List.of());

        List<SkillEvidence> result =
                skillExtractionService.extractSkills(
                        "I know Java, Python and Spring Boot."
                );

        assertFalse(result.isEmpty());
    }

    // Test 2: Empty input
    @Test
    void testEmptyInput() {

        List<SkillEvidence> result =
                skillExtractionService.extractSkills("");

        assertTrue(result.isEmpty());
    }

    // Test 3: Gemini failure, fallback to Groq
    @Test
    void testGeminiFailure() {

        when(geminiSkillService.discoverSkills(anyString()))
                .thenThrow(new RuntimeException("Gemini failed"));

        when(groqSkillService.discoverSkills(anyString()))
                .thenReturn(List.of(
                        new SkillEvidence("Docker", "Used Docker")
                ));

        List<SkillEvidence> result =
                skillExtractionService.extractSkills(
                        "I know Java."
                );

        assertTrue(result.stream()
                .anyMatch(skill -> skill.getSkill().equals("Docker")));
    }
}
