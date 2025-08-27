package com.solar.monitoring.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableKafka
@EnableAsync
@EnableScheduling
public class SolarMonitoringSystemApplication {

    /**
     * Application entry point; bootstraps and starts the Spring application context.
     *
     * Invokes SpringApplication.run(...) to initialize and launch the Spring Boot
     * application (Kafka, async processing, and scheduling are enabled via class
     * annotations).
     *
     * @param args command-line arguments forwarded to SpringApplication
     */
    public static void main(String[] args) {
        SpringApplication.run(SolarMonitoringSystemApplication.class, args);
    }
}