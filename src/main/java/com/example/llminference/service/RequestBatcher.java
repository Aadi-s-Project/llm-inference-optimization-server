package com.example.llminference.service;

import com.example.llminference.model.BatchItem;
import com.example.llminference.model.LlmRequest;
import com.example.llminference.model.LlmResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Service that collects individual requests and processes them in batches.
 * Design Pattern: Producer-Consumer / Batching Pattern.
 */
@Service
public class RequestBatcher {

    private final BlockingQueue<BatchItem> queue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final LlmClient llmClient;

    private static final int BATCH_SIZE_THRESHOLD = 5;
    private static final long BATCH_WINDOW_MS = 50;

    public RequestBatcher(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    @PostConstruct
    public void init() {
        // Start the background worker that wakes up every 50ms
        executor.scheduleWithFixedDelay(this::processBatch, BATCH_WINDOW_MS, BATCH_WINDOW_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * Entry point for a request to be added to a batch.
     * Returns a CompletableFuture that will be completed when the batch is processed.
     */
    public CompletableFuture<LlmResponse> submit(LlmRequest request) {
        CompletableFuture<LlmResponse> future = new CompletableFuture<>();
        queue.add(new BatchItem(request, future));
        return future;
    }

    private void processBatch() {
        if (queue.isEmpty()) {
            return;
        }

        List<BatchItem> batch = new ArrayList<>();
        // Drain up to BATCH_SIZE_THRESHOLD items from the queue
        queue.drainTo(batch, BATCH_SIZE_THRESHOLD);

        if (batch.isEmpty()) {
            return;
        }

        System.out.println("--- Batch Processing Started ---");
        System.out.println("Processing a batch of " + batch.size() + " requests via " + llmClient.getClass().getSimpleName());

        // Extract requests to process
        List<LlmRequest> requests = batch.stream()
                .map(BatchItem::request)
                .collect(java.util.stream.Collectors.toList());

        // Call the LLM Client (Adapter Pattern)
        List<LlmResponse> responses = llmClient.generateBatch(requests);

        // Map responses back to their original futures
        for (int i = 0; i < batch.size(); i++) {
            batch.get(i).future().complete(responses.get(i));
        }

        System.out.println("--- Batch Processing Completed ---");
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }
}
