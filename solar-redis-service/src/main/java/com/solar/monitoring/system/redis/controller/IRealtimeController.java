package com.solar.monitoring.system.redis.controller;

import com.solar.monitoring.system.redis.dto.RealtimeDataDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface IRealtimeController {
    ResponseEntity<RealtimeDataDto> getRealtimeData(@PathVariable String machineId);
}