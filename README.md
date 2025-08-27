# Solar Power Plant Monitoring System

A comprehensive Spring Boot application for monitoring solar power plant metrics with real-time processing and historical data analysis.

## Project Structure

```
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

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker and Docker Compose (for databases and message brokers)

### Database Setup

1. **SQL Server**:
```sql
CREATE TABLE solar_raw_data (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    machine_id NVARCHAR(50) NOT NULL,
    plant_id NVARCHAR(50) NOT NULL,
    raw_data NVARCHAR(MAX) NOT NULL,
    data_type NVARCHAR(10) NOT NULL,
    created_at DATETIME2 NOT NULL,
    processed BIT NOT NULL DEFAULT 0
);

-- Sample data
INSERT INTO solar_raw_data (machine_id, plant_id, raw_data, data_type, created_at, processed)
VALUES 
('MACHINE_001', 'PLANT_A', '{"timestamp":"2024-01-15T10:30:00","energyProduction":125.5,"energyConsumption":95.2,"efficiency":85.5,"temperature":25.3,"irradiance":850.0,"status":"ONLINE"}', 'JSON', GETDATE(), 0),
('MACHINE_002', 'PLANT_A', '<metrics><timestamp>2024-01-15T10:31:00</timestamp><energyProduction>130.2</energyProduction><energyConsumption>98.1</energyConsumption><efficiency>87.2</efficiency><temperature>26.1</temperature><irradiance>875.0</irradiance><status>ONLINE</status></metrics>', 'XML', GETDATE(), 0);
```

2. **Docker Compose** (databases and message brokers):
```yaml
version: '3.8'
services:
  mongodb:
    image: mongo:7.0
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: password
      MONGO_INITDB_DATABASE: solar_metrics

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  kafka:
    image: confluentinc/cp-kafka:7.4.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  rabbitmq:
    image: rabbitmq:3.12-management
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
```

### Running the Application

1. **Start dependencies**:
```bash
docker-compose up -d
```

2. **Update configuration** in `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://your-sql-server:1433;databaseName=solar_plant
    username: your_username
    password: your_password
```

3. **Build and run**:
```bash
mvn clean install
mvn spring-boot:run
```

## API Documentation

### Real-time Metrics
```bash
# Get all real-time metrics
GET /api/v1/metrics/realtime

# Get metrics for specific plant
GET /api/v1/metrics/realtime?plantId=PLANT_A

# Get metrics for specific machine
GET /api/v1/metrics/realtime?plantId=PLANT_A&machineId=MACHINE_001
```

### Historical Metrics
```bash
# Query with filters
POST /api/v1/metrics/historical
Content-Type: application/json

{
  "plantId": "PLANT_A",
  "machineId": "MACHINE_001",
  "startDate": "2024-01-01 00:00:00",
  "endDate": "2024-01-31 23:59:59",
  "status": "ONLINE",
  "page": 0,
  "size": 20,
  "sortBy": "timestamp",
  "sortDirection": "desc"
}

# Query with parameters
GET /api/v1/metrics/historical?plantId=PLANT_A&page=0&size=10
```

### System Management
```bash
# Trigger manual sync
POST /api/v1/sync/trigger
{
  "plantId": "PLANT_A",
  "startDate": "2024-01-01 00:00:00",
  "endDate": "2024-01-31 23:59:59"
}

# Process unprocessed data
POST /api/v1/sync/process-unprocessed

# Check sync status
GET /api/v1/sync/status/{syncId}

# System health
GET /api/v1/health/status
```

## Message Broker Integration

### Kafka Message Format
```json
{
  "machineId": "MACHINE_001",
  "plantId": "PLANT_A",
  "dataType": "JSON",
  "timestamp": "2024-01-15T10:30:00",
  "data": "{\"energyProduction\":125.5,\"energyConsumption\":95.2,...}"
}
```

### RabbitMQ Message Format
Same as Kafka format. Messages are sent to the `solar-metrics-exchange` with routing key `solar.metrics`.

## Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn test -Dtest="**/*IntegrationTest"
```

### Run All Tests
```bash
mvn verify
```

## Monitoring and Observability

### Health Checks
- SQL Server connectivity
- MongoDB connectivity  
- Redis connectivity
- Kafka connectivity
- RabbitMQ connectivity

### Metrics Endpoints
- `/actuator/health` - Application health
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics

### Logging
- Structured logging with correlation IDs
- Error tracking with backup creation
- Performance monitoring

## Error Handling and Recovery

### Circuit Breaker Pattern
- MongoDB operations protected with circuit breaker
- Redis operations with fallback mechanisms
- Automatic recovery after service restoration

### Retry Mechanisms
- Configurable retry policies for data processing
- Exponential backoff for failed operations
- Dead letter queues for failed messages

### Backup and Recovery
- Automatic backup creation on processing failures
- Manual backup triggers for critical operations
- Rollback mechanisms for data inconsistencies

## Configuration

### Environment Variables
```bash
# Database
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export MONGO_HOST=localhost
export MONGO_PORT=27017
export REDIS_HOST=localhost
export REDIS_PORT=6379

# Message Brokers
export KAFKA_SERVERS=localhost:9092
export RABBITMQ_HOST=localhost
export RABBITMQ_PORT=5672

# Application
export BACKUP_DIR=/var/backup/solar-metrics
```

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
