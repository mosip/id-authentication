FROM openjdk:8

COPY ./target/kernel-smsnotification-service-1.0.0-SNAPSHOT.jar kernel-smsnotification-service-1.0.0-SNAPSHOT.jar

EXPOSE 8084

CMD ["java","-jar","-Dspring.profiles.active=int","kernel-smsnotification-service-1.0.0-SNAPSHOT.jar"]

