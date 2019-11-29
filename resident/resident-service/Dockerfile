FROM openjdk:8

#Uncomment below and Comment above line(i.e. FROM openjdk:8) for OS specific (e.g. Alpine OS ) docker base image
#FROM openjdk:8-jdk-alpine

COPY ./target/resident-service-*.jar resident-service.jar

EXPOSE 8098

CMD ["java","-jar","resident-service.jar"]
