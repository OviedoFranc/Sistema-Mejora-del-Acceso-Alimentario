FROM maven:3.8.6-openjdk-18 AS build
COPY . .
RUN mvn clean package assembly:single -DskipTests -T 1C
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /target/TPDDSApp.jar metrics.jar
EXPOSE 8081
CMD ["java", "-jar", "metrics.jar"]
