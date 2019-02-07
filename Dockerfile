FROM openjdk:8-jdk-alpine as builder
WORKDIR /app
COPY . .
RUN ./gradlew build

FROM openjdk:8-jdk-alpine

ENV APPLICATION_USER ktor
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY --from=builder /app/build/libs/tothecomments.jar /app/tothecomments.jar
WORKDIR /app

ENV PORT 8080
EXPOSE $PORT

CMD [ \
    "java", \
    "-server", \
    "-XX:+UnlockExperimentalVMOptions", \
    "-XX:+UseCGroupMemoryLimitForHeap", \
    "-XX:InitialRAMFraction=2", \
    "-XX:MinRAMFraction=2", \
    "-XX:MaxRAMFraction=2", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=100", \
    "-XX:+UseStringDeduplication", \
    "-jar", \
    "tothecomments.jar" \
]