package com.example.llminference.service;

import com.example.llminference.model.LlmRequest;
import com.example.llminference.model.LlmResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of LlmClient that proxies requests to the Python AI Sidecar.
 * Supports multiple providers: OpenAI, Gemini, Claude.
 */
@Service
public class MultiAiClient implements LlmClient {

    private final RestTemplate restTemplate;
    private final String pythonServiceUrl = "http://localhost:5005";

    public MultiAiClient() {
        this.restTemplate = new RestTemplate();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Java-Spring-Boot-Client");
        return headers;
    }

    @Override
    public LlmResponse generate(LlmRequest request) {
        try {
            // Explicitly creating a Map to ensure Jackson serializes all fields
            Map<String, String> body = new HashMap<>();
            body.put("prompt", request.prompt());
            body.put("provider", request.provider());

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, createHeaders());

            return restTemplate.postForObject(
                    pythonServiceUrl + "/generate",
                    entity,
                    LlmResponse.class
            );
        } catch (Exception e) {
            System.err.println("Sidecar Proxy Error: " + e.getMessage());
            return new LlmResponse("[PROXY ERROR] Python sidecar is down or unreachable.");
        }
    }

    @Override
    public List<LlmResponse> generateBatch(List<LlmRequest> requests) {
        return requests.stream()
                .map(this::generate)
                .collect(Collectors.toList());
    }
}
