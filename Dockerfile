FROM eclipse-temurin:21-alpine
LABEL authors="khkh1v"

ARG JAR_FILE=/target/simple-messenger-1.0-SNAPSHOT.jar
WORKDIR /opt/app
COPY ${JAR_FILE} simple-messenger.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "simple-messenger.jar"]