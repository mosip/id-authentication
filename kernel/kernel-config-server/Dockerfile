FROM openjdk:8

COPY ./target/kernel-config-server-*.jar kernel-config-server.jar

EXPOSE 51000

CMD ["java","-jar","kernel-config-server.jar"]
