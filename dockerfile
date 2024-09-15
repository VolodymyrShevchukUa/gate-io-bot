FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY build/libs/gate-io-bot-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
