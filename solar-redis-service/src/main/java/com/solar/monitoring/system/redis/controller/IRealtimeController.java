package com.solar.monitoring.system.redis.controller;

import com.solar.monitoring.system.redis.dto.RealtimeDataDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface IRealtimeController {
    /**
 * Retrieve the latest real-time telemetry for a specific machine.
 *
 * @param machineId the unique identifier of the machine (provided as a URI path variable)
 * @return a ResponseEntity containing the RealtimeDataDto for the requested machine; HTTP status reflects success or failure
 */
ResponseEntity<RealtimeDataDto> getRealtimeData(@PathVariable String machineId);
}