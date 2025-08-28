# Build stage
FROM maven:3.8.4-openjdk-17-slim AS builder

WORKDIR /build
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built artifact from builder stage
COPY --from=builder /build/target/solar-redis-service-1.0.0.jar app.jar
COPY --from=builder /build/src/main/resources/application.yml application.yml

# Create volume for temporary files
VOLUME /tmp

# Expose the application port
EXPOSE 8081

# Set JVM options and run the application
ENTRYPOINT ["java", "-jar", "app.jar"]