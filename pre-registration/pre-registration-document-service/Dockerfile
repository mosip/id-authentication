FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

ADD target/pre-registartion-document-service-1.0.0-SNAPSHOT.jar /PreRegistartionDocumentService.jar

EXPOSE 9093

ENTRYPOINT ["java","-jar","/PreRegistartionDocumentService.jar"]
