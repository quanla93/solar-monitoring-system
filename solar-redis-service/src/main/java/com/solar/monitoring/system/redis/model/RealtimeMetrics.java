package com.solar.monitoring.system.redis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeMetrics {
    private String machineId;
    private LocalDateTime timestamp;
    private Double powerOutput;
    private Double voltage;
    private Double current;
    private Double temperature;
    private Double efficiency;
    private Map<String, Object> metadata;
    private LocalDateTime lastUpdated;
}