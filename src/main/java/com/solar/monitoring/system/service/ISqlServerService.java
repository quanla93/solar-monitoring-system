package com.solar.monitoring.system.service;

import com.solar.monitoring.system.model.SqlServerData;

import java.util.List;

public interface ISqlServerService {
    /**
 * Retrieve all SqlServerData records that have not yet been processed.
 *
 * @return a list of unprocessed {@link com.solar.monitoring.system.model.SqlServerData} records
 */
List<SqlServerData> fetchUnprocessedData();
    /**
 * Mark the SqlServerData record with the given identifier as processed.
 *
 * @param id the identifier of the SqlServerData to mark as processed
 */
void markAsProcessed(Long id);
    /**
 * Persists the provided raw SqlServerData and returns the saved entity.
 *
 * The returned instance will reflect any changes applied by persistence (for example,
 * generated identifiers or timestamps).
 *
 * @param data the raw SqlServerData to save
 * @return the persisted SqlServerData instance
 */
SqlServerData saveRawData(SqlServerData data);
}