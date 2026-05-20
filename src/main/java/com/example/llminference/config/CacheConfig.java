package com.example.llminference.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for the caching layer.
 * Uses Caffeine for high-performance in-memory caching.
 */
@Configuration
public class CacheConfig {

    public static final String LLM_RESPONSES_CACHE = "llm-responses";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(LLM_RESPONSES_CACHE);
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000) // Limit memory usage
                .expireAfterWrite(10, TimeUnit.MINUTES) // Cache for 10 minutes
                .recordStats(); // Useful for metrics later (Stage 9)
    }
}
