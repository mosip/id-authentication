FROM openjdk:8

COPY ./target/kernel-idgenerator-uin-1.0.0-SNAPSHOT.jar kernel-idgenerator-uin-1.0.0-SNAPSHOT.jar

EXPOSE 8080

CMD ["java","-jar","-Dspring.profiles.active=int","kernel-idgenerator-uin-1.0.0-SNAPSHOT.jar"]
