FROM java:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

COPY ./target/registration-processor-camel-bridge-1.0.0-SNAPSHOT-jar-with-dependencies.jar registration-processor-camel-bridge.jar

CMD ["java","-Dspring.profiles.active=${active_profile_env}","-jar","registration-processor-camel-bridge.jar"]
