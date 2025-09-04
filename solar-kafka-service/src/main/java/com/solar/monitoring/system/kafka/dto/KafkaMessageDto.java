package com.solar.monitoring.system.kafka.dto;
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
public class KafkaMessageDto {
    private String messageId;
    private String topic;
    private String key;
    private Map<String, Object> payload;
    private LocalDateTime timestamp;
}
