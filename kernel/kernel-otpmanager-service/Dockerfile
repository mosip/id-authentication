FROM openjdk:8

#Uncomment below and Comment above line(i.e. FROM openjdk:8) for OS specific (e.g. Alpine OS ) docker base image
#FROM openjdk:8-jdk-alpine

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG spring_config_label

# can be passed during Docker build as build time environment for spring profiles active 
ARG active_profile

# can be passed during Docker build as build time environment for config server URL 
ARG spring_config_url

# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

ENV glowroot_env
ENV command_to_run

# environment variable to pass github branch to pickup configuration from, at docker runtime
ENV spring_config_label_env=${spring_config_label}

# environment variable to pass spring configuration url, at docker runtime
ENV spring_config_url_env=${spring_config_url}

ADD http://13.71.87.138:8040/artifactory/libs-release-local/io/mosip/kernel/kernel-otpmanager-service/glowroot.zip glowroot.zip
RUN ls

RUN apt-get update && apt-get install -y unzip
RUN unzip glowroot.zip
RUN rm -rf glowroot.zip

RUN ls
RUN ls glowroot/ 

COPY ./target/kernel-otpmanager-service-*.jar kernel-otpmanager-service.jar

EXPOSE 8085

RUN if [ "$glowroot_env" = "preprod" ]; then \
      command_to_run="java","-jar","-javaagent:glowroot/glowroot.jar","-Dspring.cloud.config.label=${spring_config_label_env}","-Dspring.profiles.active=${active_profile_env}","-Dspring.cloud.config.uri=${spring_config_url_env}","kernel-otpmanager-service.jar" \
    else \
      command_to_run="java","-jar","-Dspring.cloud.config.label=${spring_config_label_env}","-Dspring.profiles.active=${active_profile_env}","-Dspring.cloud.config.uri=${spring_config_url_env}","kernel-otpmanager-service.jar" \

CMD ["$command_to_run"]
