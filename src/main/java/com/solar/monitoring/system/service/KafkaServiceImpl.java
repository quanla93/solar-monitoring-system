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