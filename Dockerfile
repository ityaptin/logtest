FROM openjdk:8u151-jdk-alpine3.7
VOLUME /tmp

# Set environment variables.
RUN mkdir /logtest
ENV HOME /logtest

# Define working directory.
WORKDIR /logtest

ARG JAR_FILE
COPY ${JAR_FILE} /app.jar

ENV SERVER_PORT=8081
HEALTHCHECK --interval=5s --timeout=3s --retries=10 CMD curl localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

RUN apk add --update \
    curl \
    && rm -rf /var/cache/apk/*