package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.SolarMetricsDto;
import com.solar.monitoring.system.dto.SyncResponseDto;
import com.solar.monitoring.system.model.SqlServerData;
import com.solar.monitoring.system.util.DataParser;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class DataProcessingServiceImpl implements IDataProcessingService {
    
    @Autowired
    private ISqlServerService sqlServerService;
    
    @Autowired
    private IRedisService redisService;
    
    @Autowired
    private IMongoService mongoService;
    
    @Autowired
    private IKafkaService kafkaService;
    
    @Autowired
    private DataParser dataParser;
    
    @Override
    @CircuitBreaker(name = "data-processing", fallbackMethod = "processDataPipelineFallback")
    @Retry(name = "data-processing")
    @Transactional
    public SyncResponseDto processDataPipeline() {
        String syncId = UUID.randomUUID().toString();
        log.info("Starting data processing pipeline with sync ID: {}", syncId);
        
        try {
            List<SqlServerData> unprocessedData = sqlServerService.fetchUnprocessedData();
            int processedCount = 0;
            
            for (SqlServerData data : unprocessedData) {
                try {
                    processingSingleRecord(data);
                    sqlServerService.markAsProcessed(data.getId());
                    processedCount++;
                } catch (Exception e) {
                    log.error("Failed to process record {}: {}", data.getId(), e.getMessage());
                    handleFailure(e.getMessage(), data.getDataContent());
                }
            }
            
            return SyncResponseDto.builder()
                .status("SUCCESS")
                .message("Data processing completed successfully")
                .processedRecords(processedCount)
                .syncId(syncId)
                .build();
                
        } catch (Exception e) {
            log.error("Data processing pipeline failed for sync ID {}: {}", syncId, e.getMessage());
            return SyncResponseDto.builder()
                .status("FAILED")
                .message("Data processing failed: " + e.getMessage())
                .processedRecords(0)
                .syncId(syncId)
                .build();
        }
    }
    
    @Override
    @Async
    public void processRealTimeData(String rawData) {
        try {
            log.info("Processing real-time data");
            SolarMetricsDto metrics = dataParser.parseToMetrics(rawData);
            
            // Save to Redis for real-time access
            redisService.saveMetrics(metrics.getMachineId(), metrics);
            
            // Save to MongoDB for historical data
            mongoService.saveMetrics(metrics);
            
            log.info("Successfully processed real-time data for machine: {}", metrics.getMachineId());
        } catch (Exception e) {
            log.error("Failed to process real-time  {}", e.getMessage());
            handleFailure(e.getMessage(), rawData);
        }
    }
    
    @Override
    public void handleFailure(String errorMessage, String data) {
        log.error("Handling failure: {} for  {}", errorMessage, data);
        // Implement dead letter queue or retry mechanism
        // For now, just log the failure
    }
    
    private void processingSingleRecord(SqlServerData data) {
        SolarMetricsDto metrics = dataParser.parseToMetrics(data.getDataContent());
        
        // Save to Redis
        redisService.saveMetrics(metrics.getMachineId(), metrics);
        
        // Save to MongoDB
        mongoService.saveMetrics(metrics);
        
        log.debug("Processed record for machine: {}", metrics.getMachineId());
    }
    
    public SyncResponseDto processDataPipelineFallback(Exception ex) {
        return SyncResponseDto.builder()
            .status("CIRCUIT_BREAKER_OPEN")
            .message("Service temporarily unavailable: " + ex.getMessage())
            .processedRecords(0)
            .syncId("FALLBACK")
            .build();
    }
}