FROM openjdk:8

COPY ./target/kernel-packetserver-http-1.0.0-SNAPSHOT.jar kernel-packetserver-http-1.0.0-SNAPSHOT.jar

RUN mkdir /packet-dir

VOLUME /packet-dir

EXPOSE 8082

CMD ["java","-jar","-Dspring.profiles.active=int","kernel-packetserver-http-1.0.0-SNAPSHOT.jar"]
