# Use an official Gradle image to build the application
FROM gradle:7.3.3-jdk17 AS build
WORKDIR /app
COPY . .

RUN ./gradlew build --no-daemon

# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/gate-io-bot-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
