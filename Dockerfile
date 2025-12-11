FROM node:20-alpine AS frontend-build
WORKDIR /app

COPY frontend/package*.json ./
RUN npm install

COPY frontend/ .
RUN npm run build

FROM maven:3.9-openjdk-21 AS backend-build
WORKDIR /app

# Копируем исходники бэкенда
COPY src/ ./src/
COPY pom.xml .

COPY --from=frontend-build /app/dist ./src/main/resources/static

RUN mvn clean package -DskipTests

FROM openjdk:21-jre-slim
WORKDIR /app

COPY --from=backend-build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]