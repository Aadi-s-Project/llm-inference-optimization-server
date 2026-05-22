package com.example.llminference.controller;

import com.example.llminference.model.LlmRequest;
import com.example.llminference.model.LlmResponse;
import com.example.llminference.service.LlmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LlmControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LlmService llmService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGenerateEndpointSuccess() throws Exception {
        LlmRequest request = new LlmRequest("user-1", "Explain SOLID", "openai");
        LlmResponse response = new LlmResponse("SOLID is...");

        when(llmService.generate(any(LlmRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        MvcResult mvcResult = mockMvc.perform(post("/api/llm/generate")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("SOLID is..."))
                .andExpect(header().exists("X-Rate-Limit-Remaining"));
    }

    @Test
    void testGenerateEndpointMissingHeader() throws Exception {
        LlmRequest request = new LlmRequest("user-1", "test", "openai");

        mockMvc.perform(post("/api/llm/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRateLimiterExhaustion() throws Exception {
        LlmRequest request = new LlmRequest("user-exhaust", "test", "openai");
        LlmResponse response = new LlmResponse("ok");

        when(llmService.generate(any(LlmRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        // Exhaust the 10 tokens
        for (int i = 0; i < 10; i++) {
            MvcResult mvcResult = mockMvc.perform(post("/api/llm/generate")
                            .header("X-User-Id", "user-exhaust")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(request().asyncStarted())
                    .andReturn();
            
            mockMvc.perform(asyncDispatch(mvcResult))
                    .andExpect(status().isOk());
        }

        // 11th should be TOO MANY REQUESTS
        mockMvc.perform(post("/api/llm/generate")
                        .header("X-User-Id", "user-exhaust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests());
    }
}
