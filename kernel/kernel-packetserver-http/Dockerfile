FROM openjdk:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

COPY ./target/kernel-packetserver-http-*.jar kernel-packetserver-http.jar

RUN mkdir /packet-dir

VOLUME /packet-dir

EXPOSE 8082

CMD ["java","-jar","-Dspring.profiles.active=${active_profile_env}","kernel-packetserver-http.jar"]
