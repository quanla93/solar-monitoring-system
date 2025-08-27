package com.solar.monitoring.system.controller;

import com.solar.monitoring.system.dto.HistoryQueryDto;
import com.solar.monitoring.system.dto.SolarMetricsDto;
import com.solar.monitoring.system.dto.SyncResponseDto;
import com.solar.monitoring.system.service.IDataProcessingService;
import com.solar.monitoring.system.service.IMongoService;
import com.solar.monitoring.system.service.IRedisService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
public class SolarMonitoringController {
    
    @Autowired
    private IRedisService redisService;
    
    @Autowired
    private IMongoService mongoService;
    
    @Autowired
    private IDataProcessingService dataProcessingService;
    
    /**
     * Retrieves real-time solar metrics for the given machine.
     *
     * Queries the Redis-backed cache for a SolarMetricsDto for the provided machineId.
     * If metrics are found, returns 200 OK with the metrics; otherwise returns 404 Not Found.
     *
     * @param machineId the identifier of the machine to fetch metrics for
     * @return ResponseEntity containing the SolarMetricsDto with status 200, or 404 if not present
     */
    @GetMapping("/realtime/{machineId}")
    public ResponseEntity<SolarMetricsDto> getRealtimeMetrics(@PathVariable String machineId) {
        log.info("Fetching real-time metrics for machine: {}", machineId);
        
        Optional<SolarMetricsDto> metrics = redisService.getMetrics(machineId);
        
        return metrics.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Retrieves paginated historical solar metrics that match the provided query.
     *
     * The `query` DTO is populated from request parameters and validated; the method
     * returns a page of SolarMetricsDto containing the matching historical records.
     *
     * @param query criteria (time range, machine id, pagination, etc.) used to filter historical metrics
     * @return a Page of SolarMetricsDto matching the query
     */
    @GetMapping("/history")
    public ResponseEntity<Page<SolarMetricsDto>> getHistoricalData(@Valid @ModelAttribute HistoryQueryDto query) {
        log.info("Fetching historical data with query: {}", query);
        
        Page<SolarMetricsDto> historicalData = mongoService.getHistoricalData(query);
        
        return ResponseEntity.ok(historicalData);
    }
    
    /**
     * Triggers the data-processing pipeline and returns its result.
     *
     * <p>Initiates a manual synchronization run via the data processing service and returns
     * the resulting SyncResponseDto wrapped in a 200 OK ResponseEntity.</p>
     *
     * @return ResponseEntity containing the SyncResponseDto produced by the processing pipeline
     */
    @PostMapping("/sync")
    public ResponseEntity<SyncResponseDto> triggerManualSync() {
        log.info("Triggering manual sync");
        
        SyncResponseDto response = dataProcessingService.processDataPipeline();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check endpoint for the Solar Monitoring System.
     *
     * <p>Returns a 200 OK response with a brief status message when the service is operational.</p>
     *
     * @return ResponseEntity containing the plain-text health message ("Solar Monitoring System is running").
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Solar Monitoring System is running");
    }
}