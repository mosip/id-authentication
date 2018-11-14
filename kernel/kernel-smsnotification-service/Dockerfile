FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

ADD target/kernel-smsnotification-service-1.0.0-SNAPSHOT.jar /KernelSmsNotificationService.jar

RUN bash -c 'touch /KernelSmsNotificationService.jar'

EXPOSE 8084

ENTRYPOINT ["java","-jar","/KernelSmsNotificationService.jar"]
