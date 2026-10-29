package com.skillsync.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.skillsync.backend.dto.SkillEvidence;
import com.skillsync.backend.service.SkillExtractionService;

public class SkillExtractionServiceTest 
{
      private final SkillExtractionService service =
            new SkillExtractionService();

     @Test
    void extractsSkillsAndAliases() {

        String text = "I use JAVA, JS, Spring Boot and MySQL.";

        List<SkillEvidence> results =
                service.extractSkills(text);

        List<String> skills = results.stream()
                .map(SkillEvidence::getSkill)
                .collect(Collectors.toList());

        assertTrue(skills.contains("Java"));
        assertTrue(skills.contains("JavaScript"));
        assertTrue(skills.contains("Spring Boot"));
        assertTrue(skills.contains("MySQL"));

        assertEquals(
                skills.size(),
                skills.stream().distinct().count()
        );
    }

     @Test
    void doesNotMatchSkillsInsideOtherWords() {

        String text =
                "I develop JavaScript applications and use NoSQL.";

        List<SkillEvidence> results =
                service.extractSkills(text);

        List<String> skills = results.stream()
                .map(SkillEvidence::getSkill)
                .collect(Collectors.toList());

        assertTrue(skills.contains("JavaScript"));
        assertFalse(skills.contains("Java"));
        assertFalse(skills.contains("SQL"));
    }

    @Test
    void handlesEmptyText() {
        assertTrue(service.extractSkills("").isEmpty());
        assertTrue(service.extractSkills(null).isEmpty());
    }

     @Test
    void retainsEvidence() {

        List<SkillEvidence> results =
                service.extractSkills(
                        "I built an application using Spring Boot."
                );

        SkillEvidence skill = results.stream()
                .filter(s -> s.getSkill().equals("Spring Boot"))
                .findFirst()
                .orElseThrow();

        assertTrue(
                skill.getEvidence().contains("Spring Boot")
        );
    }
}






