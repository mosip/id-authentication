FROM openjdk:8

COPY ./target/kernel-otpmanager-service-1.0.0-SNAPSHOT.jar kernel-otpmanager-service-1.0.0-SNAPSHOT.jar

EXPOSE 8085

CMD ["java","-jar","-Dspring.profiles.active=int","kernel-otpmanager-service-1.0.0-SNAPSHOT.jar"]