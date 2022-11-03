# syntax=docker/dockerfile:1
FROM eclipse-temurin:17.0.4.1_1-jre-alpine
WORKDIR /
ARG JAR_FILE
COPY ${JAR_FILE} /cdbb.jar
EXPOSE 8080
CMD java -jar /cdbb.jar --spring.config.location=file:/application.yaml