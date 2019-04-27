FROM openjdk:8

#Uncomment below and Comment above line(i.e. FROM openjdk:8) for OS specific (e.g. Alpine OS ) docker base image
#FROM openjdk:8-jdk-alpine

COPY ./target/kernel-uingenerator-service-*.jar kernel-uingenerator-service.jar

COPY ./swagger-ui swagger-ui

EXPOSE 8080

CMD ["java","-jar","kernel-uingenerator-service.jar"]
