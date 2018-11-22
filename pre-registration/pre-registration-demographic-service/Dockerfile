FROM java:8

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

copy ./target/pre-registration-demographic-service-1.0.0-SNAPSHOT.jar pre-registration-demographic-service-1.0.0-SNAPSHOT.jar

CMD ["java","-Dspring.profiles.active=int","-jar","pre-registration-demographic-service-1.0.0-SNAPSHOT.jar"] 