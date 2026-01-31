FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/catalog-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 2001

ENTRYPOINT ["java", "-jar", "app.jar"]
