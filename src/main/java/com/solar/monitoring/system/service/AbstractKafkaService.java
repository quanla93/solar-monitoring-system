package com.solar.monitoring.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.monitoring.system.dto.SolarMetricsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractKafkaService implements IKafkaService {
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    /**
     * Serialize a SolarMetricsDto to its JSON string representation.
     *
     * @param message the DTO to serialize
     * @return the JSON string representation of {@code message}
     * @throws RuntimeException if serialization fails; the original {@link com.fasterxml.jackson.core.JsonProcessingException}
     *         is preserved as the cause
     */
    protected String serializeMessage(SolarMetricsDto message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Error serializing message: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize message", e);
        }
    }
    
    /**
     * Parses a JSON-formatted string into a SolarMetricsDto.
     *
     * @param message JSON string representing a SolarMetricsDto
     * @return the deserialized SolarMetricsDto
     * @throws RuntimeException if JSON parsing fails (wraps the original JsonProcessingException)
     */
    protected SolarMetricsDto deserializeMessage(String message) {
        try {
            return objectMapper.readValue(message, SolarMetricsDto.class);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing message: {}", e.getMessage());
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }
}