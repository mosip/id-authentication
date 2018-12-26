FROM java:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

# change volume to whichever storage directory you want to use for this container.
VOLUME /home/ftp1/LANDING_ZONE /home/ftp1/VIRUS_SCAN

copy ./target/landingzone-scanner-stage-1.0.0-SNAPSHOT-exec.jar landingzone-scanner-stage.jar

EXPOSE 8081

CMD ["java","-Dspring.profiles.active=${active_profile_env}","-jar","landingzone-scanner-stage.jar"]
