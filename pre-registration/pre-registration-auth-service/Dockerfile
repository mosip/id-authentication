FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

ADD target/pre-registartion-auth-service-1.0.0-SNAPSHOT.jar /PreRegistartionAuthService.jar

EXPOSE 9091

ENTRYPOINT ["java","-jar","/PreRegistartionAuthService.jar"]
