package com.solar.monitoring.system.kafka.controller;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IKafkaController {
    /**
 * Returns Kafka-related health information wrapped in an HTTP response.
 *
 * <p>The response body is a map of string keys to string values representing health details
 * (for example: "status", "message", or component-specific statuses). Implementations
 * should populate the map with concise, human-readable values and choose an appropriate
 * HTTP status code in the ResponseEntity.
 *
 * @return a ResponseEntity whose body is a Map<String, String> containing Kafka health details
 */
ResponseEntity<Map<String, String>> health();
}