# Stage 1: збирання проекту
FROM maven:3.8.3-openjdk-16 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn package

# Stage 2: запуск додатку
FROM openjdk:16-slim
WORKDIR /app
COPY --from=build /app/target/duty-helper-1.0.jar /app
EXPOSE 5000
CMD ["java", "-Xmx10240m", "-Dspring.profiles.active=prod", "-jar", "/app/duty-helper-1.0.jar"]
