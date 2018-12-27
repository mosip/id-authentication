FROM java:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

VOLUME /home/ftp1/ARCHIVE_PACKET_LOCATION

COPY ./target/packet-decrypter-stage-*.jar packet-decrypter-stage.jar

CMD ["java","-Dspring.profiles.active=${active_profile_env}","-jar","packet-decrypter-stage.jar"]
