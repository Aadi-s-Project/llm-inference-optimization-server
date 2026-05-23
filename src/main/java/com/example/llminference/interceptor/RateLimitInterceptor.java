package com.example.llminference.interceptor;

import com.example.llminference.service.RateLimiterService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that enforces rate limits before a request reaches the controller.
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;

    public RateLimitInterceptor(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Allow CORS preflight requests (OPTIONS) to pass through without rate limiting or header checks.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Skip rate limiting if this is an internal async dispatch
        if (request.getDispatcherType().name().equals("ASYNC")) {
            return true;
        }

        // We only want to rate limit the LLM generation endpoint.
        if (!request.getRequestURI().startsWith("/api/llm/")) {
            return true;
        }

        // Identify the user. Here we use a custom header 'X-User-Id'.
        // In a real app, this might come from a JWT token or Session.
        String userId = request.getHeader("X-User-Id");

        if (userId == null || userId.isBlank()) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Missing X-User-Id header");
            return false;
        }

        Bucket bucket = rateLimiterService.resolveBucket(userId);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // Add a header to tell the user how many tokens they have left.
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            // No tokens left. Reject the request.
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate limit exceeded. Try again in " + waitForRefill + " seconds.");
            return false;
        }
    }
}
