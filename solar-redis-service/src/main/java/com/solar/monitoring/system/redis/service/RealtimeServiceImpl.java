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

    /**
     * Constructs a RealtimeServiceImpl backed by the provided RedisTemplate for Redis operations.
     */
    public RealtimeServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Log a debug message indicating a service-level operation for the specified machine.
     *
     * @param operation  short name of the operation being performed (e.g., "get", "save", "delete")
     * @param machineId  identifier of the machine the operation targets
     */
    protected void logServiceOperation(String operation, String machineId) {
        log.debug("Executing {} operation for machine: {}", operation, machineId);
    }

    /**
         * Persist the provided realtime metrics into Redis for a machine.
         *
         * Stores the metrics as a Redis hash (fields: machineId, timestamp, powerOutput,
         * voltage, current, temperature, efficiency, lastUpdated) and sets the key to
         * expire after 1 hour.
         *
         * Note: the implementation uses metrics.getMachineId() to derive the Redis key;
         * that value should match the supplied `machineId`.
         *
         * @param machineId the machine identifier (should match metrics.getMachineId())
         * @param metrics   the realtime metrics payload to persist
         * @throws RuntimeException propagates any runtime exception raised by Redis operations
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

    /**
     * Retrieve realtime metrics for a given machine.
     *
     * If stored metrics exist in Redis the returned Optional contains a RealtimeDataDto
     * populated from the stored values. If no stored data is found the Optional will
     * contain a RealtimeDataDto with only the provided machineId and a current
     * lastUpdated timestamp. On low-level errors the method throws a RedisException.
     *
     * @param machineId the identifier of the machine whose realtime metrics are requested
     * @return an Optional containing a RealtimeDataDto (always present unless an exception is thrown)
     * @throws RedisException if an error occurs while reading data from Redis
     */
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

    /**
     * Delete the stored realtime metrics for the given machine from Redis.
     *
     * The method constructs the Redis key for the machine and removes the associated hash entry.
     *
     * @param machineId the unique identifier of the machine whose realtime data should be deleted
     */
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

    /**
     * Checks whether realtime metrics exist in Redis for the given machine.
     *
     * @param machineId the unique identifier of the machine
     * @return true if a Redis key for the machine's realtime data exists; false if the key is absent or if an error occurs while checking
     */
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


    /**
     * Build and return the Redis key used to store realtime data for the given machine.
     *
     * @param machineId the identifier of the machine
     * @return the Redis key for this machine's realtime data
     */
    protected String getRealtimeKey(String machineId) {
        return RedisKeyUtil.getRealtimeDataKey(machineId);
    }

    /**
     * Log a Redis repository operation for a specific machine at debug level.
     *
     * @param operation  short name of the Redis operation (e.g., "GET", "HSET", "DEL")
     * @param machineId  identifier of the machine the operation targets
     */
    protected void logRepositoryOperation(String operation, String machineId) {
        log.debug("Executing Redis {} operation for machine: {}", operation, machineId);
    }


    /**
     * Retrieves stored realtime metrics for a machine from Redis and maps them to a RealtimeMetrics instance.
     *
     * Reads a Redis hash keyed for the given machineId and converts stored fields (machineId, timestamp,
     * powerOutput, voltage, current, temperature, efficiency, lastUpdated) using the class' parsing helpers.
     *
     * @param machineId the identifier of the machine whose realtime data should be retrieved
     * @return a populated RealtimeMetrics if data exists for the machine; null if no data is found
     */
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

    /**
     * Converts various object types to a Double.
     *
     * <p>Supported inputs:
     * - null -> returns null
     * - Double -> returned as-is
     * - other Number implementations -> converted via {@code doubleValue()}
     * - String -> parsed with {@link Double#parseDouble}; non-numeric strings return null
     *
     * @param value the value to convert (may be null)
     * @return the converted Double, or null if the input is null, not convertible, or a non-numeric string
     */
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

    /**
     * Parses an Object into a LocalDateTime.
     *
     * Converts a String value using LocalDateTime.parse and returns null for null input,
     * non-String types, or when parsing fails.
     *
     * @param value the value to parse (expected to be a ISO-8601 datetime String)
     * @return the parsed LocalDateTime, or null if input is null, not a String, or cannot be parsed
     */
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