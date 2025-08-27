package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.SolarMetricsDto;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class RedisServiceImpl extends AbstractRedisService {
    
    /**
     * Persist the given SolarMetricsDto in Redis for the specified machine.
     *
     * The method derives the Redis key from the supplied machineId, stores the
     * metrics under that key with the service's default TTL, and propagates any
     * runtime exception encountered during the operation.
     *
     * @param machineId the identifier of the machine; used to build the Redis key
     * @param metrics   the metrics payload to persist
     */
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
    
    /**
     * Retrieves stored SolarMetricsDto for the given machine id from Redis.
     *
     * Retrieves the metrics by building the Redis key from the provided machineId and returning the value
     * wrapped in an Optional. If no value is found, returns Optional.empty(). On Redis access errors this
     * method logs the failure and returns Optional.empty().
     *
     * @param machineId the identifier of the machine whose metrics are being retrieved; used to build the Redis key
     * @return an Optional containing the stored SolarMetricsDto if present, otherwise Optional.empty()
     */
    @Override
    public Optional<SolarMetricsDto> getLatestMetrics(String machineId) {
        try {
            String key = buildKey(machineId);
            SolarMetricsDto metrics = (SolarMetricsDto) redisTemplate.opsForValue().get(key);
            return Optional.ofNullable(metrics);
        } catch (Exception e) {
            log.error("Failed to get metrics for machine {}: {}", machineId, e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Removes persisted SolarMetricsDto for the given machine from Redis.
     *
     * <p>Builds the Redis key from {@code machineId} and deletes the corresponding entry.
     * Any exception during deletion is caught and logged; the method does not propagate exceptions.</p>
     *
     * @param machineId identifier of the machine whose metrics should be deleted (used to derive the Redis key)
     */
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
    
    /**
     * Checks whether metrics for the given machineId exist in Redis.
     *
     * @param machineId the machine identifier used to derive the Redis key
     * @return true if the Redis key exists and contains metrics; false if the key does not exist or an error occurs while checking
     */
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