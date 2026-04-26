FROM eclipse-temurin:8-jdk

RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY target/pdv-0.0.1-SNAPSHOT.war .
