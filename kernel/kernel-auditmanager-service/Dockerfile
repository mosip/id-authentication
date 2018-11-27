FROM openjdk:8

COPY ./target/kernel-auditmanager-service-1.0.0-SNAPSHOT.jar kernel-auditmanager-service-1.0.0-SNAPSHOT.jar

EXPOSE 8081

CMD ["java","-jar","-Dspring.profiles.active=int","kernel-auditmanager-service-1.0.0-SNAPSHOT.jar"]