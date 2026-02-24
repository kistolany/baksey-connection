# --- STAGE 1: Build stage ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy Common Service and Address Service
COPY common-service ./common-service
COPY address_service ./address_service

# Build common-service first, then the address-service
RUN mvn -f common-service/pom.xml clean install -DskipTests && \
    mvn -f address_service/pom.xml clean package -DskipTests -B

# --- STAGE 2: Run stage ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache netcat-openbsd

# Copy the jar from build stage
COPY --from=build /workspace/address_service/target/*.jar app.jar

# Create entrypoint script
RUN printf "#!/bin/sh\n\
echo 'Waiting for postgres to be ready...'\n\
until nc -z postgres 5432; do\n\
  echo 'Postgres is unavailable - sleeping'\n\
  sleep 2\n\
done\n\
echo 'Postgres is up - executing application'\n\
exec java -jar app.jar\n" > /app/entrypoint.sh && \
chmod +x /app/entrypoint.sh

# Updated port to match your application.properties
EXPOSE 8084

ENTRYPOINT ["/app/entrypoint.sh"]