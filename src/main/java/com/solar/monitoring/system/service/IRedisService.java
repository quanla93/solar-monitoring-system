package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.SolarMetricsDto;

import java.util.Optional;

public interface IRedisService {
    /**
 * Persist the given solar metrics in Redis under the provided machine identifier.
 *
 * Implementations should store or update the metrics associated with the specified
 * machineId so they can be retrieved later via getMetrics. The method is intended
 * to perform a single put/replace operation for the given key.
 *
 * @param machineId unique identifier of the machine whose metrics are being saved
 * @param metrics   the metrics payload to persist
 */
void saveMetrics(String machineId, SolarMetricsDto metrics);
    /**
 * Retrieves the stored SolarMetricsDto for the given machine identifier.
 *
 * @param machineId the unique identifier of the machine whose metrics are being requested
 * @return an Optional containing the metrics if present, or an empty Optional if no metrics are stored for the given machineId
 */
Optional<SolarMetricsDto> getMetrics(String machineId);
    /**
 * Delete stored metrics for the given machine identifier.
 *
 * Removes any Redis entry associated with the provided machineId. Implementations may perform a no-op
 * if no metrics exist for that id.
 *
 * @param machineId unique identifier of the machine whose metrics should be deleted; must be non-null
 */
void deleteMetrics(String machineId);
    /**
 * Checks whether metrics for the given machine ID are stored in Redis.
 *
 * @param machineId the unique identifier of the machine whose metrics presence is being checked
 * @return true if metrics exist for the specified machineId, false otherwise
 */
boolean exists(String machineId);
}