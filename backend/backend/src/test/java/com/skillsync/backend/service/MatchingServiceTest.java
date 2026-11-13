
package com.skillsync.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.skillsync.backend.dto.MatchingResult;
import com.skillsync.backend.dto.SkillEvidence;

class MatchingServiceTest {

    MatchingService matchingService = new MatchingService();

    // Test 1: Match skills
    @Test
    void testMatchedSkills() {

        List<SkillEvidence> resumeSkills = List.of(
                new SkillEvidence("Java", "I know Java"),
                new SkillEvidence("MySQL", "I used MySQL")
        );

        List<SkillEvidence> jobSkills = List.of(
                new SkillEvidence("Java", "Java required"),
                new SkillEvidence("MySQL", "MySQL required")
        );

        MatchingResult result =
                matchingService.calcuateMatch(resumeSkills, jobSkills);

        assertEquals(2, result.getMatchedSkills().size());
        assertTrue(result.getMissingSkills().isEmpty());
    }

    // Test 2: Identify missing skills
    @Test
    void testMissingSkills() {

        List<SkillEvidence> resumeSkills = List.of(
                new SkillEvidence("Java", "I know Java")
        );

        List<SkillEvidence> jobSkills = List.of(
                new SkillEvidence("Java", "Java required"),
                new SkillEvidence("Docker", "Docker required")
        );

        MatchingResult result =
                matchingService.calcuateMatch(resumeSkills, jobSkills);

        assertEquals(1, result.getMatchedSkills().size());
        assertEquals(1, result.getMissingSkills().size());
    }
}
