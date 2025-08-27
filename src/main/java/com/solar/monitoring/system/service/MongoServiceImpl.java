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
    
    /**
     * Persists the given solar metrics to MongoDB, ensuring a timestamp is assigned.
     *
     * If the provided DTO has a null timestamp, the current LocalDateTime is set before saving.
     *
     * @param metrics the metrics to save; may have a null timestamp which will be replaced with the current time
     * @return the saved metrics mapped back to a SolarMetricsDto (including any persisted fields such as id and timestamp)
     */
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
    
    /**
     * Retrieves a paged list of historical solar metrics matching the given query.
     *
     * The query's time range (startDate..endDate) is always applied. If query.getMachineId()
     * is non-null and non-empty, results are additionally filtered by that machineId.
     * Pagination and sorting are taken from the pageable created by createPageable(query).
     *
     * @param query contains the time range (startDate, endDate), optional machineId, and pagination/sorting parameters
     * @return a Page of SolarMetricsDto matching the query
     */
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
    
    /**
     * Deletes metrics older than the specified number of days from the MongoDB repository.
     *
     * <p>Computes a cutoff timestamp as now minus {@code daysToKeep} days and removes all records
     * with a timestamp before that cutoff.
     *
     * @param daysToKeep number of days to retain; records older than this will be deleted
     * @throws RuntimeException if the deletion fails (the original exception is propagated)
     */
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