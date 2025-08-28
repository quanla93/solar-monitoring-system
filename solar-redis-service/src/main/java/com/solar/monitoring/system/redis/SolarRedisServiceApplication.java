package com.solar.monitoring.system.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SolarRedisServiceApplication {
    /**
     * Application entry point that boots the Spring Boot context for the Redis service.
     *
     * @param args command-line arguments forwarded to SpringApplication (e.g., profiles and standard Spring Boot flags)
     */
    public static void main(String[] args) {
        SpringApplication.run(SolarRedisServiceApplication.class, args);
    }
}