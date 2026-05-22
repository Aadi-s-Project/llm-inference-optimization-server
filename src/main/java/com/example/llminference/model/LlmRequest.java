package com.example.llminference.model;

/**
 * Represents the incoming LLM request.
 * @param provider "openai", "gemini", or "claude"
 */
public record LlmRequest(
        String userId,
        String prompt,
        String provider
) {
    // Provide a default constructor or use a default value if provider is null
    public LlmRequest {
        if (provider == null) {
            provider = "openai";
        }
    }
}