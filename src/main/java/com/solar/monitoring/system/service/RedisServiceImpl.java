package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.SolarMetricsDto;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class RedisServiceImpl extends AbstractRedisService {
    
    @Override
    @Retry(name = "redis")
    public void saveMetrics(String machineId, SolarMetricsDto metrics) {
        try {
            String key = buildKey(machineId);
            setWithTTL(key, metrics, DEFAULT_TTL);
            log.info("Saved metrics for machine: {}", machineId);
        } catch (Exception e) {
            log.error("Failed to save metrics for machine {}: {}", machineId, e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Optional<SolarMetricsDto> getMetrics(String machineId) {
        try {
            String key = buildKey(machineId);
            SolarMetricsDto metrics = (SolarMetricsDto) redisTemplate.opsForValue().get(key);
            return Optional.ofNullable(metrics);
        } catch (Exception e) {
            log.error("Failed to get metrics for machine {}: {}", machineId, e.getMessage());
            return Optional.empty();
        }
    }
    
    @Override
    public void deleteMetrics(String machineId) {
        try {
            String key = buildKey(machineId);
            redisTemplate.delete(key);
            log.info("Deleted metrics for machine: {}", machineId);
        } catch (Exception e) {
            log.error("Failed to delete metrics for machine {}: {}", machineId, e.getMessage());
        }
    }
    
    @Override
    public boolean exists(String machineId) {
        try {
            String key = buildKey(machineId);
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Failed to check existence for machine {}: {}", machineId, e.getMessage());
            return false;
        }
    }
}