# Solar Power Plant Monitoring System

A comprehensive Spring Boot application for monitoring solar power plant metrics with real-time processing and historical data analysis.

## Project Structure

```
solar-monitoring-system/
├── docker-compose.yml
├── README.md
├── solar-data-processing-service/
│   ├── src/main/java/com/solar/monitoring/system/dataprocessing/
│   │   ├── SolarDataProcessingServiceApplication.java
│   │   ├── config/
│   │   │   ├── DatabaseConfig.java
│   │   │   ├── KafkaProducerConfig.java
│   │   │   └── RedisConfig.java
│   │   ├── controller/
│   │   │   ├── IDataController.java
│   │   │   ├── AbstractDataController.java
│   │   │   └── DataControllerImpl.java
│   │   ├── dto/
│   │   │   ├── SolarDataRequestDto.java
│   │   │   ├── SolarDataResponseDto.java
│   │   │   └── HistoryQueryDto.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── DataProcessingException.java
│   │   │   └── ValidationException.java
│   │   ├── model/
│   │   │   ├── SolarData.java
│   │   │   ├── Machine.java
│   │   │   └── HistoricalData.java
│   │   ├── repository/
│   │   │   ├── ISolarDataRepository.java
│   │   │   ├── AbstractSolarDataRepository.java
│   │   │   ├── SolarDataRepositoryImpl.java
│   │   │   ├── IMachineRepository.java
│   │   │   └── MachineRepositoryImpl.java
│   │   ├── service/
│   │   │   ├── IDataProcessingService.java
│   │   │   ├── AbstractDataProcessingService.java
│   │   │   ├── DataProcessingServiceImpl.java
│   │   │   ├── IRedisIntegrationService.java
│   │   │   ├── AbstractRedisIntegrationService.java
│   │   │   ├── RedisIntegrationServiceImpl.java
│   │   │   ├── IKafkaIntegrationService.java
│   │   │   ├── AbstractKafkaIntegrationService.java
│   │   │   └── KafkaIntegrationServiceImpl.java
│   │   └── util/
│   │       ├── DataMapper.java
│   │       └── ValidationUtil.java
│   ├── src/main/resources/
│   │   └── application.yml
│   ├── src/test/java/
│   ├── pom.xml
│   └── Dockerfile
├── solar-redis-service/
│   ├── src/main/java/com/solar/monitoring/system/redis/
│   │   ├── SolarRedisServiceApplication.java
│   │   ├── config/
│   │   │   └── RedisConfig.java
│   │   ├── controller/
│   │   │   ├── IRealtimeController.java
│   │   │   ├── AbstractRealtimeController.java
│   │   │   └── RealtimeControllerImpl.java
│   │   ├── dto/
│   │   │   └── RealtimeDataDto.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── RedisException.java
│   │   ├── model/
│   │   │   └── RealtimeMetrics.java
│   │   ├── repository/
│   │   │   ├── IRedisRepository.java
│   │   │   ├── AbstractRedisRepository.java
│   │   │   └── RedisRepositoryImpl.java
│   │   ├── service/
│   │   │   ├── IRealtimeService.java
│   │   │   ├── AbstractRealtimeService.java
│   │   │   └── RealtimeServiceImpl.java
│   │   └── util/
│   │       └── RedisKeyUtil.java
│   ├── src/main/resources/
│   │   └── application.yml
│   ├── pom.xml
│   └── Dockerfile
└── solar-kafka-service/
    ├── src/main/java/com/solar/monitoring/system/kafka/
    │   ├── SolarKafkaServiceApplication.java
    │   ├── config/
    │   │   ├── KafkaConsumerConfig.java
    │   │   └── KafkaProducerConfig.java
    │   ├── controller/
    │   │   ├── IKafkaController.java
    │   │   ├── AbstractKafkaController.java
    │   │   └── KafkaControllerImpl.java
    │   ├── dto/
    │   │   ├── KafkaMessageDto.java
    │   │   └── EventDto.java
    │   ├── exception/
    │   │   ├── GlobalExceptionHandler.java
    │   │   └── KafkaException.java
    │   ├── model/
    │   │   └── SolarEvent.java
    │   ├── service/
    │   │   ├── IKafkaConsumerService.java
    │   │   ├── AbstractKafkaConsumerService.java
    │   │   ├── KafkaConsumerServiceImpl.java
    │   │   ├── IKafkaProducerService.java
    │   │   ├── AbstractKafkaProducerService.java
    │   │   └── KafkaProducerServiceImpl.java
    │   └── util/
    │       └── MessageUtil.java
    ├── src/main/resources/
    │   └── application.yml
    ├── pom.xml
    └── Dockerfile
```

## Features

### Core Functionality
- **Multi-source Data Ingestion**: SQL Server database, Kafka, and RabbitMQ
- **Real-time Processing**: Immediate parsing and processing of XML/JSON data
- **Dual Storage**: Redis for real-time access, MongoDB for historical data
- **Atomic Operations**: Ensures data consistency across Redis and MongoDB
- **Error Handling**: Circuit breakers, retries, fallbacks, and backup strategies

### APIs
- **Real-time Metrics**: Fetch current metrics from Redis cache
- **Historical Data**: Query MongoDB with advanced filtering
- **Manual Synchronization**: Trigger data sync between SQL Server and MongoDB
- **System Health**: Comprehensive health monitoring for all components

### Technology Stack
- **Spring Boot 3.x** with Java 17
- **Spring Data JPA** for SQL Server integration
- **Spring Data MongoDB** for document storage
- **Spring Data Redis** for caching and real-time data
- **Spring Kafka** and **Spring AMQP** for message processing
- **MapStruct** for object mapping
- **Lombok** for boilerplate reduction
- **Resilience4j** for fault tolerance
- **JUnit 5 + Testcontainers** for comprehensive testing

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker and Docker Compose (for databases and message brokers)
## Configuration

### Production Considerations
- Use externalized configuration management
- Implement proper security (authentication, authorization)
- Set up monitoring and alerting
- Configure log aggregation
- Implement data retention policies
- Set up automated backups

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement changes with tests
4. Submit a pull request

## License

This project is licensed under the MIT License.
