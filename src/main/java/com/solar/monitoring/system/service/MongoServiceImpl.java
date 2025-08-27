package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.HistoryQueryDto;
import com.solar.monitoring.system.dto.SolarMetricsDto;
import com.solar.monitoring.system.model.SolarMetrics;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class MongoServiceImpl extends AbstractMongoService {
    
    @Override
    @Retry(name = "mongodb")
    public SolarMetricsDto saveMetrics(SolarMetricsDto metrics) {
        try {
            SolarMetrics entity = mapToEntity(metrics);
            if (entity.getTimestamp() == null) {
                entity.setTimestamp(LocalDateTime.now());
            }
            SolarMetrics saved = solarMetricsRepository.save(entity);
            log.info("Saved metrics to MongoDB for machine: {}", metrics.getMachineId());
            return mapToDto(saved);
        } catch (Exception e) {
            log.error("Failed to save metrics to MongoDB: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Page<SolarMetricsDto> getHistoricalData(HistoryQueryDto query) {
        try {
            Pageable pageable = createPageable(query);
            Page<SolarMetrics> page;
            
            if (query.getMachineId() != null && !query.getMachineId().isEmpty()) {
                page = solarMetricsRepository.findByMachineIdAndTimestampBetween(
                    query.getMachineId(),
                    query.getStartDate(),
                    query.getEndDate(),
                    pageable
                );
            } else {
                page = solarMetricsRepository.findByTimestampBetween(
                    query.getStartDate(),
                    query.getEndDate(),
                    pageable
                );
            }
            
            return page.map(this::mapToDto);
        } catch (Exception e) {
            log.error("Failed to fetch historical  {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    @Retry(name = "mongodb")
    public void deleteOldData(int daysToKeep) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
            long deletedCount = solarMetricsRepository.deleteByTimestampBefore(cutoffDate);
            log.info("Deleted {} old records before {}", deletedCount, cutoffDate);
        } catch (Exception e) {
            log.error("Failed to delete old data: {}", e.getMessage());
            throw e;
        }
    }
}