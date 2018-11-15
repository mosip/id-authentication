FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

ADD target/kernel-masterdata-service-1.0.0-SNAPSHOT.jar /KernelMasterdataService.jar

EXPOSE 8086

ENTRYPOINT ["java","-jar","/KernelMasterdataService.jar"]
