FROM openjdk:8

COPY ./target/kernel-masterdata-service-1.0.0-SNAPSHOT.jar kernel-masterdata-service-1.0.0-SNAPSHOT.jar

EXPOSE 8086

CMD ["java","-jar","-Dspring.profiles.active=int","kernel-masterdata-service-1.0.0-SNAPSHOT.jar"]
