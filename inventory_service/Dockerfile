# --- STAGE 1: Build stage ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy POMs and Source
COPY common-service ./common-service
COPY inventory_service ./inventory_service

# Build logic
RUN mvn -f common-service/pom.xml clean install -DskipTests && \
    mvn -f inventory_service/pom.xml clean package -DskipTests -B

# --- STAGE 2: Run stage ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install netcat for port checking
RUN apk add --no-cache netcat-openbsd

# Copy the jar from build stage
COPY --from=build /workspace/inventory_service/target/*.jar app.jar

# FIX: Use a heredoc or a single printf command to create the entrypoint.
# This avoids the "unknown instruction" error caused by stray quotes.
RUN printf "#!/bin/sh\n\
echo 'Waiting for postgres to be ready...'\n\
until nc -z postgres 5432; do\n\
  echo 'Postgres is unavailable - sleeping'\n\
  sleep 2\n\
done\n\
echo 'Postgres is up - executing application'\n\
exec java -jar app.jar\n" > /app/entrypoint.sh && \
chmod +x /app/entrypoint.sh

# Removed the period after 8081
EXPOSE 8081

ENTRYPOINT ["/app/entrypoint.sh"]