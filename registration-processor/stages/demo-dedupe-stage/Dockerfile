FROM java:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

<<<<<<< HEAD
COPY ./target/demo-dedupe-stage-*.jar demo-dedupe-stage.jar
=======
COPY ./target/Demo-dedupe-stage-*.jar Demo-dedupe-stage.jar
>>>>>>> 1667bce2bc061028f3b84cfff7c18882e5971df1

CMD ["java","-Dspring.profiles.active=${active_profile_env}","-jar","demo-dedupe-stage.jar"]
