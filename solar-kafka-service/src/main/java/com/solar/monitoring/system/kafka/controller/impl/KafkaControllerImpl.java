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

    protected void logHealthCheck() {
        log.debug("Health check requested");
    }
}