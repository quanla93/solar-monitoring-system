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
    
    protected String serializeMessage(SolarMetricsDto message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Error serializing message: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize message", e);
        }
    }
    
    protected SolarMetricsDto deserializeMessage(String message) {
        try {
            return objectMapper.readValue(message, SolarMetricsDto.class);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing message: {}", e.getMessage());
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }
}