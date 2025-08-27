package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.SolarMetricsDto;

public interface IKafkaService {
    void sendMessage(String topic, SolarMetricsDto message);
    void consumeMessage(String message);
}