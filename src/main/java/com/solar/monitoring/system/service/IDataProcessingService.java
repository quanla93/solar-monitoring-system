package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.SyncResponseDto;

public interface IDataProcessingService {
    SyncResponseDto processDataPipeline();
    void processRealTimeData(String rawData);
    void handleFailure(String errorMessage, String data);
}