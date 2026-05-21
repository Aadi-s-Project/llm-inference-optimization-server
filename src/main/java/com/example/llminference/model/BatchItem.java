package com.example.llminference.model;

import java.util.concurrent.CompletableFuture;

/**
 * A wrapper to hold a single request and the "promise" (future) 
 * that will eventually contain its response.
 */
public record BatchItem(
        LlmRequest request,
        CompletableFuture<LlmResponse> future
) {
}
