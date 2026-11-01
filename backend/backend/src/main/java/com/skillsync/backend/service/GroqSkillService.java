
package com.skillsync.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skillsync.backend.client.GroqClient;
import com.skillsync.backend.dto.SkillEvidence;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class GroqSkillService {

    private final GroqClient groqClient;
    private final ObjectMapper objectMapper;

    public GroqSkillService(
            GroqClient groqClient,
            ObjectMapper objectMapper) {

        this.groqClient = groqClient;
        this.objectMapper = objectMapper;
    }

    public List<SkillEvidence> discoverSkills(String text) {

        List<SkillEvidence> skills = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return skills;
        }

        String prompt = """
                Identify all technical skills explicitly mentioned
                in the following text.

                Return ONLY a valid JSON array.

                Each object must contain:
                - skill: technical skill name
                - evidence: exact sentence from the input supporting it

                Rules:
                1. Include only skills supported by the input.
                2. Do not invent skills.
                3. Do not include explanations outside the JSON.
                4. Do not return duplicate skills.
                5. If no skills are found, return [].

                Example:
                [
                  {
                    "skill": "Redis",
                    "evidence": "Used Redis caching."
                  }
                ]

                Text:
                """ + text;

        String response = groqClient.generateContent(prompt);

        try {

            JsonNode root = objectMapper.readTree(response);

            if (root == null || !root.isArray()) {
                throw new IllegalStateException(
                        "Groq response is not a JSON array");
            }

            for (JsonNode node : root) {

                if (!node.hasNonNull("skill")
                        || !node.hasNonNull("evidence")) {
                    continue;
                }

                String skill = node.get("skill").asText().trim();
                String evidence = node.get("evidence").asText().trim();

                if (!skill.isEmpty() && !evidence.isEmpty()) {

                    skills.add(new SkillEvidence(skill, evidence));
                }
            }

            return skills;

        } catch (JacksonException e) {

            throw new IllegalStateException(
                    "Failed to parse Groq skill response", e);
        }
    }
}
