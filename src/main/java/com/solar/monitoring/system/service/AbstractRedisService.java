package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.SolarMetricsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Slf4j
public abstract class AbstractRedisService implements IRedisService {
    
    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;
    
    protected static final Duration DEFAULT_TTL = Duration.ofHours(24);
    
    /**
     * Build a Redis key for storing metrics for a specific machine.
     *
     * @param machineId the unique identifier of the machine
     * @return the Redis key in the form "solar:metrics:{machineId}"
     */
    protected String buildKey(String machineId) {
        return "solar:metrics:" + machineId;
    }
    
    /**
     * Store a SolarMetricsDto in Redis under the specified key with the given time-to-live.
     *
     * @param key   Redis key under which the value will be stored
     * @param value SolarMetricsDto to persist
     * @param ttl   duration the key should remain in Redis before expiring
     */
    protected void setWithTTL(String key, SolarMetricsDto value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
        log.debug("Saved metrics to Redis with key: {} and TTL: {}", key, ttl);
    }
}