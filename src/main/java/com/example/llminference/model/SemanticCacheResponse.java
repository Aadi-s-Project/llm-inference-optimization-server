package com.example.llminference.model;

/**
 * Represents the response from the Python Semantic Cache service.
 */
public record SemanticCacheResponse(
        boolean hit,
        String response,
        String originalPrompt,
        double distance
) {
}
