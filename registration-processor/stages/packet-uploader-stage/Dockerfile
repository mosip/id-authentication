FROM java:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

# change volume to whichever storage directory you want to use for this container.
VOLUME /home/ftp1/VIRUS_SCAN/encrypted /home/ftp1/ARCHIVE_PACKET_LOCATION  /home/ftp1/VIRUS_SCAN/decrypted /home/ftp1/VIRUS_SCAN/unpacked

COPY ./target/packet-uploader-stage-*.jar packet-uploader-stage.jar

CMD ["java","-Dspring.profiles.active=${active_profile_env}","-jar","packet-uploader-stage.jar"]

