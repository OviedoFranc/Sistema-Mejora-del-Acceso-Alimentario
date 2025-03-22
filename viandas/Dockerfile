FROM maven:3.8.6-openjdk-18 AS build
COPY . .
RUN mvn clean package assembly:single -DskipTests -T 1C
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /target/TPDDSApp.jar viandas.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","viandas.jar"]
