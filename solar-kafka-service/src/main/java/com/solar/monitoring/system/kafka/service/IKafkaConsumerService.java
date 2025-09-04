package com.solar.monitoring.system.kafka.service;

import com.solar.monitoring.system.kafka.model.SolarEvent;

public interface IKafkaConsumerService {
    void handleExternalEvent(SolarEvent event);
    void handleDataProcessedEvent(SolarEvent event);
}
