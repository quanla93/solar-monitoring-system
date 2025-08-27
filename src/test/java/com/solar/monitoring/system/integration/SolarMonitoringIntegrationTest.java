package com.solar.monitoring.system.integration;

import com.solar.monitoring.system.dto.SolarMetricsDto;
import com.solar.monitoring.system.model.SolarMetrics;
import com.solar.monitoring.system.repository.ISolarMetricsRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Disabled
class SolarMonitoringIntegrationTest {
    
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withExposedPorts(27017);
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ISolarMetricsRepository repository;
    
    @Test
    void testHealthEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/health", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    
    @Test
    void testGetRealtimeMetricsWhenNotFound() {
        ResponseEntity<SolarMetricsDto> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/realtime/NON_EXISTENT", SolarMetricsDto.class);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    void testMongoDbIntegration() {
        // Given
        SolarMetrics metrics = SolarMetrics.builder()
            .machineId("TEST_MACHINE")
            .powerGeneration(200.0)
            .voltage(220.0)
            .current(0.91)
            .temperature(40.5)
            .status("ACTIVE")
            .timestamp(LocalDateTime.now())
            .build();
        
        // When
        SolarMetrics saved = repository.save(metrics);
        
        // Then
        assertNotNull(saved.getId());
        assertEquals("TEST_MACHINE", saved.getMachineId());
        assertEquals(200.0, saved.getPowerGeneration());
    }
}