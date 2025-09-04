package com.solar.monitoring.system.kafka.service.impl;
import com.solar.monitoring.system.kafka.dto.EventDto;
import com.solar.monitoring.system.kafka.service.IKafkaProducerService;
import com.solar.monitoring.system.kafka.model.SolarEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class KafkaProducerServiceImpl implements IKafkaProducerService {

    protected final KafkaTemplate<String, SolarEvent> kafkaTemplate;

    protected void logEventPublishing(String topic, String eventType, String machineId) {
        log.info("Publishing {} event to topic {} for machine: {}", eventType, topic, machineId);
    }

    protected void logEventPublished(String topic, String eventType, String machineId) {
        log.info("Successfully published {} event to topic {} for machine: {}", eventType, topic, machineId);
    }

    public KafkaProducerServiceImpl(KafkaTemplate<String, SolarEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishEvent(String topic, EventDto event) {
        logEventPublishing(topic, event.getEventType(), event.getMachineId());

        try {
            SolarEvent solarEvent = SolarEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(event.getEventType())
                    .machineId(event.getMachineId())
                    .timestamp(event.getTimestamp())
                    .data(event.getData())
                    .source(event.getSource())
                    .createdAt(LocalDateTime.now())
                    .build();

            kafkaTemplate.send(topic, event.getMachineId(), solarEvent);
            logEventPublished(topic, event.getEventType(), event.getMachineId());

        } catch (Exception e) {
            log.error("Error publishing event to topic {} for machine {}: {}",
                    topic, event.getMachineId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void publishDataProcessedEvent(EventDto event) {
        publishEvent("solar-data-processed", event);
    }
}