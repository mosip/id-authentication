FROM java:8

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

copy ./target/kernel-ldap-service*.jar kernel-ldap-service.jar

EXPOSE 8092

CMD ["java","-Dspring.profiles.active=${active_profile_env}","-jar","kernel-ldap-service.jar"]