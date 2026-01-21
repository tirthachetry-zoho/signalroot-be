# SignalRoot Backend

Spring Boot backend for SignalRoot alert enrichment platform.

## 🚀 Quick Start

```bash
# Install dependencies and build
mvn clean install

# Start application
mvn spring-boot:run

# Build for production
mvn clean package
```

## 📁 Project Structure

```
backend/
├── src/
│   └── main/
│       └── java/
│           └── com/signalroot/
│               ├── controller/
│               │   ├── AlertController.java
│               │   ├── ApiDocsController.java
│               │   ├── DogfoodingController.java
│               │   ├── FakeCustomerController.java
│               │   ├── SafetyController.java
│               │   ├── VersionController.java
│               │   └── WebhookController.java
│               ├── service/
│               │   ├── AlertEnrichmentService.java
│               │   ├── DogfoodingService.java
│               │   ├── FakeOrganizationService.java
│               │   ├── FakeCustomerService.java
│               │   ├── SafetyService.java
│               │   └── VersionService.java
│               ├── model/
│               │   ├── Alert.java
│               │   ├── DogfoodingScenario.java
│               │   ├── FakeCustomer.java
│               │   ├── Team.java
│               │   └── Version.java
│               ├── config/
│               │   ├── SwaggerConfig.java
│               │   ├── WebhookConfig.java
│               │   └── SignalRootApplication.java
│               └── resources/
│                   ├── application.properties
│                   └── application-dev.properties
├── target/
│   └── signalroot-0.0.1-SNAPSHOT.jar
├── pom.xml
├── docker-compose.yml
└── Dockerfile
```

## 🎨 Technologies

- **Spring Boot 3.1.5**: Java application framework
- **Spring Web**: RESTful API development
- **Spring Data JPA**: Database ORM with Hibernate
- **PostgreSQL**: Primary database (configurable)
- **SpringDoc OpenAPI**: Interactive API documentation
- **Maven**: Dependency management and build tool
- **Docker**: Containerization support

## 🛠️ Development

### Environment Setup

1. **Java**: Version 17 or higher
2. **Maven**: Version 3.6 or higher
3. **PostgreSQL**: Version 15+ (or use Docker)
4. **IDE**: IntelliJ IDEA, Eclipse, VS Code

### Available Commands

```bash
# Development
mvn spring-boot:run

# Testing
mvn test

# Build
mvn clean package

# Docker build
docker build -t signalroot-backend .

# Run with Docker
docker-compose up -d
```

### Configuration

#### Application Properties

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/signalroot
spring.datasource.username=your_db_username
spring.datasource.password=your_secure_db_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Service Configuration
signalroot.service.mode=mock
signalroot.frontend.url=http://localhost:3000

# Webhook Configuration
signalroot.webhook.secret=default-secret
signalroot.slack.webhook-url=

# Notification Configuration
signalroot.notifications.email.enabled=false
signalroot.notifications.slack.enabled=false
signalroot.notifications.email.from=alerts@signalroot.com
signalroot.notifications.email.to=

# SpringDoc Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

#### Environment Variables

```bash
# Development
export SPRING_PROFILES_ACTIVE=dev

# Production
export DATABASE_URL=jdbc:postgresql://prod-db:5432/signalroot
export DATABASE_USERNAME=prod_db_username
export DATABASE_PASSWORD=your_secure_prod_db_password
export WEBHOOK_SECRET=your_secure_webhook_secret
export SLACK_WEBHOOK_URL=https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK
```

## 🔗 API Endpoints

### Core Controllers

#### Alert Processing
- `POST /webhooks/alerts/pagerduty` - PagerDuty webhook
- `POST /webhooks/alerts/cloudwatch/{orgKey}` - CloudWatch webhook
- `POST /webhooks/deploy/github` - GitHub deployment webhook

#### Data Management
- `GET /api/fake-customers/teams` - Get all teams
- `GET /api/fake-customers/teams/{teamId}` - Get team details
- `GET /api/fake-customers/teams/{teamId}/stats` - Team statistics
- `POST /api/fake-customers/teams/{teamId}/simulate-alert` - Simulate alert

