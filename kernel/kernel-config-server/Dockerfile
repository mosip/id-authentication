FROM openjdk:8

#Uncomment below and Comment above line(i.e. FROM openjdk:8) for OS specific (e.g. Alpine OS ) docker base image
#FROM openjdk:8-jdk-alpine

COPY ./target/kernel-config-server-*.jar kernel-config-server.jar

EXPOSE 51000

CMD ["java","-jar","kernel-config-server.jar"]
