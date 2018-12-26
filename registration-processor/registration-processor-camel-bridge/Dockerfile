FROM java:8
FROM vertx/vertx3

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

# change volume to whichever storage directory you want to use for this container.
VOLUME /home/ftp1/LANDING_ZONE /home/ftp1/VIRUS_SCAN

copy ./target/registration-processor-camel-bridge-1.0.0-SNAPSHOT-jar-with-dependencies.jar registration-processor-camel-bridge.jar

CMD ["java","-Dspring.profiles.active=${active_profile_env}","-jar","registration-processor-camel-bridge.jar"]
