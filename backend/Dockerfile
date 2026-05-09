# Stage 1: Build the application
# Use Maven to compile the source code and package it into a JAR
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copy everything to the container
COPY . .
# Build the JAR file, skipping unit tests for a faster build
RUN mvn clean package -DskipTests

# Stage 2: Create the final lightweight image
# Use a smaller JRE image for the production environment
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy only the compiled JAR from the build stage
COPY --from=build /app/target/*.jar app.jar
# Expose the port Spring Boot runs on
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
