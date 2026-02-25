# Use Java 21 runtime
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy built jar
COPY target/*.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]