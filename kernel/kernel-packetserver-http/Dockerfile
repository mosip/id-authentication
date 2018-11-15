FROM java:8

ADD target/kernel-packetserver-http-1.0.0-SNAPSHOT.jar /KernelHttpPacketServer.jar

RUN mkdir /packet-dir

VOLUME /packet-dir

EXPOSE 8082

ENTRYPOINT ["java","-jar","/KernelHttpPacketServer.jar"]
