# Use Amazon Corretto OpenJDK 17 as base image
FROM amazoncorretto:17-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Maven files for dependency resolution
COPY pom.xml .
COPY src ./src

# Download dependencies and build the application
RUN mvn clean package -DskipTests

# Runtime stage - smaller image
FROM amazoncorretto:17-alpine

# Copy the built JAR from builder stage
COPY --from=builder /app/target/signalroot-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
