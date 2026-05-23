package com.example.llminference.model;

/**
 * Record representing the performance metrics of the server.
 */
public record MetricsResponse(
        long totalRequests,
        long exactCacheHits,
        long semanticCacheHits,
        double averageLatency,
        double cacheHitRate,
        double estimatedSavings
) {
}
