# --- STAGE 1: Build stage ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy common-service (if all services depend on it)
COPY common-service ./common-service
# Copy the specific service folder
COPY shopping_cart_service ./shopping_cart_service

# Build common-service first, then the specific service
RUN mvn -f common-service/pom.xml -DskipTests install && \
    mvn -f shopping_cart_service/pom.xml -DskipTests package -DskipITs -B

# --- STAGE 2: Run stage ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install netcat for port checking
RUN apk add --no-cache netcat-openbsd

# Copy the jar from the build stage
COPY --from=build /workspace/shopping_cart_service/target/*.jar app.jar

# Create entrypoint script that waits for postgres
RUN echo '#!/bin/sh' > /app/entrypoint.sh && \
    echo 'echo "Waiting for postgres to be ready..."' >> /app/entrypoint.sh && \
    echo 'until nc -z postgres 5432; do' >> /app/entrypoint.sh && \
    echo '  echo "Postgres is unavailable - sleeping"' >> /app/entrypoint.sh && \
    echo '  sleep 2' >> /app/entrypoint.sh && \
    echo 'done' >> /app/entrypoint.sh && \
    echo 'echo "Postgres is up - executing application"' >> /app/entrypoint.sh && \
    echo 'exec java -jar app.jar' >> /app/entrypoint.sh && \
    chmod +x /app/entrypoint.sh

EXPOSE 8083
ENTRYPOINT ["/app/entrypoint.sh"]
