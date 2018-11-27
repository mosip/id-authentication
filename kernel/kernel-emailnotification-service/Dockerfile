FROM openjdk:8

COPY ./target/kernel-emailnotification-service-1.0.0-SNAPSHOT.jar kernel-emailnotification-service-1.0.0-SNAPSHOT.jar

EXPOSE 8083

CMD ["java","-jar","-Dspring.profiles.active=int","kernel-emailnotification-service-1.0.0-SNAPSHOT.jar"]
