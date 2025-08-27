package com.solar.monitoring.system.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    
    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;
    
    @Value("${spring.kafka.consumer.group-id:solar-monitoring-group}")
    private String groupId;
    
    /**
     * Creates and configures a ProducerFactory<String, String> for producing Kafka messages.
     *
     * The returned factory is initialized with the configured bootstrap servers, uses
     * String serializers for keys and values, sets acknowledgments to "all", and configures
     * the producer to retry up to 3 times.
     *
     * @return a ProducerFactory for String key/value messages
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    /**
     * Creates and exposes a KafkaTemplate<String, String> bean for sending string messages to Kafka.
     *
     * The template is backed by this configuration's ProducerFactory and is suitable for producing
     * string key/value messages to the configured bootstrap servers.
     *
     * @return a KafkaTemplate configured with the class's ProducerFactory
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    /**
     * Creates a ConsumerFactory<String, String> bean configured for the application's Kafka consumers.
     *
     * The returned factory is initialized with the configured bootstrap servers and consumer group,
     * uses StringDeserializer for both keys and values, and sets `auto.offset.reset` to "earliest".
     *
     * @return a DefaultKafkaConsumerFactory configured for String key/value deserialization
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    /**
     * Creates and returns a ConcurrentKafkaListenerContainerFactory configured for String keys and values.
     *
     * This factory is wired with the class's ConsumerFactory and is intended for use by Spring's
     * @KafkaListener infrastructure to create listener containers.
     *
     * @return a ConcurrentKafkaListenerContainerFactory<String, String> configured with the consumer factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}