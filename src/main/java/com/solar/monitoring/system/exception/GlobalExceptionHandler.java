package com.solar.monitoring.system.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handles MethodArgumentNotValidException thrown by controller input validation and
     * converts it into a 400 Bad Request response.
     *
     * The response body is a Map with the following keys:
     * - "timestamp": current server time
     * - "status": HTTP status code (400)
     * - "error": short error label ("Validation Failed")
     * - "errors": a Map<String, String> mapping each invalid field name to its validation message
     *
     * @param ex the validation exception containing binding results and field errors
     * @return a ResponseEntity with status 400 and a structured body describing validation failures
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        response.put("errors", errors);
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * Handles any uncaught exceptions from controllers and returns a standardized 500 response.
     *
     * <p>Logs the exception and builds a response body containing:
     * <ul>
     *   <li>timestamp - the time the error occurred</li>
     *   <li>status - HTTP status code 500</li>
     *   <li>error - short error label "Internal Server Error"</li>
     *   <li>message - the exception's message</li>
     * </ul>
     *
     * @param ex the unhandled exception
     * @return a ResponseEntity with HTTP 500 and a map containing error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unhandled exception: ", ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}