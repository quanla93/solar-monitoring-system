package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.SolarMetricsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import redis.embedded.RedisServer;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.redis.port=6370"
})
@Disabled
class RedisServiceImplTest {
    
    private RedisServer redisServer;
    
    @Autowired
    private IRedisService redisService;
    
    @BeforeEach
    void setUp() throws Exception {
        redisServer = new RedisServer(6370);
        redisServer.start();
    }
    
    @Test
    void testSaveAndGetLatestMetrics() {
        // Given
        SolarMetricsDto metrics = SolarMetricsDto.builder()
            .machineId("MACHINE_001")
            .powerGeneration(150.5)
            .voltage(240.0)
            .current(0.625)
            .temperature(45.2)
            .status("ACTIVE")
            .timestamp(LocalDateTime.now())
            .build();
        
        // When
        redisService.saveMetrics("MACHINE_001", metrics);
        Optional<SolarMetricsDto> retrieved = redisService.getLatestMetrics("MACHINE_001");
        
        // Then
        assertTrue(retrieved.isPresent());
        assertEquals("MACHINE_001", retrieved.get().getMachineId());
        assertEquals(150.5, retrieved.get().getPowerGeneration());
    }
    
    @Test
    void testExistsMethod() {
        // Given
        SolarMetricsDto metrics = SolarMetricsDto.builder()
            .machineId("MACHINE_002")
            .powerGeneration(100.0)
            .build();
        
        // When
        assertFalse(redisService.exists("MACHINE_002"));
        redisService.saveMetrics("MACHINE_002", metrics);
        
        // Then
        assertTrue(redisService.exists("MACHINE_002"));
    }
    
    void tearDown() throws Exception {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}