# solar-monitoring-system
A comprehensive Spring Boot application for monitoring solar power plant metrics with real-time processing and historical data analysis.

solar-monitoring-system/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── solarplant/
    │   │           ├── SolarMonitoringApplication.java
    │   │           ├── config/
    │   │           │   ├── KafkaConfig.java
    │   │           │   └── RabbitMQConfig.java
    │   │           ├── controller/
    │   │           │   ├── SolarMetricsController.java
    │   │           │   ├── SyncController.java
    │   │           │   └── HealthController.java
    │   │           ├── document/
    │   │           │   └── SolarMetrics.java
    │   │           ├── dto/
    │   │           │   ├── request/
    │   │           │   │   ├── MetricsFilterRequest.java
    │   │           │   │   └── SyncRequest.java
    │   │           │   └── response/
    │   │           │       ├── SolarMetricsResponse.java
    │   │           │       ├── RealtimeMetricsResponse.java
    │   │           │       ├── SystemHealthResponse.java
    │   │           │       ├── SyncStatusResponse.java
    │   │           │       └── PagedResponse.java
    │   │           ├── entity/
    │   │           │   └── SolarRawData.java
    │   │           ├── exception/
    │   │           │   └── GlobalExceptionHandler.java
    │   │           ├── mapper/
    │   │           │   └── SolarMetricsMapper.java
    │   │           ├── messaging/
    │   │           │   ├── kafka/
    │   │           │   │   └── SolarMetricsKafkaConsumer.java
    │   │           │   └── rabbitmq/
    │   │           │       └── SolarMetricsRabbitConsumer.java
    │   │           ├── model/
    │   │           │   └── SolarRealtimeMetrics.java
    │   │           ├── repository/
    │   │           │   ├── jpa/
    │   │           │   │   └── SolarRawDataRepository.java
    │   │           │   ├── mongo/
    │   │           │   │   └── SolarMetricsRepository.java
    │   │           │   └── redis/
    │   │           │       └── SolarRealtimeMetricsRepository.java
    │   │           └── service/
    │   │               ├── SolarMetricsService.java
    │   │               ├── DataProcessingService.java
    │   │               ├── SyncService.java
    │   │               ├── HealthService.java
    │   │               ├── BackupService.java
    │   │               └── SchedulerService.java
    │   └── resources/
    │       ├── application.yml
    │       └── application-test.yml
    └── test/
        └── java/
            └── com/
                └── solarplant/
                    ├── integration/
                    │   ├── SolarMetricsIntegrationTest.java
                    │   └── KafkaIntegrationTest.java
                    └── service/
                        ├── DataProcessingServiceTest.java
                        └── SolarMetricsServiceTest.java

Features
Core Functionality

Multi-source Data Ingestion: SQL Server database, Kafka, and RabbitMQ
Real-time Processing: Immediate parsing and processing of XML/JSON data
Dual Storage: Redis for real-time access, MongoDB for historical data
Atomic Operations: Ensures data consistency across Redis and MongoDB
Error Handling: Circuit breakers, retries, fallbacks, and backup strategies

APIs

Real-time Metrics: Fetch current metrics from Redis cache
Historical Data: Query MongoDB with advanced filtering
Manual Synchronization: Trigger data sync between SQL Server and MongoDB
System Health: Comprehensive health monitoring for all components

Technology Stack

Spring Boot 3.x with Java 17
Spring Data JPA for SQL Server integration
Spring Data MongoDB for document storage
Spring Data Redis for caching and real-time data
Spring Kafka and Spring AMQP for message processing
MapStruct for object mapping
Lombok for boilerplate reduction
Resilience4j for fault tolerance
JUnit 5 + Testcontainers for comprehensive testing

Quick Start
Prerequisites

Java 17+
Maven 3.6+
Docker and Docker Compose (for databases and message brokers)
