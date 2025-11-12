# syntax=docker/dockerfile:1
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy only dependency files first (better caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source after dependencies
COPY src src
RUN mvn clean package -DskipTests

# ---- Runtime ----
FROM eclipse-temurin:21-jre-alpine AS runtime
RUN apk add --no-cache tini curl \
    && addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=local

EXPOSE 8761
ENTRYPOINT ["/sbin/tini", "--"]
CMD ["java", "-jar", "app.jar"]

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8761/actuator/health || exit 1

LABEL maintainer="MarketGrid Team <dev@marketgrid.com>" \
      version="1.0.0" \
      description="MarketGrid Discovery Server (Eureka)"