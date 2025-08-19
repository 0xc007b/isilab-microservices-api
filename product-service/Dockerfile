# Multi-stage Dockerfile for Product Service
# Stage 1: Build stage
FROM maven:3.9.4-openjdk-17-slim AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (for better layer caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime stage
FROM openjdk:17-jre-slim

# Set metadata
LABEL maintainer="Product Service Team <backend@orderms.com>"
LABEL description="Product Management Service for Order Management System"
LABEL version="1.0.0"

# Install necessary packages and create app user
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        curl \
        wget \
        netcat-traditional \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd -r appgroup && useradd -r -g appgroup appuser

# Set working directory
WORKDIR /app

# Create directories for logs and temp files
RUN mkdir -p /app/logs /app/temp && \
    chown -R appuser:appgroup /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/product-service-*.jar app.jar

# Copy wait script for service dependencies
COPY --chown=appuser:appgroup docker/wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh || echo "wait-for-it.sh not found, skipping"

# Set environment variables
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap" \
    SPRING_PROFILES_ACTIVE=docker \
    SERVER_PORT=8082 \
    JVM_XMS=512m \
    JVM_XMX=1024m

# Expose port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8082/actuator/health || exit 1

# Switch to non-root user
USER appuser

# Set JVM options and start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Xms$JVM_XMS -Xmx$JVM_XMX -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

# Alternative entrypoint with wait script (uncomment if using docker-compose with dependencies)
# ENTRYPOINT ["sh", "-c", "./wait-for-it.sh db:5432 --timeout=60 --strict -- java $JAVA_OPTS -Xms$JVM_XMS -Xmx$JVM_XMX -jar app.jar"]
