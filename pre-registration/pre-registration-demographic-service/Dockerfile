FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

ADD target/pre-registration-demographic-service-1.0.0-SNAPSHOT.jar /PreRegistrationDemographicService.jar

EXPOSE 9092

ENTRYPOINT ["java","-jar","/PreRegistrationDemographicService.jar"]
