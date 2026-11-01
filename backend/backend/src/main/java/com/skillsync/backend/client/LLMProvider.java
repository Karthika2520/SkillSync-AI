package com.skillsync.backend.client;

public interface LLMProvider 
{
    String generateContent(String prompt);
}
