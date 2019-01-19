FROM java:8

ARG active_profile

# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

ADD target/authentication-service-*.jar /AuthenticationService.jar

EXPOSE 8090

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=${active_profile_env}","/AuthenticationService.jar"]
