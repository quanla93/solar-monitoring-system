package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.SolarMetricsDto;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaServiceImpl extends AbstractKafkaService {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private IDataProcessingService dataProcessingService;
    
    @Value("${app.kafka.topic.solar-metrics:solar-metrics}")
    private String solarMetricsTopic;
    
    /**
     * Serializes a SolarMetricsDto and publishes it to the given Kafka topic using the DTO's machineId as the message key.
     *
     * <p>The method delegates serialization to the superclass and uses the injected KafkaTemplate to send the message.
     * Any exception encountered while sending is propagated to the caller.</p>
     *
     * @param topic   the Kafka topic to send the message to
     * @param message the solar metrics payload; its {@code machineId} is used as the Kafka message key
     */
    @Override
    @Retry(name = "kafka-producer")
    public void sendMessage(String topic, SolarMetricsDto message) {
        try {
            String serializedMessage = serializeMessage(message);
            kafkaTemplate.send(topic, message.getMachineId(), serializedMessage);
            log.info("Message sent to topic {}: {}", topic, message.getMachineId());
        } catch (Exception e) {
            log.error("Failed to send message to topic {}: {}", topic, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Kafka listener for the solar metrics topic; receives raw message payloads and forwards them to the data processing service.
     *
     * Subscribes to the topic configured by `app.kafka.topic.solar-metrics` (default "solar-metrics").
     * On success the payload is passed to IDataProcessingService.processRealTimeData.
     * If processing fails the exception is caught and IDataProcessingService.handleFailure is invoked with the error message and original payload.
     *
     * @param message raw message payload from the Kafka topic
     */
    @Override
    @KafkaListener(topics = "${app.kafka.topic.solar-metrics:solar-metrics}")
    public void consumeMessage(String message) {
        try {
            log.info("Received message: {}", message);
            dataProcessingService.processRealTimeData(message);
        } catch (Exception e) {
            log.error("Failed to process Kafka message: {}", e.getMessage());
            dataProcessingService.handleFailure(e.getMessage(), message);
        }
    }
}