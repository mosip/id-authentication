FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

ADD target/kernel-config-server-1.0.0-SNAPSHOT.jar /KernelConfigServer.jar

EXPOSE 51000

ENTRYPOINT ["java","-jar","/KernelConfigServer.jar"]
