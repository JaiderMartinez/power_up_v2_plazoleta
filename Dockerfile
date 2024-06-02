FROM adoptopenjdk:11-jre-hotspot
EXPOSE 9090
WORKDIR /app
COPY build/libs/*.jar /app/api.jar
ENTRYPOINT ["java", "-jar", "/app/api.jar"]