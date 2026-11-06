package com.skillsync.backend.client;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Primary;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Primary //telle the spring to manages bean creation & manages its lifecycle in ioc
public class GeminiClient implements LLMProvider
{
  
    private final RestClient restClient; 
    //RestClient is a Spring framework class used to communicate with external APIs using HTTP.
    private final String apiKey;
    private final String model;

    public GeminiClient(RestClient.Builder builder,@Value("${gemini.api.key}") String apiKey,@Value("${gemini.api.model:gemini-3.8-flash}") String model)
    {
        this.restClient=builder.baseUrl("https://generativelanguage.googleapis.com").build();
        this.apiKey=apiKey;
        this.model=model;
    }
    public String generateContent(String prompt)
    {
        Map<String,Object> reqBody=Map.of(
            "contents",List.of(
                Map.of("parts",List.of(Map.of("text",prompt)))
            )
        );
        try{

        Map<?,?> response=restClient.post()
         .uri("/v1beta/models/" + model + ":generateContent")
         .header("x-goog-api-key",apiKey)
         .contentType(MediaType.APPLICATION_JSON)
         .body(reqBody)
         .retrieve()
         .body(Map.class);

         if(response==null)
         {
            throw new IllegalStateException("Empty response from Gemini");
         }

         Object candidate=response.get("candidates");

         if(!(candidate instanceof List<?> candidates)||candidates.isEmpty())
         {
            throw new IllegalStateException("Gemini returned no candidates");

         }

         Object content=((Map<?,?>) candidates.get(0)).get("content");
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

        return generatedText;
    }
    catch (org.springframework.web.client.RestClientResponseException e) {
    System.err.println("Gemini API HTTP Status: " + e.getStatusCode());
    System.err.println("Gemini API Error: " + e.getResponseBodyAsString());
    throw e;
}

    }
}