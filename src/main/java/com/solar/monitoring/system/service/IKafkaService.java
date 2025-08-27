package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.SolarMetricsDto;

public interface IKafkaService {
    /**
 * Send the given SolarMetricsDto to the specified Kafka topic.
 *
 * Implementations are responsible for delivering the provided payload to the named Kafka topic (including any required serialization and producer configuration).
 *
 * @param topic   the destination Kafka topic name
 * @param message the metrics payload to send
 */
void sendMessage(String topic, SolarMetricsDto message);
    /**
 * Process a raw message payload received from Kafka.
 *
 * Implementations should parse the provided message string and apply any
 * necessary business logic (for example, converting to domain DTOs and
 * persisting or forwarding metrics).
 *
 * @param message the raw message payload (e.g., a JSON-encoded metrics object)
 */
void consumeMessage(String message);
}