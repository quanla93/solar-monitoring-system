package com.solar.monitoring.system.kafka.service.impl;
import com.solar.monitoring.system.kafka.service.IKafkaConsumerService;
import com.solar.monitoring.system.kafka.model.SolarEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerServiceImpl implements IKafkaConsumerService {

    /**
     * Kafka listener that handles external solar events from the "external-solar-events" topic.
     *
     * This method is invoked for each incoming external SolarEvent; it records processing start,
     * performs event-specific handling (e.g., forwarding to processing services or persisting),
     * and records successful completion. Exceptions are caught and logged by the method.
     *
     * @param event the incoming SolarEvent containing at least machineId and eventType
     */
    @KafkaListener(topics = "external-solar-events", groupId = "solar-kafka-service-group")
    @Override
    public void handleExternalEvent(SolarEvent event) {
        logEventProcessing("external", event.getMachineId());

        try {
            // Process external event logic here
            // Could forward to data processing service, store, etc.
            log.info("Received external event: {} for machine: {}",
                    event.getEventType(), event.getMachineId());

            logEventProcessed("external", event.getMachineId());
        } catch (Exception e) {
            log.error("Error processing external event for machine {}: {}",
                    event.getMachineId(), e.getMessage(), e);
        }
    }

    /**
     * Kafka listener invoked when a "solar-data-processed" event is received.
     *
     * Processes a data-processed SolarEvent for the given machine (e.g., trigger notifications,
     * analytics, or other downstream handling). This method logs the start and successful
     * completion of processing; any exceptions are caught and logged.
     *
     * @param event the received SolarEvent containing at least the machineId and event details
     */
    @KafkaListener(topics = "solar-data-processed", groupId = "solar-kafka-service-group")
    @Override
    public void handleDataProcessedEvent(SolarEvent event) {
        logEventProcessing("data-processed", event.getMachineId());

        try {
            // Handle data processed event
            // Could trigger notifications, analytics, etc.
            log.info("Data processed event received for machine: {}", event.getMachineId());

            logEventProcessed("data-processed", event.getMachineId());
        } catch (Exception e) {
            log.error("Error processing data-processed event for machine {}: {}",
                    event.getMachineId(), e.getMessage(), e);
        }
    }

    /**
     * Record the start of processing for an event for the given machine.
     *
     * @param eventType  the type/name of the event being processed (e.g., "external", "data-processed")
     * @param machineId  identifier of the machine the event pertains to
     */
    protected void logEventProcessing(String eventType, String machineId) {
        log.info("Processing {} event for machine: {}", eventType, machineId);
    }

    /**
     * Log a successful completion message for processing an event.
     *
     * @param eventType  short identifier of the event category (e.g., "external", "data-processed")
     * @param machineId  unique identifier of the machine related to the event
     */
    protected void logEventProcessed(String eventType, String machineId) {
        log.info("Successfully processed {} event for machine: {}", eventType, machineId);
    }
}