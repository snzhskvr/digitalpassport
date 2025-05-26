FROM eclipse-temurin:21-jre-alpine
RUN apk add curl
WORKDIR /app
COPY build/libs/digitalpassport-*-SNAPSHOT.jar /app/digitalpassport.jar
COPY deploy/config/application.yaml /app/config/
VOLUME /app/config
HEALTHCHECK --interval=20s --timeout=5s --start-period=40s --retries=5 CMD curl --fail --silent localhost:8080/actuator/health | grep UP || exit 1
ENTRYPOINT java -jar digitalpassport.jar --spring.config.location=config/application.yaml