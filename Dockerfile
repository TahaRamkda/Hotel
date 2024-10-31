FROM eclipse-temurin:17-jre

WORKDIR /app

COPY build/classes/java/main /app

ENTRYPOINT ["java", "Main"]
