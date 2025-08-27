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
    
    @GetMapping("/realtime/{machineId}")
    public ResponseEntity<SolarMetricsDto> getRealtimeMetrics(@PathVariable String machineId) {
        log.info("Fetching real-time metrics for machine: {}", machineId);
        
        Optional<SolarMetricsDto> metrics = redisService.getMetrics(machineId);
        
        return metrics.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/history")
    public ResponseEntity<Page<SolarMetricsDto>> getHistoricalData(@Valid @ModelAttribute HistoryQueryDto query) {
        log.info("Fetching historical data with query: {}", query);
        
        Page<SolarMetricsDto> historicalData = mongoService.getHistoricalData(query);
        
        return ResponseEntity.ok(historicalData);
    }
    
    @PostMapping("/sync")
    public ResponseEntity<SyncResponseDto> triggerManualSync() {
        log.info("Triggering manual sync");
        
        SyncResponseDto response = dataProcessingService.processDataPipeline();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Solar Monitoring System is running");
    }
}