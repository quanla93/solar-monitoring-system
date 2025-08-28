package com.solar.monitoring.system.kafka.service;

import com.solar.monitoring.system.kafka.model.SolarEvent;

public interface IKafkaConsumerService {
    /**
 * Handle a SolarEvent received from an external source (e.g., an external Kafka topic).
 *
 * Implementations should perform whatever processing or routing is required for externally
 * sourced events represented by the provided SolarEvent.
 *
 * @param event the external SolarEvent to handle
 */
void handleExternalEvent(SolarEvent event);
    /**
 * Handle an event indicating that raw solar data has been processed and is ready for downstream use.
 *
 * Implementations should perform any further processing, persistence, or propagation required when
 * processed data becomes available (for example updating application state, storing results, or
 * publishing to other systems).
 *
 * @param event the processed solar event carrying aggregated or transformed data and associated metadata
 */
void handleDataProcessedEvent(SolarEvent event);
}
