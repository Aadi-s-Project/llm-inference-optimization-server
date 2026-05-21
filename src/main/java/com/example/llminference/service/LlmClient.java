package com.example.llminference.service;

import com.example.llminference.model.LlmRequest;
import com.example.llminference.model.LlmResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface defining the contract for LLM providers.
 * Design Pattern: Adapter Pattern (Target Interface).
 */
public interface LlmClient {

    /**
     * Processes a single prompt.
     */
    LlmResponse generate(LlmRequest request);

    /**
     * Processes a list of prompts as a batch.
     */
    List<LlmResponse> generateBatch(List<LlmRequest> requests);
}
