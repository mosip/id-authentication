FROM openjdk:8

COPY ./target/kernel-syncdata-service-1.0.0-SNAPSHOT.jar kernel-syncdata-service-1.0.0-SNAPSHOT.jar

EXPOSE 8089

CMD ["java","-jar","-Dspring.profiles.active=int","kernel-syncdata-service-1.0.0-SNAPSHOT.jar"]
