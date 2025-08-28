package com.solar.monitoring.system.kafka.config;
import com.solar.monitoring.system.kafka.model.SolarEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Creates a ProducerFactory configured for producing String-keyed, JSON-serialized SolarEvent messages to Kafka.
     *
     * <p>The factory is configured with the bootstrap servers from the application's
     * {@code spring.kafka.bootstrap-servers} property, String key serializer, JSON value serializer,
     * and delivery reliability settings (acks = "all", retries = 3, idempotence enabled).</p>
     *
     * @return a ProducerFactory<String, SolarEvent> ready for use by a KafkaTemplate
     */
    @Bean
    public ProducerFactory<String, SolarEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Creates a KafkaTemplate for sending String-keyed, JSON-serialized SolarEvent messages.
     *
     * <p>The template is constructed from the {@link #producerFactory()} bean and is configured
     * to produce messages with String keys and {@code SolarEvent} values serialized as JSON.
     *
     * @return a configured {@code KafkaTemplate<String, SolarEvent>} for publishing events to Kafka
     */
    @Bean
    public KafkaTemplate<String, SolarEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}