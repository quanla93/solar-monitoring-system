package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.HistoryQueryDto;
import com.solar.monitoring.system.dto.SolarMetricsDto;
import org.springframework.data.domain.Page;

public interface IMongoService {
    /**
 * Persist the provided solar metrics and return the stored representation.
 *
 * <p>Persists the given {@code SolarMetricsDto} to the underlying MongoDB store and returns
 * the persisted instance, which may include database-generated fields (for example an id or
 * timestamps) populated by the persistence layer.</p>
 *
 * @param metrics the solar metrics to save
 * @return the persisted {@link SolarMetricsDto}, potentially augmented with DB-generated fields
 */
SolarMetricsDto saveMetrics(SolarMetricsDto metrics);
    /**
 * Retrieve a page of historical solar metrics that match the provided query criteria.
 *
 * The query DTO supplies filtering and paging parameters (for example: time range, filters, sorting, and pagination).
 *
 * @param query the history query parameters used to filter and page results
 * @return a page of SolarMetricsDto containing metrics that match the query
 */
Page<SolarMetricsDto> getHistoricalData(HistoryQueryDto query);
    /**
 * Remove solar metrics older than the specified retention period.
 *
 * Deletes stored metric records whose timestamp is older than the current time minus the given
 * number of days, retaining only data within the specified window.
 *
 * @param daysToKeep number of days to retain; records older than this many days will be deleted
 */
void deleteOldData(int daysToKeep);
}