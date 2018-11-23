FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

ADD target/kernel-otpmanager-service-1.0.0-SNAPSHOT.jar /KernelOtpManagerService.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","/KernelOtpManagerService.jar"]
