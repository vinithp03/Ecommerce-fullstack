# Use lightweight Java 21 image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar from target folder
COPY target/*.jar app.jar

# Expose cart port
EXPOSE 2002

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
