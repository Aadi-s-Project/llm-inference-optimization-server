package com.example.llminference.service;

import com.example.llminference.model.LlmRequest;
import com.example.llminference.model.LlmResponse;
import org.springframework.stereotype.Service;

// This class contains the business logic for the LLM endpoint.
// Controller will call this service instead of doing the work itself.
@Service
public class LlmService {

    // For now, this method returns a mock response.
    // Later we will add rate limiting, cache, batching, and OpenAI calls here.
    public LlmResponse generate(LlmRequest request) {
        return new LlmResponse("Mock response for: " + request.prompt());
    }
}