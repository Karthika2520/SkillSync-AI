
package com.skillsync.backend.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GroqClient implements LLMProvider {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GroqClient(
            RestClient.Builder builder,
            @Value("${groq.api.key}") String apiKey,
            @Value("${groq.api.model}") String model) {

        this.restClient = builder
                .baseUrl("https://api.groq.com/openai/v1")
                .build();

        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public String generateContent(String prompt) {

        Map<String, Object> reqBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        try {

            Map<?, ?> response = restClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(reqBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                throw new IllegalStateException(
                        "Empty response from Groq");
            }

            Object choicesObject = response.get("choices");

            if (!(choicesObject instanceof List<?> choices)
                    || choices.isEmpty()) {
                throw new IllegalStateException(
                        "Groq returned no choices");
            }

            Object messageObject =
                    ((Map<?, ?>) choices.get(0)).get("message");

            if (!(messageObject instanceof Map<?, ?> message)) {
                throw new IllegalStateException(
                        "Groq response message is missing");
            }

            Object contentObject = message.get("content");

            if (!(contentObject instanceof String generatedText)
                    || generatedText.isBlank()) {
                throw new IllegalStateException(
                        "Groq generated an empty response");
            }

            return generatedText;

        } catch (org.springframework.web.client.RestClientResponseException e) {

            System.err.println("Groq API HTTP Status: " + e.getStatusCode());
            System.err.println("Groq API Error: " + e.getResponseBodyAsString());

            throw e;
        }
    }
}
