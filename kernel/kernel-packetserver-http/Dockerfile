FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

ADD target/kernel-packetserver-http-1.0.0-SNAPSHOT.jar /KernelHttpPacketServer.jar

RUN bash -c 'touch /KernelHttpPacketServer.jar'

EXPOSE 8082

ENTRYPOINT ["java","-jar","/KernelHttpPacketServer.jar"]
