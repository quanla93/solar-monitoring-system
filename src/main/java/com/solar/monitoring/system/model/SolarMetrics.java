package com.solar.monitoring.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "solar_metrics")
public class SolarMetrics {
    
    @Id
    private String id;
    private String machineId;
    private Double powerGeneration;
    private Double voltage;
    private Double current;
    private Double temperature;
    private String status;
    private LocalDateTime timestamp;
    private String rawData;
    private Map<String, Object> additionalMetrics;
}