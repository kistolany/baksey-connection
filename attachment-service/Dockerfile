# --- STAGE 1: Build stage ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY common-service ./common-service
COPY attachment-service ./attachment-service
RUN mvn -f common-service/pom.xml -DskipTests install && \
    mvn -f attachment-service/pom.xml -DskipTests package -DskipITs -B

# --- STAGE 2: Run stage ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /workspace/attachment-service/target/*.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]