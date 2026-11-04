FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY backend/pom.xml ./pom.xml
RUN mvn -B dependency:go-offline
COPY backend/src ./src
RUN mvn -B -DskipTests package
COPY backend/tessdata ./tessdata

FROM eclipse-temurin:21-jre-jammy
RUN apt-get update \
    && apt-get install -y --no-install-recommends tesseract-ocr libtesseract-dev libleptonica-dev \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /workspace/target/snapit-0.0.1-SNAPSHOT.jar ./app.jar
COPY --from=build /workspace/tessdata ./tessdata
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-Djava.awt.headless=true", "-jar", "/app/app.jar"]
