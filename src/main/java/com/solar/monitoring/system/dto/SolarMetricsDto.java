package com.solar.monitoring.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolarMetricsDto {
    
    private String id;
    
    @NotBlank(message = "Machine ID is required")
    private String machineId;
    
    @NotNull(message = "Power generation is required")
    private Double powerGeneration;
    
    private Double voltage;
    private Double current;
    private Double temperature;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private Map<String, Object> additionalMetrics;
}