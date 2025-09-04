package com.solar.monitoring.system.redis.exception;

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
     * Handles Redis-related exceptions thrown by controllers.
     *
     * Builds a response body with keys "error" ("Redis Error"), "message" (exception message),
     * and "timestamp" (current LocalDateTime), and returns HTTP 500 Internal Server Error.
     *
     * @param e the RedisException that occurred
     * @return a ResponseEntity with a Map containing error details and HTTP status 500
     */
    @ExceptionHandler(RedisException.class)
    public ResponseEntity<Map<String, Object>> handleRedisException(RedisException e) {
        log.error("Redis exception: {}", e.getMessage(), e);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Redis Error");
        response.put("message", e.getMessage());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Global handler for uncaught exceptions thrown by controllers.
     *
     * Returns an HTTP 500 (Internal Server Error) response with a JSON body containing:
     * - "error": fixed value "Internal Server Error"
     * - "message": fixed value "An unexpected error occurred"
     * - "timestamp": the current server LocalDateTime
     *
     * @param e the exception that was thrown
     * @return a ResponseEntity containing a Map<String,Object> with error details and HTTP status 500
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