#### Dogfooding
- `GET /api/dogfooding/scenarios` - Get test scenarios
- `POST /api/dogfooding/run-all` - Run all scenarios
- `POST /api/dogfooding/scenarios/{scenarioId}/run` - Run specific scenario

#### System Management
- `GET /actuator/health` - Health check
- `GET /api/versions/version/current` - Current version
- `GET /api/versions/changelog/{version}` - Version changelog

### Example Requests

```bash
# Health check
curl http://localhost:8080/actuator/health

# Get teams
curl http://localhost:8080/api/fake-customers/teams

# Process PagerDuty alert
curl -X POST http://localhost:8080/webhooks/alerts/pagerduty \
  -H "Content-Type: application/json" \
  -d '{"messages":[{"type":"incident","incident":{"id":"INC123","status":"triggered"}}]}'

# Run dogfooding
curl -X POST http://localhost:8080/api/dogfooding/run-all
```

## 🐳 Docker Support

### Docker Compose

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: signalroot
      POSTGRES_USER: signalroot
      POSTGRES_PASSWORD: signalroot
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U signalroot -d signalroot"]
      interval: 30s
      timeout: 10s
      retries: 3

  signalroot-backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DATABASE_URL=jdbc:postgresql://postgres:5432/signalroot
      - DATABASE_USERNAME=signalroot
      - DATABASE_PASSWORD=signalroot
    depends_on:
      - postgres
    healthcheck:
      test: ["CMD", "curl -f http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  postgres_data:
```

### Dockerfile

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/signalroot-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 🧪 Testing

### Unit Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AlertControllerTest

# Generate test coverage report
mvn jacoco:report
```

### Integration Tests

```bash
# Test with TestContainers
mvn test -Dspring.profiles.active=testcontainers

# API testing with Postman
# Import collection from docs/api-postman-collection.json
```

## 🚀 Deployment

### Build Process

```bash
# Create executable JAR
mvn clean package

# Output in target/
# signalroot-0.0.1-SNAPSHOT.jar
```

### Production Deployment

#### Environment Setup

1. **Database**: PostgreSQL with connection pooling
2. **Application Server**: Tomcat with SSL termination
3. **Load Balancer**: AWS ALB or NGINX
4. **Monitoring**: Spring Actuator with metrics

#### Configuration

```properties
# Production application.properties
server.port=8080
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your_secure_keystore_password

# Database with HikariCP
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000

# Production profiles
spring.profiles.active=prod
```

#### Docker Deployment

```bash
# Build production image
docker build -t signalroot-backend:prod .

# Run production container
docker run -d \
  --name signalroot-backend \
  -p 8080:8080 \
  -e DATABASE_URL \
  -e DATABASE_USERNAME \
  -e DATABASE_PASSWORD \
  signalroot-backend:prod
```

## 📊 Monitoring & Observability

### Actuator Endpoints

- `/actuator/health` - Application health
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application information
- `/actuator/env` - Environment variables

### Logging Configuration

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

## 🔧 Troubleshooting

### Common Issues

1. **Database Connection**: Check PostgreSQL status and credentials
2. **Port Conflicts**: Kill processes on port 8080
3. **Memory Issues**: Increase JVM heap size with `-Xmx`
4. **Build Failures**: Clean Maven cache and rebuild

### Debug Mode

```bash
# Enable debug logging
mvn spring-boot:run -Dspring.profiles.active=dev -Dlogging.level.com.signalroot=DEBUG

# Remote debugging
mvn spring-boot:run -Dspring.profiles.active=dev -Dspring.jpa.show-sql=true
```

## 📄 License

MIT License - see LICENSE file for details.

## 📞 Support

- **Documentation**: [SignalRoot Docs](https://docs.signalroot.com)
- **API Reference**: [Swagger UI](http://localhost:8080/swagger-ui.html)
- **Issues**: [GitHub Issues](https://github.com/signalroot/signalroot/issues)
- **Discord**: [Community Discord](https://discord.gg/signalroot)
- **Email**: [backend-support@signalroot.com](mailto:backend-support@signalroot.com)

---

Built with Spring Boot 3.1.5, Java 17, and Maven.
