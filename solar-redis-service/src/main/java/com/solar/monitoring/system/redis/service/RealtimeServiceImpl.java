package com.solar.monitoring.system.redis.service;

import com.solar.monitoring.system.redis.dto.RealtimeDataDto;
import com.solar.monitoring.system.redis.exception.RedisException;
import com.solar.monitoring.system.redis.model.RealtimeMetrics;
import com.solar.monitoring.system.redis.util.RedisKeyUtil;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RealtimeServiceImpl implements IRealtimeService {

    protected final RedisTemplate<String, Object> redisTemplate;

    public RealtimeServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    protected void logServiceOperation(String operation, String machineId) {
        log.debug("Executing {} operation for machine: {}", operation, machineId);
    }

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
    public void saveMetrics(String machineId, RealtimeDataDto metrics) {
        logRepositoryOperation("save", metrics.getMachineId());

        try {
            String key = getRealtimeKey(metrics.getMachineId());

            redisTemplate.opsForHash().put(key, "machineId", metrics.getMachineId());
            redisTemplate.opsForHash().put(key, "timestamp", metrics.getTimestamp().toString());
            redisTemplate.opsForHash().put(key, "powerOutput", metrics.getPowerOutput());
            redisTemplate.opsForHash().put(key, "voltage", metrics.getVoltage());
            redisTemplate.opsForHash().put(key, "current", metrics.getCurrent());
            redisTemplate.opsForHash().put(key, "temperature", metrics.getTemperature());
            redisTemplate.opsForHash().put(key, "efficiency", metrics.getEfficiency());
            redisTemplate.opsForHash().put(key, "lastUpdated", LocalDateTime.now().toString());

            // Set expiration to 1 hour
            redisTemplate.expire(key, 1, TimeUnit.HOURS);


        } catch (Exception e) {
            log.error("Error saving realtime data for machine {}: {}", metrics.getMachineId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<RealtimeDataDto> getRealtimeMetrics(String machineId) {
        logServiceOperation("getRealtimeMetrics", machineId);

        try {
            RealtimeMetrics metrics = getRealtimeData(machineId);

            if (metrics == null) {
                log.warn("No realtime data found for machine: {}", machineId);
                return Optional.ofNullable(RealtimeDataDto.builder()
                        .machineId(machineId)
                        .lastUpdated(LocalDateTime.now())
                        .build());
            }

            return Optional.ofNullable(RealtimeDataDto.builder()
                    .machineId(metrics.getMachineId())
                    .timestamp(metrics.getTimestamp())
                    .powerOutput(metrics.getPowerOutput())
                    .voltage(metrics.getVoltage())
                    .current(metrics.getCurrent())
                    .temperature(metrics.getTemperature())
                    .efficiency(metrics.getEfficiency())
                    .metadata(metrics.getMetadata())
                    .lastUpdated(metrics.getLastUpdated())
                    .build());

        } catch (Exception e) {
            log.error("Error retrieving realtime metrics for machine {}: {}", machineId, e.getMessage(), e);
            throw new RedisException("Failed to retrieve realtime data: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteMetrics(String machineId) {
        logRepositoryOperation("delete", machineId);

        try {
            String key = getRealtimeKey(machineId);
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Error deleting realtime data for machine {}: {}", machineId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean exists(String machineId) {
        logRepositoryOperation("exists", machineId);
        try {
            String key = getRealtimeKey(machineId);
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Error checking existence for machine {}: {}", machineId, e.getMessage(), e);
            return false;
        }
    }


    protected String getRealtimeKey(String machineId) {
        return RedisKeyUtil.getRealtimeDataKey(machineId);
    }

    protected void logRepositoryOperation(String operation, String machineId) {
        log.debug("Executing Redis {} operation for machine: {}", operation, machineId);
    }


    public RealtimeMetrics getRealtimeData(String machineId) {
        logRepositoryOperation("get", machineId);

        try {
            String key = getRealtimeKey(machineId);
            Map<Object, Object> data = redisTemplate.opsForHash().entries(key);

            if (data.isEmpty()) {
                return null;
            }

            return RealtimeMetrics.builder()
                    .machineId((String) data.get("machineId"))
                    .timestamp(parseLocalDateTime(data.get("timestamp")))
                    .powerOutput(parseDouble(data.get("powerOutput")))
                    .voltage(parseDouble(data.get("voltage")))
                    .current(parseDouble(data.get("current")))
                    .temperature(parseDouble(data.get("temperature")))
                    .efficiency(parseDouble(data.get("efficiency")))
                    .lastUpdated(parseLocalDateTime(data.get("lastUpdated")))
                    .build();

        } catch (Exception e) {
            log.error("Error retrieving realtime data for machine {}: {}", machineId, e.getMessage(), e);
            throw e;
        }
    }

    private Double parseDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private LocalDateTime parseLocalDateTime(Object value) {
        if (value == null) return null;
        if (value instanceof String) {
            try {
                return LocalDateTime.parse((String) value);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}