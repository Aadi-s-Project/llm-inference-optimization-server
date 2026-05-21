package com.example.llminference.service;

import com.example.llminference.config.CacheConfig;
import com.example.llminference.model.LlmRequest;
import com.example.llminference.model.LlmResponse;
import com.example.llminference.model.SemanticCacheResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

// This class contains the business logic for the LLM endpoint.
// Controller will call this service instead of doing the work itself.
@Service
public class LlmService {

    private final SemanticCacheClient semanticCacheClient;
    private final RequestBatcher requestBatcher;
    private final MetricsService metricsService;

    public LlmService(SemanticCacheClient semanticCacheClient, RequestBatcher requestBatcher, MetricsService metricsService) {
        this.semanticCacheClient = semanticCacheClient;
        this.requestBatcher = requestBatcher;
        this.metricsService = metricsService;
    }

    // Exact Cache (Stage 5)
    @Cacheable(value = CacheConfig.LLM_RESPONSES_CACHE, key = "#request.prompt()")
    public CompletableFuture<LlmResponse> generate(LlmRequest request) {
        long startTime = System.currentTimeMillis();
        System.out.println("Exact Cache Miss! Checking Semantic Cache for: " + request.prompt());

        // 1. Check Semantic Cache (Stage 6)
        SemanticCacheResponse semanticHit = semanticCacheClient.search(request.prompt());

        if (semanticHit != null && semanticHit.hit()) {
            System.out.println("Semantic Cache HIT! Match found for: " + semanticHit.originalPrompt());
            long latency = System.currentTimeMillis() - startTime;
            metricsService.recordRequest(latency, false, true);
            metricsService.printSummary();
            return CompletableFuture.completedFuture(new LlmResponse(semanticHit.response()));
        }

        // 2. If both miss, send to Request Batcher (Stage 7)
        System.out.println("Semantic Cache Miss! Adding to Request Batcher.");
        
        return requestBatcher.submit(request).thenApply(response -> {
            // 3. Store in Semantic Cache for next time
            semanticCacheClient.add(request.prompt(), response.answer());
            
            long latency = System.currentTimeMillis() - startTime;
            metricsService.recordRequest(latency, false, false);
            metricsService.printSummary();
            return response;
        });
    }
}