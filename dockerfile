FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/your-app-name.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
