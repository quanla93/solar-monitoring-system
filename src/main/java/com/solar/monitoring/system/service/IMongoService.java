package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.HistoryQueryDto;
import com.solar.monitoring.system.dto.SolarMetricsDto;
import org.springframework.data.domain.Page;

public interface IMongoService {
    SolarMetricsDto saveMetrics(SolarMetricsDto metrics);
    Page<SolarMetricsDto> getHistoricalData(HistoryQueryDto query);
    void deleteOldData(int daysToKeep);
}