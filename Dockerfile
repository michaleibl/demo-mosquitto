FROM azul/zulu-openjdk-alpine:21-jre-headless
LABEL authors="Mike"
COPY target/demo-mosquitto-1.0.0.jar /opt/demo-mosquitto-1.0.0.jar
ENTRYPOINT ["java", "-jar", "/opt/demo-mosquitto-1.0.0.jar"]