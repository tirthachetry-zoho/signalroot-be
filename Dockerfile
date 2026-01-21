# Use Eclipse Temurin OpenJDK 17 as base image
FROM eclipse-temurin:17-jdk-slim AS builder

# Set working directory
WORKDIR /app

# Copy Maven files for dependency resolution
COPY pom.xml .
COPY src ./src

# Download dependencies and build the application
RUN mvn clean package -DskipTests

# Runtime stage - smaller image
FROM eclipse-temurin:17-jre-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl

# Copy the built JAR from builder stage
COPY --from=builder /app/target/signalroot-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
