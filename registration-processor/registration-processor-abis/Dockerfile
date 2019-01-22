FROM java:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

# change volume to whichever storage directory you want to use for this container.
VOLUME /home/ftp1/VIRUS_SCAN

COPY ./target/registration-processor-abis-*.jar registration-processor-abis.jar

CMD ["java","-Dspring.profiles.active=${active_profile_env}","-jar","registration-processor-abis.jar"]
