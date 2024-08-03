FROM amazoncorretto:11-alpine3.18-jdk AS builder

RUN jlink --compress=2 --module-path /usr/lib/jvm/java-11-amazon-corretto/jmods/ \
    --add-modules java.base,java.logging,java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument,jdk.management,jdk.crypto.cryptoki \
    --no-header-files --no-man-pages --output /custom-jre

FROM alpine:3.18

RUN addgroup -S user \
      && adduser -S user -G user

USER user

ENV JAVA_HOME /opt/jdk
ENV PATH $JAVA_HOME/bin:$PATH
ENV JAVA_OPTS=" -Duser.timezone=America/Bogota"

COPY --from=builder /custom-jre /opt/jdk/
COPY build/libs/plazoleta-1.0.jar parking.jar

EXPOSE 9090
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /parking.jar"]