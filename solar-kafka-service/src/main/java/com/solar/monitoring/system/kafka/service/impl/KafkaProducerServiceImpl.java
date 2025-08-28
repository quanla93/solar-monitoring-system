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

    /**
     * Logs an info-level message indicating the start of publishing an event to a Kafka topic.
     *
     * @param topic     the Kafka topic the event will be sent to
     * @param eventType the type of the event being published
     * @param machineId identifier of the machine the event relates to
     */
    protected void logEventPublishing(String topic, String eventType, String machineId) {
        log.info("Publishing {} event to topic {} for machine: {}", eventType, topic, machineId);
    }

    /**
     * Log an info-level message indicating a successful publication of an event to Kafka.
     *
     * @param topic     the Kafka topic the event was published to
     * @param eventType the event type that was published
     * @param machineId identifier of the machine associated with the event
     */
    protected void logEventPublished(String topic, String eventType, String machineId) {
        log.info("Successfully published {} event to topic {} for machine: {}", eventType, topic, machineId);
    }

    /**
     * Create a KafkaProducerServiceImpl wired with the given KafkaTemplate.
     *
     * The provided KafkaTemplate<String, SolarEvent> is used to send SolarEvent messages to Kafka.
     */
    public KafkaProducerServiceImpl(KafkaTemplate<String, SolarEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes an EventDto to the given Kafka topic as a SolarEvent.
     *
     * The method constructs a SolarEvent (assigning a new UUID to eventId and setting createdAt to the current time),
     * then sends it to Kafka using the event's machineId as the message key.
     *
     * @param topic the Kafka topic to publish to
     * @param event the event payload to convert and publish; its eventType, machineId, timestamp, data and source
     *              are copied into the produced SolarEvent
     * @throws RuntimeException if sending the message fails (the underlying exception is propagated)
     */
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

    /**
     * Publish the given processed event to the "solar-data-processed" Kafka topic.
     *
     * @param event the processed event payload to send (contains event type, machine id, timestamp, data, and source)
     */
    @Override
    public void publishDataProcessedEvent(EventDto event) {
        publishEvent("solar-data-processed", event);
    }
}