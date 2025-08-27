package com.solar.monitoring.system.service;

import com.solar.monitoring.system.model.SqlServerData;
import com.solar.monitoring.system.repository.ISqlServerDataRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SqlServerServiceImpl implements ISqlServerService {
    
    @Autowired
    private ISqlServerDataRepository sqlServerDataRepository;
    
    /**
     * Retrieves all SqlServerData records that are not marked as processed, ordered by creation time ascending.
     *
     * @return a list of unprocessed SqlServerData entities ordered by createdAt (oldest first)
     */
    @Override
    @Retry(name = "sqlserver")
    public List<SqlServerData> fetchUnprocessedData() {
        try {
            List<SqlServerData> unprocessedData = sqlServerDataRepository.findByProcessedFalseOrderByCreatedAtAsc();
            log.info("Fetched {} unprocessed records from SQL Server", unprocessedData.size());
            return unprocessedData;
        } catch (Exception e) {
            log.error("Failed to fetch unprocessed data: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Marks the SqlServerData record with the given id as processed.
     *
     * Updates the persistent record identified by the provided primary key so it is no longer considered unprocessed.
     *
     * @param id the ID of the SqlServerData record to mark as processed
     */
    @Override
    @Transactional
    @Retry(name = "sqlserver")
    public void markAsProcessed(Long id) {
        try {
            sqlServerDataRepository.markAsProcessed(id);
            log.info("Marked record {} as processed", id);
        } catch (Exception e) {
            log.error("Failed to mark record {} as processed: {}", id, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Persists the given SqlServerData and returns the saved entity.
     *
     * The returned instance reflects any modifications made by the repository (for example generated IDs or auditing timestamps).
     *
     * @param data the SqlServerData to persist
     * @return the saved SqlServerData instance
     */
    @Override
    @Retry(name = "sqlserver")
    public SqlServerData saveRawData(SqlServerData data) {
        try {
            SqlServerData saved = sqlServerDataRepository.save(data);
            log.info("Saved raw data for machine: {}", data.getMachineId());
            return saved;
        } catch (Exception e) {
            log.error("Failed to save raw  {}", e.getMessage());
            throw e;
        }
    }
}