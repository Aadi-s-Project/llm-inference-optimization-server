package com.example.llminference.model;

// This record represents the JSON request body sent by the user.
public record LlmRequest(
        String userId,
        String prompt
) {
}