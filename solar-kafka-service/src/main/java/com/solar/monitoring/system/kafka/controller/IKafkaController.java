package com.solar.monitoring.system.kafka.controller;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IKafkaController {
    ResponseEntity<Map<String, String>> health();
}