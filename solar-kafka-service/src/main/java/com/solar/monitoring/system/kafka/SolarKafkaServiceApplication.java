package com.solar.monitoring.system.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class SolarKafkaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SolarKafkaServiceApplication.class, args);
    }
}