FROM openjdk:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

COPY ./target/kernel-config-server-*.jar kernel-config-server.jar

EXPOSE 51000

CMD ["java","-jar","-Dspring.profiles.active=${active_profile_env}","kernel-config-server.jar"]
