package com.solar.monitoring.system.kafka.exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles KafkaException thrown by controllers and returns a standardized error response.
     *
     * <p>Builds a JSON-friendly map with keys:
     * <ul>
     *   <li>"error" — fixed value "Kafka Error"</li>
     *   <li>"message" — the exception message</li>
     *   <li>"timestamp" — the current LocalDateTime</li>
     * </ul>
     * and responds with HTTP 500 (Internal Server Error).
     *
     * @param e the caught KafkaException
     * @return a ResponseEntity containing the error map and HTTP 500 status
     */
    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<Map<String, Object>> handleKafkaException(KafkaException e) {
        log.error("Kafka exception: {}", e.getMessage(), e);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Kafka Error");
        response.put("message", e.getMessage());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handles uncaught exceptions and returns a standardized HTTP 500 response.
     *
     * <p>Builds a response body containing:
     * <ul>
     *   <li>{@code "error"}: fixed value "Internal Server Error"</li>
     *   <li>{@code "message"}: fixed value "An unexpected error occurred"</li>
     *   <li>{@code "timestamp"}: the current LocalDateTime</li>
     * </ul>
     *
     * @param e the exception that was thrown
     * @return a ResponseEntity with HTTP 500 (INTERNAL_SERVER_ERROR) and a Map body containing error, message, and timestamp
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        log.error("Unexpected exception: {}", e.getMessage(), e);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Internal Server Error");
        response.put("message", "An unexpected error occurred");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}