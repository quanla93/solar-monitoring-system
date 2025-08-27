package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.SyncResponseDto;

public interface IDataProcessingService {
    /**
 * Executes the full data-processing pipeline and returns a synchronous summary of the result.
 *
 * The pipeline may encompass batch ingestion, validation, enrichment, aggregation, and persistence
 * steps; implementations decide exact behavior and error handling. Use this method when a caller
 * requires a consolidated, synchronous response about the pipeline run.
 *
 * @return a SyncResponseDto containing status, metrics, and any relevant error or summary information
 */
SyncResponseDto processDataPipeline();
    /**
 * Process a single raw real-time data payload.
 *
 * <p>Ingests the provided rawData into the system's real-time processing flow. Implementations
 * should validate and transform the payload as needed and route it to downstream consumers
 * (e.g., enrichers, aggregators, storage, or alerting). This method is intended for
 * individual telemetry or event messages received in real time.
 *
 * @param rawData the raw input payload (for example, a JSON or delimited string) representing
 *                one telemetry/event message; implementations should handle null or malformed
 *                values according to their error-handling policy
 */
void processRealTimeData(String rawData);
    /**
 * Handle a processing failure for the given payload.
 *
 * Implementations should record or react to an error that occurred while handling the provided data.
 *
 * @param errorMessage a short description or code identifying the failure
 * @param data the raw input or payload associated with the failure (may be null or truncated)
 */
void handleFailure(String errorMessage, String data);
}