package com.solar.monitoring.system.repository;

import com.solar.monitoring.system.model.SolarMetrics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ISolarMetricsRepository extends MongoRepository<SolarMetrics, String> {
    
    /**
     * Retrieves a page of SolarMetrics for the given machine whose timestamp falls between the two supplied dates (inclusive).
     *
     * @param machineId the identifier of the machine to filter metrics for
     * @param startDate lower bound of the timestamp range (inclusive)
     * @param endDate   upper bound of the timestamp range (inclusive)
     * @param pageable  pagination and sorting configuration for the result page
     * @return a page of SolarMetrics matching the machineId and timestamp range
     */
    Page<SolarMetrics> findByMachineIdAndTimestampBetween(
        String machineId, 
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );
    
    /**
     * Returns a page of SolarMetrics with timestamps between the given start and end datetimes.
     *
     * The range is inclusive of the provided bounds. Results are returned according to the supplied
     * Pageable (page size, page number and sort).
     *
     * @param startDate the start of the timestamp range (inclusive)
     * @param endDate   the end of the timestamp range (inclusive)
     * @param pageable  pagination and sorting information for the query
     * @return a page of SolarMetrics matching the timestamp range
     */
    Page<SolarMetrics> findByTimestampBetween(
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );
    
    /**
 * Delete all SolarMetrics documents whose {@code timestamp} is strictly before the given cutoff.
 *
 * @param cutoffDate the exclusive upper bound; documents with {@code timestamp} < {@code cutoffDate} will be removed
 * @return the number of documents deleted
 */
long deleteByTimestampBefore(LocalDateTime cutoffDate);
}