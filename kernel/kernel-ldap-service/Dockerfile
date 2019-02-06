FROM openjdk:8

#Uncomment below and Comment above line(i.e. FROM openjdk:8) for OS specific (e.g. Alpine OS ) docker base image
#FROM openjdk:8-jdk-alpine

ARG active_profile
# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

# change volume to whichever storage directory you want to use for this container.
VOLUME /tmp

copy ./target/kernel-ldap-service*.jar kernel-ldap-service.jar

EXPOSE 8092

CMD ["java","-Dspring.profiles.active=${active_profile_env}","-jar","kernel-ldap-service.jar"]