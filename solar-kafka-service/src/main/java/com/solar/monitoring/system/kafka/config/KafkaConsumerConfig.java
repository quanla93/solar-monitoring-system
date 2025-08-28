package com.solar.monitoring.system.kafka.config;

import com.solar.monitoring.system.kafka.model.SolarEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Creates a ConsumerFactory for consuming SolarEvent messages from Kafka.
     *
     * Configures bootstrap servers, consumer group id, key and value deserializers
     * (String for keys, JSON for SolarEvent values), sets the trusted package for
     * JSON deserialization to "com.solar.monitoring.system.*" and sets offset reset
     * to "earliest".
     *
     * @return a configured ConsumerFactory<String, SolarEvent>
     */
    @Bean
    public ConsumerFactory<String, SolarEvent> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.solar.monitoring.system.*");
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(),
                new JsonDeserializer<>(SolarEvent.class));
    }

    /**
     * Spring bean that creates a ConcurrentKafkaListenerContainerFactory for consuming SolarEvent messages.
     *
     * The returned factory is configured to use this class's ConsumerFactory<String, SolarEvent> for consumer instances.
     *
     * @return a configured ConcurrentKafkaListenerContainerFactory for String keys and SolarEvent values
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SolarEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SolarEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
