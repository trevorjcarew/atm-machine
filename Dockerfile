FROM openjdk:8-jdk-alpine
MAINTAINER baeldung.com
COPY target/atm-machine-1.0.0.jar atm-1.0.0.jar
ENTRYPOINT ["java","-jar","/atm-1.0.0.jar"]