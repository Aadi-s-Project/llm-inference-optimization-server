package com.example.llminference.service;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterServiceTest {

    private final RateLimiterService rateLimiterService = new RateLimiterService();

    @Test
    void testResolveBucket() {
        Bucket bucket1 = rateLimiterService.resolveBucket("user-1");
        Bucket bucket2 = rateLimiterService.resolveBucket("user-1");
        Bucket bucket3 = rateLimiterService.resolveBucket("user-2");

        assertNotNull(bucket1);
        assertSame(bucket1, bucket2, "Should return the same bucket for the same user");
        assertNotSame(bucket1, bucket3, "Should return different buckets for different users");
    }

    @Test
    void testTokenConsumption() {
        Bucket bucket = rateLimiterService.resolveBucket("user-burst");
        
        // Consume all 10 tokens
        for (int i = 0; i < 10; i++) {
            assertTrue(bucket.tryConsume(1), "Should allow 10 requests");
        }
        
        // 11th should fail
        assertFalse(bucket.tryConsume(1), "11th request should be rate limited");
    }
}
