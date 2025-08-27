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
    
    /**
     * Orchestrates a bulk data processing pipeline: fetches unprocessed records from SQL Server,
     * processes each record (persisting parsed metrics to Redis and MongoDB), and marks successful
     * records as processed.
     *
     * <p>Per-record errors are handled individually (they are logged and delegated to {@code handleFailure})
     * so a failure processing one record does not abort the whole run. A UUID syncId is generated for
     * the pipeline run and included in the returned response. This method is protected by a circuit
     * breaker and retry policy; a configured fallback method will be invoked if the circuit breaker opens.
     *
     * @return a SyncResponseDto summarizing the run: status (e.g., "SUCCESS", "FAILED", or fallback state),
     *         a human-readable message, the number of records successfully processed, and the generated syncId
     */
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
    
    /**
     * Asynchronously processes a single real-time payload: parses the input, persists the resulting
     * metrics to Redis (real-time view) and MongoDB (historical store), and logs success or delegates
     * failures to centralized error handling.
     *
     * @param rawData the raw payload string containing metric data (expected format parseable by DataParser)
     */
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
    
    /**
     * Centralized failure handler for processing errors.
     *
     * <p>Records a processing failure for the given data and error message. Intended as a single
     * place to route failed payloads to a dead-letter queue, retry mechanism, or alerting; current
     * implementation only logs the failure.
     *
     * @param errorMessage human-readable error message describing the failure
     * @param data the raw or serialized payload that failed processing
     */
    @Override
    public void handleFailure(String errorMessage, String data) {
        log.error("Handling failure: {} for  {}", errorMessage, data);
        // Implement dead letter queue or retry mechanism
        // For now, just log the failure
    }
    
    /**
     * Processes a single SqlServerData record: parses its payload into a SolarMetricsDto
     * and persists the resulting metrics to Redis (real-time view) and MongoDB (historical).
     *
     * @param data the SQL Server record whose dataContent will be parsed and persisted
     */
    private void processingSingleRecord(SqlServerData data) {
        SolarMetricsDto metrics = dataParser.parseToMetrics(data.getDataContent());
        
        // Save to Redis
        redisService.saveMetrics(metrics.getMachineId(), metrics);
        
        // Save to MongoDB
        mongoService.saveMetrics(metrics);
        
        log.debug("Processed record for machine: {}", metrics.getMachineId());
    }
    
    /**
     * Circuit-breaker fallback for the data-processing pipeline.
     *
     * Returns a SyncResponseDto indicating the pipeline is unavailable when the circuit breaker is open.
     *
     * @param ex the triggering exception from the primary pipeline execution; its message is included in the response
     * @return a SyncResponseDto with status "CIRCUIT_BREAKER_OPEN", zero processed records, and a "FALLBACK" syncId
     */
    public SyncResponseDto processDataPipelineFallback(Exception ex) {
        return SyncResponseDto.builder()
            .status("CIRCUIT_BREAKER_OPEN")
            .message("Service temporarily unavailable: " + ex.getMessage())
            .processedRecords(0)
            .syncId("FALLBACK")
            .build();
    }
}