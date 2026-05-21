package com.example.llminference.controller;

import com.example.llminference.model.LlmRequest;
import com.example.llminference.model.LlmResponse;
import com.example.llminference.service.LlmService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

// This controller handles LLM-related HTTP requests.
@RestController
public class LlmController {

    private final LlmService llmService;

    // Spring injects the service here.
    public LlmController(LlmService llmService) {
        this.llmService = llmService;
    }

    // When someone sends POST /api/llm/generate,
    // Spring converts the JSON body into an LlmRequest object
    // and passes it into this method.
    // Returning CompletableFuture tells Spring to handle the request asynchronously.
    @PostMapping("/api/llm/generate")
    public CompletableFuture<LlmResponse> generate(@RequestBody LlmRequest request) {

        // Controller stays thin.
        // It only forwards the request to the service and returns the result.
        return llmService.generate(request);
    }
}