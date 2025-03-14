FROM maven:3.8.6-openjdk-18 AS build
COPY . .
RUN mvn clean package assembly:single -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /target/Metrics.jar Metrics.jar

EXPOSE 8080

CMD ["java","-classpath","Metrics.jar","ar.edu.utn.dds.k3003.app.WebApp"]