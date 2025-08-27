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
    
    /**
     * Parses a raw metrics payload (JSON or XML) and returns a populated SolarMetricsDto.
     *
     * The method determines format by checking whether the trimmed input starts with '<' (XML)
     * or is treated as JSON otherwise, then delegates to the appropriate parser.
     * Timestamp values are parsed using ISO_LOCAL_DATE_TIME; if timestamp parsing fails,
     * the parser falls back to the current time.
     *
     * @param rawData a non-null string containing either a JSON or XML representation of solar metrics
     * @return a SolarMetricsDto populated from the input
     * @throws RuntimeException if parsing fails for any reason (original cause is wrapped)
     */
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
    
    /**
     * Parses a JSON string into a SolarMetricsDto.
     *
     * The input JSON must contain at minimum the keys "machineId", "powerGeneration" and "timestamp".
     * Optional keys: "voltage", "current", "temperature", and "status".
     * - "status" defaults to "UNKNOWN" when absent.
     * - "timestamp" is converted via parseTimestamp(...).
     *
     * @param jsonData JSON string representing solar metrics
     * @return populated SolarMetricsDto
     * @throws Exception if JSON parsing or field conversion fails
     */
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
    
    /**
     * Parses an XML document (as a string) into a SolarMetricsDto.
     *
     * The XML is read into a JsonNode and mapped to the DTO using the following fields:
     * - machineId (required)
     * - powerGeneration (required)
     * - voltage, current, temperature (optional; mapped to null if absent)
     * - status (optional; defaults to "UNKNOWN" if absent)
     * - timestamp (required; parsed by parseTimestamp)
     *
     * The timestamp is expected to be in ISO_LOCAL_DATE_TIME format; parsing failures are handled by parseTimestamp.
     *
     * @param xmlData raw XML string containing the metric fields
     * @return a populated SolarMetricsDto built from the XML content
     * @throws Exception if the XML cannot be parsed or required fields are missing/invalid
     */
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
    
    /**
     * Parses an ISO_LOCAL_DATE_TIME timestamp string into a LocalDateTime.
     *
     * If parsing fails, logs a warning and returns the current system time.
     *
     * @param timestamp the timestamp string expected in ISO_LOCAL_DATE_TIME format (e.g. "2025-08-27T14:30:00")
     * @return the parsed LocalDateTime, or LocalDateTime.now() if the input cannot be parsed
     */
    private LocalDateTime parseTimestamp(String timestamp) {
        try {
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            log.warn("Failed to parse timestamp {}, using current time", timestamp);
            return LocalDateTime.now();
        }
    }
}