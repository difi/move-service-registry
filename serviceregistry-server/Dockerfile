FROM openjdk:8-jre-alpine

MAINTAINER Johannes Molland <johannes.molland@digdir.no>

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG jarPath
COPY ${jarPath} app.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]

HEALTHCHECK --interval=30s --timeout=2s --retries=3 \
CMD wget --no-verbose --tries=1 --spider "http://localhost:8080/actuator/health" || exit 1