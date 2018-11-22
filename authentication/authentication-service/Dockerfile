FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

ADD target/authentication-service-1.0.0-SNAPSHOT.jar /AuthenticationService.jar

EXPOSE 8090

ENTRYPOINT ["java","-jar","/AuthenticationService.jar"]