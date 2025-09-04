package com.solar.monitoring.system.kafka.util;
import com.solar.monitoring.system.kafka.dto.EventDto;
import com.solar.monitoring.system.kafka.model.SolarEvent;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class MessageUtil {

    public static SolarEvent createSolarEvent(EventDto eventDto, String source) {
        return SolarEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventDto.getEventType())
                .machineId(eventDto.getMachineId())
                .timestamp(eventDto.getTimestamp())
                .data(eventDto.getData())
                .source(source)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static String generateEventKey(String machineId, String eventType) {
        return String.format("%s_%s_%d", machineId, eventType, System.currentTimeMillis());
    }
}