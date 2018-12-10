FROM openjdk:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

COPY ./target/kernel-auditmanager-service-*.jar kernel-auditmanager-service.jar

EXPOSE 8081

CMD ["java","-jar","-Dspring.profiles.active=${active_profile_env}","kernel-auditmanager-service.jar"]