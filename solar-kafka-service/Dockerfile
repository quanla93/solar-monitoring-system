# solar-kafka-service/Dockerfile
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Install necessary tools
RUN apt-get update && apt-get install -y \
    curl \
    netcat-traditional \
    && rm -rf /var/lib/apt/lists/*

# Copy Maven wrapper and pom.xml first for better Docker layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create app user for security
RUN addgroup --system --gid 1001 appgroup && \
    adduser --system --uid 1001 --gid 1001 appuser

# Copy the built jar
COPY --from=0 --chown=appuser:appgroup /app/target/*.jar app.jar

# Switch to non-root user
USER appuser

# Health check - checking Kafka connection through the service
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8082/actuator/health || exit 1

# Expose port
EXPOSE 8082

# Environment variables
ENV JAVA_OPTS="-Xmx256m -Xms128m" \
    SPRING_PROFILES_ACTIVE=docker \
    SERVER_PORT=8082 \
    KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Entry point with JVM optimization for containers
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]