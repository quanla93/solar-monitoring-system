package com.solar.monitoring.system.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class SolarKafkaServiceApplication {
    /**
     * Application entry point that boots the Spring application context with Kafka enabled.
     *
     * Starts the Spring Boot application for the Solar Kafka service and forwards any
     * command-line arguments to SpringApplication.
     *
     * @param args command-line arguments passed through to SpringApplication.run
     */
    public static void main(String[] args) {
        SpringApplication.run(SolarKafkaServiceApplication.class, args);
    }
}