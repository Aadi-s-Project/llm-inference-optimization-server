package com.example.llminference.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to manage rate limiting buckets for each user.
 * Uses the Token Bucket algorithm via Bucket4j.
 */
@Service
public class RateLimiterService {

    // Cache of buckets per user. ConcurrentHashMap is thread-safe.
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    /**
     * Resolves the bucket for a specific user.
     * If no bucket exists, it creates a new one with a default limit.
     */
    public Bucket resolveBucket(String userId) {
        return cache.computeIfAbsent(userId, this::newBucket);
    }

    private Bucket newBucket(String userId) {
        // Define the limit: 10 tokens capacity, refill 10 tokens every 1 minute.
        // This allows for bursts of up to 10 requests.
        return Bucket.builder()
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
                .build();
    }
}
