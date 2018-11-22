FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

ADD target/kernel-emailnotification-service-1.0.0-SNAPSHOT.jar /KernelEmailNotificationService.jar

EXPOSE 8083

ENTRYPOINT ["java","-jar","/KernelEmailNotificationService.jar"]
