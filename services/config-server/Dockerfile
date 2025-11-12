# syntax=docker/dockerfile:1
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN apk add --no-cache tini curl \
    && addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=local
ENV GITHUB_USERNAME=<your-username>
ENV GITHUB_TOKEN=<your-token-here>
ENV CONFIG_PASSWORD=<your-password-here>

EXPOSE 8888
ENTRYPOINT ["/sbin/tini", "--"]
CMD ["java", "-jar", "app.jar"]

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8888/actuator/health || exit 1

LABEL maintainer="MarketGrid Team <dev@marketgrid.com>" \
      version="1.0.0" \
      description="MarketGrid Config Server"
