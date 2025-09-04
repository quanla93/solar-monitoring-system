package com.solar.monitoring.system.redis.service;

import com.solar.monitoring.system.redis.dto.RealtimeDataDto;

import java.util.Optional;

public interface IRealtimeService {
    /**
 * Persist or update realtime solar metrics for a machine in Redis.
 *
 * Store or replace the provided RealtimeDataDto under the given machineId so it can
 * be retrieved later via getRealtimeMetrics(String). Intended as a single put/replace
 * operation for the key.
 *
 * @param machineId non-null unique identifier of the machine whose metrics are being saved
 * @param metrics   the realtime metrics payload to persist
 */
    void saveMetrics(String machineId, RealtimeDataDto metrics);
    /**
 * Retrieve the realtime metrics for the given machine.
 *
 * @param machineId the machine identifier whose realtime metrics are requested
 * @return an Optional containing the RealtimeDataDto if metrics exist for the given machineId, otherwise an empty Optional
 */
    Optional<RealtimeDataDto> getRealtimeMetrics(String machineId);

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
 * Returns whether realtime metrics for the given machine ID exist in Redis.
 *
 * @param machineId the machine's unique identifier
 * @return true if metrics are present for the specified machineId, false otherwise
 */
    boolean exists(String machineId);
}
