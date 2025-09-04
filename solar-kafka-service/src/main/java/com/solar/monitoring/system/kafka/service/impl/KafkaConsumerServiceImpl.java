package com.solar.monitoring.system.kafka.service.impl;
import com.solar.monitoring.system.kafka.service.IKafkaConsumerService;
import com.solar.monitoring.system.kafka.model.SolarEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerServiceImpl implements IKafkaConsumerService {

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

    protected void logEventProcessing(String eventType, String machineId) {
        log.info("Processing {} event for machine: {}", eventType, machineId);
    }

    protected void logEventProcessed(String eventType, String machineId) {
        log.info("Successfully processed {} event for machine: {}", eventType, machineId);
    }
}