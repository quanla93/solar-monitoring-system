package com.solar.monitoring.system.kafka.controller.impl;

import com.solar.monitoring.system.kafka.controller.IKafkaController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class KafkaControllerImpl implements IKafkaController {

    /**
     * Returns a simple health payload for the service.
     *
     * The response body is a map with keys:
     * - "status": set to "UP"
     * - "service": set to "solar-kafka-service"
     * - "timestamp": current server LocalDateTime as an ISO-8601 string
     *
     * @return ResponseEntity containing the health map (HTTP 200)
     */
    @GetMapping("/health")
    @Override
    public ResponseEntity<Map<String, String>> health() {
        logHealthCheck();

        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "solar-kafka-service");
        health.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(health);
    }

    /**
     * Logs a debug-level message indicating that a health check was requested.
     *
     * Used by the health endpoint to record invocation for diagnostics.
     */
    protected void logHealthCheck() {
        log.debug("Health check requested");
    }
}