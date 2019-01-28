FROM openjdk:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

COPY ./target/kernel-uingenerator-service-*.jar kernel-uingenerator-service.jar

EXPOSE 8080

CMD ["java","-jar","-Dspring.profiles.active=${active_profile_env}","kernel-uingenerator-service.jar"]
