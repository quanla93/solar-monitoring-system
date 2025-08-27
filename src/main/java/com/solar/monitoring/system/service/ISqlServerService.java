package com.solar.monitoring.system.service;

import com.solar.monitoring.system.model.SqlServerData;

import java.util.List;

public interface ISqlServerService {
    List<SqlServerData> fetchUnprocessedData();
    void markAsProcessed(Long id);
    SqlServerData saveRawData(SqlServerData data);
}