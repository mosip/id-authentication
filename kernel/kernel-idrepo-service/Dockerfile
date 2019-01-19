FROM openjdk:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

COPY ./target/kernel-idrepo-service-*.jar kernel-idrepo-service.jar

EXPOSE 8090

CMD ["java","-jar","-Dspring.profiles.active=${active_profile_env}", "kernel-idrepo-service.jar"]