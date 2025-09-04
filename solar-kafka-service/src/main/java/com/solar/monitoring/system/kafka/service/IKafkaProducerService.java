package com.solar.monitoring.system.kafka.service;
import com.solar.monitoring.system.kafka.dto.EventDto;

public interface IKafkaProducerService {
    void publishEvent(String topic, EventDto event);
    void publishDataProcessedEvent(EventDto event);
}
