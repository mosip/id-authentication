FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

ADD target/pre-registration-auth-service-1.0.0-SNAPSHOT.jar /PreRegistrationAuthService.jar

EXPOSE 9090

ENTRYPOINT ["java","-jar","/PreRegistrationAuthService.jar"]
