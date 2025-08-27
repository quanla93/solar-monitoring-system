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