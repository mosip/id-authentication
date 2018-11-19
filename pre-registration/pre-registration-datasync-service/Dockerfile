FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

ADD target/pre-registartion-datasync-service-1.0.0-SNAPSHOT.jar /PreRegistartionDatasyncService.jar

EXPOSE 9093

ENTRYPOINT ["java","-jar","/PreRegistartionDatasyncService.jar"]
