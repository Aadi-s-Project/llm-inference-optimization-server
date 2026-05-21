package com.example.llminference.service;

import com.example.llminference.config.CacheConfig;
import com.example.llminference.model.LlmRequest;
import com.example.llminference.model.LlmResponse;
import com.example.llminference.model.SemanticCacheResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

// This class contains the business logic for the LLM endpoint.
// Controller will call this service instead of doing the work itself.
@Service
public class LlmService {

    private final SemanticCacheClient semanticCacheClient;

    public LlmService(SemanticCacheClient semanticCacheClient) {
        this.semanticCacheClient = semanticCacheClient;
    }

    // Exact Cache (Stage 5)
    @Cacheable(value = CacheConfig.LLM_RESPONSES_CACHE, key = "#request.prompt()")
    public LlmResponse generate(LlmRequest request) {
        System.out.println("Exact Cache Miss! Checking Semantic Cache for: " + request.prompt());

        // 1. Check Semantic Cache (Stage 6)
        SemanticCacheResponse semanticHit = semanticCacheClient.search(request.prompt());

        if (semanticHit != null && semanticHit.hit()) {
            System.out.println("Semantic Cache HIT! Match found for: " + semanticHit.originalPrompt());
            return new LlmResponse(semanticHit.response());
        }

        // 2. If both miss, generate response (Stage 2/3)
        System.out.println("Semantic Cache Miss! Generating fresh response.");
        String generatedResponse = "Mock response for: " + request.prompt();
        LlmResponse finalResponse = new LlmResponse(generatedResponse);

        // 3. Store in Semantic Cache for next time
        semanticCacheClient.add(request.prompt(), generatedResponse);

        return finalResponse;
    }
}