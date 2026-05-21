package com.example.llminference.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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

    public void printSummary() {
        long total = totalRequests.get();
        long exact = exactCacheHits.get();
        long semantic = semanticCacheHits.get();
        double avgLatency = total == 0 ? 0 : (double) totalLatencyMs.get() / total;
        double hitRate = total == 0 ? 0 : (double) (exact + semantic) / total * 100;

        System.out.println("=========================================");
        System.out.println("      LLM SERVER PERFORMANCE SUMMARY      ");
        System.out.println("=========================================");
        System.out.println("Total Requests:         " + total);
        System.out.println("Exact Cache Hits:       " + exact);
        System.out.println("Semantic Cache Hits:    " + semantic);
        System.out.println("Overall Cache Hit Rate: " + String.format("%.2f", hitRate) + "%");
        System.out.println("Average Latency:        " + String.format("%.2f", avgLatency) + "ms");
        System.out.println("Estimated Savings ($):  " + String.format("%.4f", (exact + semantic) * 0.02)); // Assuming $0.02 per LLM call
        System.out.println("=========================================");
    }
}
