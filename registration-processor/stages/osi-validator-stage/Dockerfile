FROM java:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

COPY ./target/osi-validator-stage-*.jar osi-validator-stage.jar

CMD ["java","-Dspring.profiles.active=${active_profile_env}","-jar","osi-validator-stage.jar"]
