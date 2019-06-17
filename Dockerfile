FROM amazoncorretto:latest
MAINTAINER Regis Santos
RUN mkdir -p /opt/payments
COPY ./build/libs/payments-api-0.0.1-SNAPSHOT.jar /opt/payments
WORKDIR /opt/payments
ENTRYPOINT java -jar payments-api-0.0.1-SNAPSHOT.jar