FROM java:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

COPY ./target/Demo-dedupe-stage-1.0.0-SNAPSHOT-exec.jar Demo-dedupe-stage.jar

CMD ["java","-Dspring.profiles.active=${active_profile_env}","-jar","Demo-dedupe-stage.jar"]
