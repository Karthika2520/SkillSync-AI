
package com.skillsync.backend.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@Primary
public class GeminiClient implements LLMProvider {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiClient(
            RestClient.Builder builder,
            @Qualifier("llmRequestFactory")
            ClientHttpRequestFactory requestFactory,
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.model}") String model) {

        this.restClient = builder
                .baseUrl("https://generativelanguage.googleapis.com")
                .requestFactory(requestFactory)
                .build();

        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public String generateContent(String prompt) {

        long start = System.currentTimeMillis();

        Map<String, Object> reqBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                ),
                "generationConfig", Map.of(
                        "maxOutputTokens", 512,
                        "responseMimeType", "application/json"
                )
        );

        try {

            System.out.println("Calling Gemini API...");
            System.out.println("Gemini Model: " + model);

            Map<?, ?> response = restClient.post()
                    .uri("/v1beta/models/" + model + ":generateContent")
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(reqBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                throw new IllegalStateException(
                        "Empty response from Gemini");
            }

            Object candidate = response.get("candidates");

            if (!(candidate instanceof List<?> candidates)
                    || candidates.isEmpty()) {

                throw new IllegalStateException(
                        "Gemini returned no candidates");
            }

            Object content =
                    ((Map<?, ?>) candidates.get(0)).get("content");

            if (!(content instanceof Map<?, ?> contents)) {

                throw new IllegalStateException(
                        "Gemini response content is missing");
            }

            Object partsObject = contents.get("parts");

            if (!(partsObject instanceof List<?> parts)
                    || parts.isEmpty()) {

                throw new IllegalStateException(
                        "Gemini response parts are missing");
            }

            Object textObject =
                    ((Map<?, ?>) parts.get(0)).get("text");

            if (!(textObject instanceof String generatedText)
                    || generatedText.isBlank()) {

                throw new IllegalStateException(
                        "Gemini generated an empty response");
            }

            System.out.println("Gemini response received successfully.");

            return generatedText;

        } catch (RestClientResponseException e) {

            System.err.println(
                    "Gemini HTTP Status: " + e.getStatusCode());

            System.err.println(
                    "Gemini API Error: " + e.getResponseBodyAsString());

            throw e;

        } catch (Exception e) {

            System.err.println(
                    "Gemini connection/request failed: "
                            + e.getClass().getName());

            System.err.println(
                    "Gemini failure message: " + e.getMessage());

            throw e;

        } finally {

            long end = System.currentTimeMillis();

            System.out.println(
                    "Gemini response time: " + (end - start) + " ms");
        }
    }
}
