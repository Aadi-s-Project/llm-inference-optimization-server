package com.example.llminference.service;

import com.example.llminference.model.MetricsResponse;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Service to track performance metrics of the optimization pipeline.
 */
@Service
public class MetricsService {

    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong exactCacheHits = new AtomicLong(0);
    private final AtomicLong semanticCacheHits = new AtomicLong(0);
    private final AtomicLong totalLatencyMs = new AtomicLong(0);

    public void recordRequest(long latencyMs, boolean exactHit, boolean semanticHit) {
        totalRequests.incrementAndGet();
        totalLatencyMs.addAndGet(latencyMs);
        if (exactHit) exactCacheHits.incrementAndGet();
        if (semanticHit) semanticCacheHits.incrementAndGet();
    }

    public MetricsResponse getMetrics() {
        long total = totalRequests.get();
        long exact = exactCacheHits.get();
        long semantic = semanticCacheHits.get();
        double avgLatency = total == 0 ? 0 : (double) totalLatencyMs.get() / total;
        double hitRate = total == 0 ? 0 : (double) (exact + semantic) / total * 100;
        double savings = (exact + semantic) * 0.02;

        return new MetricsResponse(
                total,
                exact,
                semantic,
                avgLatency,
                hitRate,
                savings
        );
    }

    public void printSummary() {
        MetricsResponse metrics = getMetrics();

        System.out.println("=========================================");
        System.out.println("      LLM SERVER PERFORMANCE SUMMARY      ");
        System.out.println("=========================================");
        System.out.println("Total Requests:         " + metrics.totalRequests());
        System.out.println("Exact Cache Hits:       " + metrics.exactCacheHits());
        System.out.println("Semantic Cache Hits:    " + metrics.semanticCacheHits());
        System.out.println("Overall Cache Hit Rate: " + String.format("%.2f", metrics.cacheHitRate()) + "%");
        System.out.println("Average Latency:        " + String.format("%.2f", metrics.averageLatency()) + "ms");
        System.out.println("Estimated Savings ($):  " + String.format("%.4f", metrics.estimatedSavings()));
        System.out.println("=========================================");
    }
}
