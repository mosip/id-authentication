FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

ADD target/kernel-idgenerator-uin-1.0.0-SNAPSHOT.jar /KerneUinGeneratorService.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/KerneUinGeneratorService.jar"]
