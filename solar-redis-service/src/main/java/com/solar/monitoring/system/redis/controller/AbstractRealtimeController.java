package com.solar.monitoring.system.redis.controller;

import com.solar.monitoring.system.redis.service.IRealtimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public abstract class AbstractRealtimeController implements IRealtimeController {
    protected final IRealtimeService realtimeService;

    /**
     * Logs an info-level message indicating that realtime data for the given machine is being retrieved.
     *
     * @param machineId the identifier of the machine whose realtime data is being requested
     */
    protected void logRequest(String machineId) {
        log.info("Retrieving realtime data for machine: {}", machineId);
    }
}