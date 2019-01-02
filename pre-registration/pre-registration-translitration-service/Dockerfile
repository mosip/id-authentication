FROM java:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

copy ./target/pre-registration-translitration-service-*.jar pre-registration-translitration-service.jar

EXPOSE 9098

CMD ["java","-Dspring.profiles.active=${active_profile_env}","-jar","pre-registration-translitration-service-1.0.0-SNAPSHOT.jar"] 