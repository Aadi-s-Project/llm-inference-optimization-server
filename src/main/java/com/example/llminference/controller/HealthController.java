package com.example.llminference.controller;

// GetMapping maps an HTTP GET request to a Java method.
import org.springframework.web.bind.annotation.GetMapping;

// RestController tells Spring this class handles HTTP requests
// and returns data directly in the response body.
import org.springframework.web.bind.annotation.RestController;

// This class contains simple health-check endpoints for the server.
@RestController
public class HealthController {

    // When someone sends GET /health, Spring runs this method.
    @GetMapping("/health")
    public String health() {

        // Returning "OK" means the server is up and responding.
        return "OK";
    }
}