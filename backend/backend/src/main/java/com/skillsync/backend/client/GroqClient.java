
package com.skillsync.backend.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class GroqClient implements LLMProvider {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GroqClient(
            RestClient.Builder builder,
            @Qualifier("llmRequestFactory")
            ClientHttpRequestFactory requestFactory,
            @Value("${groq.api.key}") String apiKey,
            @Value("${groq.api.model}") String model) {

        this.restClient = builder
                .baseUrl("https://api.groq.com/openai/v1")
                .requestFactory(requestFactory)
                .build();

        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public String generateContent(String prompt) {

        long start = System.currentTimeMillis();

        Map<String, Object> reqBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),
                "max_completion_tokens", 2048,
                "reasoning_effort", "low",
                "include_reasoning", false
        );

        try {

            System.out.println("Calling Groq API...");
            System.out.println("Groq Model: " + model);

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

            Map<?, ?> choice = (Map<?, ?>) choices.get(0);

            Object messageObject = choice.get("message");

            if (!(messageObject instanceof Map<?, ?> message)) {

                throw new IllegalStateException(
                        "Groq response message is missing");
            }

            Object contentObject = message.get("content");

            if (!(contentObject instanceof String generatedText)
                    || generatedText.isBlank()) {

                System.err.println("Groq message: " + message);
                System.err.println(
                        "Groq finish reason: " + choice.get("finish_reason"));

                throw new IllegalStateException(
                        "Groq generated an empty response");
            }

            System.out.println(
                    "Groq response received successfully.");

            return generatedText;

        } catch (RestClientResponseException e) {

            System.err.println(
                    "Groq HTTP Status: " + e.getStatusCode());

            System.err.println(
                    "Groq API Error: " + e.getResponseBodyAsString());

            throw e;

        } finally {

            long end = System.currentTimeMillis();

            System.out.println(
                    "Groq response time: " + (end - start) + " ms");
        }
    }
}
