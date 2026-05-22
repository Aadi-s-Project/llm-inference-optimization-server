package com.example.llminference.service;

import com.example.llminference.model.SemanticCacheResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

/**
 * Client to communicate with the Python AI service for semantic caching.
 */
@Service
public class SemanticCacheClient {

    private final RestTemplate restTemplate;
    private final String pythonServiceUrl = "http://localhost:5005";

    public SemanticCacheClient() {
        this.restTemplate = new RestTemplate();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Adding a User-Agent helps avoid 403 Forbidden errors from some environments/proxies
        headers.set("User-Agent", "Java-Spring-Boot-Client");
        return headers;
    }

    public SemanticCacheResponse search(String prompt, String provider) {
        try {
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(
                Map.of("prompt", prompt, "provider", provider), 
                createHeaders()
            );
            return restTemplate.postForObject(
                    pythonServiceUrl + "/search",
                    entity,
                    SemanticCacheResponse.class
            );
        } catch (Exception e) {
            System.err.println("Error calling semantic cache: " + e.getMessage());
            return new SemanticCacheResponse(false, null, null, 0.0);
        }
    }

    public void add(String prompt, String response, String provider) {
        try {
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(
                    Map.of("prompt", prompt, "response", response, "provider", provider),
                    createHeaders()
            );
            restTemplate.postForObject(
                    pythonServiceUrl + "/add",
                    entity,
                    Map.class
            );
        } catch (Exception e) {
            System.err.println("Error adding to semantic cache: " + e.getMessage());
        }
    }
}
