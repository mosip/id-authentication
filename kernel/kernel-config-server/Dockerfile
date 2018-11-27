FROM openjdk:8

COPY ./target/kernel-config-server-1.0.0-SNAPSHOT.jar kernel-config-server-1.0.0-SNAPSHOT.jar

EXPOSE 51000

CMD ["java","-jar","-Dspring.profiles.active=int","kernel-config-server-1.0.0-SNAPSHOT.jar"]
