package com.example.llminference.controller;

import com.example.llminference.model.MetricsResponse;
import com.example.llminference.service.MetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to expose performance metrics via REST API.
 */
@RestController
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/api/metrics")
    public MetricsResponse getMetrics() {
        return metricsService.getMetrics();
    }
}
