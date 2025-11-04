# Multi-stage Dockerfile for building and running the Spring Boot app with Java 21

# ---------- Build stage (Maven + Temurin JDK) ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy build files first to leverage Docker cache for dependencies
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Pre-fetch dependencies (improves cacheability). Use system 'mvn' from the image.
RUN mvn -B -DskipTests dependency:go-offline || true

# Copy sources and package the application
COPY src ./src
RUN mvn -B -DskipTests package


# ---------- Runtime stage (lightweight Temurin JRE) ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built artifact from the build stage. Use wildcard to avoid hardcoding version.
COPY --from=build /workspace/target/*.jar app.jar

# Expose the standard Spring Boot port and set PORT env for compatibility
ENV PORT=8080
EXPOSE 8080

## Start the app binding the server port to the PORT env var provided by hosting platforms (e.g., Render)
# Use a shell form so the environment variable is expanded at runtime
ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar /app/app.jar"]
