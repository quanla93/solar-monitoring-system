package com.solar.monitoring.system.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.solar.monitoring.system.dto.SolarMetricsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class DataParser {
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private XmlMapper xmlMapper;
    
    public SolarMetricsDto parseToMetrics(String rawData) {
        try {
            if (rawData.trim().startsWith("<")) {
                return parseXmlData(rawData);
            } else {
                return parseJsonData(rawData);
            }
        } catch (Exception e) {
            log.error("Failed to parse  {}", e.getMessage());
            throw new RuntimeException("Failed to parse data", e);
        }
    }
    
    private SolarMetricsDto parseJsonData(String jsonData) throws Exception {
        JsonNode node = objectMapper.readTree(jsonData);
        
        return SolarMetricsDto.builder()
            .machineId(node.get("machineId").asText())
            .powerGeneration(node.get("powerGeneration").asDouble())
            .voltage(node.has("voltage") ? node.get("voltage").asDouble() : null)
            .current(node.has("current") ? node.get("current").asDouble() : null)
            .temperature(node.has("temperature") ? node.get("temperature").asDouble() : null)
            .status(node.has("status") ? node.get("status").asText() : "UNKNOWN")
            .timestamp(parseTimestamp(node.get("timestamp").asText()))
            .build();
    }
    
    private SolarMetricsDto parseXmlData(String xmlData) throws Exception {
        JsonNode node = xmlMapper.readTree(xmlData);
        
        return SolarMetricsDto.builder()
            .machineId(node.get("machineId").asText())
            .powerGeneration(node.get("powerGeneration").asDouble())
            .voltage(node.has("voltage") ? node.get("voltage").asDouble() : null)
            .current(node.has("current") ? node.get("current").asDouble() : null)
            .temperature(node.has("temperature") ? node.get("temperature").asDouble() : null)
            .status(node.has("status") ? node.get("status").asText() : "UNKNOWN")
            .timestamp(parseTimestamp(node.get("timestamp").asText()))
            .build();
    }
    
    private LocalDateTime parseTimestamp(String timestamp) {
        try {
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            log.warn("Failed to parse timestamp {}, using current time", timestamp);
            return LocalDateTime.now();
        }
    }
}