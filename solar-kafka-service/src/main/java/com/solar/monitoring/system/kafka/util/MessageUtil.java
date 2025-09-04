package com.solar.monitoring.system.kafka.util;
import com.solar.monitoring.system.kafka.dto.EventDto;
import com.solar.monitoring.system.kafka.model.SolarEvent;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class MessageUtil {

    /**
     * Constructs a new SolarEvent from the provided EventDto and source.
     *
     * The returned SolarEvent contains a freshly generated UUID as its eventId,
     * copies eventType, machineId, timestamp, and data from the EventDto, sets
     * the provided source, and sets createdAt to the current system time.
     *
     * @param eventDto source DTO containing eventType, machineId, timestamp and data
     * @param source   identifier of the origin/source for the created event
     * @return a populated SolarEvent instance
     */
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

    /**
     * Builds a compact, time-ordered event key for a machine and event type.
     *
     * <p>The key format is {@code machineId_eventType_timestampMillis}, where
     * {@code timestampMillis} is the current system time in milliseconds to ensure uniqueness and ordering.
     *
     * @param machineId identifier of the machine that produced the event
     * @param eventType type or category of the event
     * @return a string key combining machineId, eventType, and the current epoch milliseconds
     */
    public static String generateEventKey(String machineId, String eventType) {
        return String.format("%s_%s_%d", machineId, eventType, System.currentTimeMillis());
    }
}