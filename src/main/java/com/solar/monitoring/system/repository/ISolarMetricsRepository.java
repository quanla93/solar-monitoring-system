package com.solar.monitoring.system.repository;

import com.solar.monitoring.system.model.SolarMetrics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ISolarMetricsRepository extends MongoRepository<SolarMetrics, String> {
    
    Page<SolarMetrics> findByMachineIdAndTimestampBetween(
        String machineId, 
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );
    
    Page<SolarMetrics> findByTimestampBetween(
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );
    
    long deleteByTimestampBefore(LocalDateTime cutoffDate);
}