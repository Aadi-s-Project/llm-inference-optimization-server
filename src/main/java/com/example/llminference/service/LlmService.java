package com.example.llminference.service;

import com.example.llminference.config.CacheConfig;
import com.example.llminference.model.LlmRequest;
import com.example.llminference.model.LlmResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

// This class contains the business logic for the LLM endpoint.
// Controller will call this service instead of doing the work itself.
@Service
public class LlmService {

    // For now, this method returns a mock response.
    // Later we will add semantic cache, batching, and OpenAI calls here.
    // @Cacheable automatically implements the Cache-Aside pattern:
    // 1. Check if 'prompt' exists in 'llm-responses' cache.
    // 2. If yes, return cached LlmResponse.
    // 3. If no, run this method and store result in cache.
    @Cacheable(value = CacheConfig.LLM_RESPONSES_CACHE, key = "#request.prompt()")
    public LlmResponse generate(LlmRequest request) {
        // This log only appears if there is a CACHE MISS.
        System.out.println("Cache Miss! Generating response for: " + request.prompt());
        
        return new LlmResponse("Mock response for: " + request.prompt());
    }
}