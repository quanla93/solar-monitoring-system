package com.solar.monitoring.system.kafka.model;
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
public class SolarEvent {
    private String eventId;
    private String eventType;
    private String machineId;
    private LocalDateTime timestamp;
    private Map<String, Object> data;
    private String source;
    private LocalDateTime createdAt;
}