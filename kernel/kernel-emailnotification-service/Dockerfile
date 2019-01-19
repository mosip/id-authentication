FROM openjdk:8


ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

COPY ./target/kernel-emailnotification-service-*.jar kernel-emailnotification-service.jar

EXPOSE 8083

CMD ["java","-jar","-Dspring.profiles.active=${active_profile_env}","kernel-emailnotification-service.jar"]
