package com.example.llminference.service;

import com.example.llminference.model.LlmRequest;
import com.example.llminference.model.LlmResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class RequestBatcherTest {

    @Test
    void testBatchingLogic() throws Exception {
        LlmClient mockClient = mock(LlmClient.class);
        RequestBatcher batcher = new RequestBatcher(mockClient);
        batcher.init(); // Start background worker

        // Mock a response for a batch of 3
        List<LlmResponse> mockResponses = List.of(
                new LlmResponse("Ans 1"),
                new LlmResponse("Ans 2"),
                new LlmResponse("Ans 3")
        );
        when(mockClient.generateBatch(anyList())).thenReturn(mockResponses);

        // Submit 3 requests simultaneously
        CompletableFuture<LlmResponse> f1 = batcher.submit(new LlmRequest("u1", "p1", "openai"));
        CompletableFuture<LlmResponse> f2 = batcher.submit(new LlmRequest("u1", "p2", "openai"));
        CompletableFuture<LlmResponse> f3 = batcher.submit(new LlmRequest("u1", "p3", "openai"));

        // Wait for batch processing (window is 50ms)
        LlmResponse r1 = f1.get(1, TimeUnit.SECONDS);
        LlmResponse r2 = f2.get(1, TimeUnit.SECONDS);
        LlmResponse r3 = f3.get(1, TimeUnit.SECONDS);

        assertEquals("Ans 1", r1.answer());
        assertEquals("Ans 2", r2.answer());
        assertEquals("Ans 3", r3.answer());

        // Verify that generateBatch was called EXACTLY ONCE for these 3 requests
        verify(mockClient, times(1)).generateBatch(anyList());
        
        batcher.shutdown();
    }
}
