# Use maintained lightweight Java 17 base image (Eclipse Temurin)
FROM eclipse-temurin:17-jdk-jammy

# Copy the built JAR file into the container
COPY target/moviedb-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "/app.jar"]
