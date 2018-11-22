FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

ADD target/kernel-auditmanager-service-1.0.0-SNAPSHOT.jar /KernelAuditManagerService.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","/KernelAuditManagerService.jar"]
