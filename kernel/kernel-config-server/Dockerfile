FROM openjdk:8

COPY ./target/kernel-config-service-*.jar kernel-config-service.jar

EXPOSE 51000

CMD ["java","-jar","kernel-config-service.jar"]