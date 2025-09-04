package com.solar.monitoring.system.kafka.service;
import com.solar.monitoring.system.kafka.dto.EventDto;

public interface IKafkaProducerService {
    /**
 * Publish the provided EventDto to the specified Kafka topic.
 *
 * The implementation is responsible for serializing the event and sending it to the given topic.
 *
 * @param topic the Kafka topic name to publish the event to
 * @param event the event payload to be published
 */
void publishEvent(String topic, EventDto event);
    /**
 * Publish an event that represents completed data processing to Kafka.
 *
 * Implementations are responsible for serializing the payload and sending it to the configured
 * "data-processed" topic or equivalent destination.
 *
 * @param event the EventDto describing the processed data (must not be null)
 */
void publishDataProcessedEvent(EventDto event);
}
