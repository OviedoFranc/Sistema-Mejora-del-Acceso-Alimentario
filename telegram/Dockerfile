# syntax = docker/dockerfile:1.2
#
# Build stage
#

FROM maven:3.8.6-openjdk-18 AS build
COPY . .
RUN mvn clean package assembly:single -DskipTests

#
# Package stage
#

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /target/Bot.jar Bot.jar

EXPOSE 8080

CMD ["java","-classpath","Bot.jar","ar.edu.utn.dds.k3003.app.BotApp"]