package com.example.llminference.controller;

// GetMapping is used to map an HTTP GET request to a Java method.
import org.springframework.web.bind.annotation.GetMapping;

// RestController tells Spring that this class will handle HTTP requests
// and directly return data as the HTTP response.
import org.springframework.web.bind.annotation.RestController;

// This annotation marks the class as a REST controller.
// Simple meaning: this class receives requests from browser/curl/Postman.
@RestController
public class HelloController {

    // This annotation means:
    // When someone sends a GET request to /hello,
    // Spring should run this hello() method.
    @GetMapping("/hello")
    public String hello() {

        // Whatever we return from this method becomes the HTTP response.
        return "Hello from LLM Inference Optimization Server";
    }
}