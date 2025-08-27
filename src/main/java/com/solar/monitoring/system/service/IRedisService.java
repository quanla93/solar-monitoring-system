package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.SolarMetricsDto;

import java.util.Optional;

public interface IRedisService {
    void saveMetrics(String machineId, SolarMetricsDto metrics);
    Optional<SolarMetricsDto> getMetrics(String machineId);
    void deleteMetrics(String machineId);
    boolean exists(String machineId);
}