FROM openjdk:8

#Uncomment below and Comment above line(i.e. FROM openjdk:8) for OS specific (e.g. Alpine OS ) docker base image
#FROM openjdk:8-jdk-alpine

# setting labels for extra properties needed for this Docker image, so that it can be used at run time with docker inspect
LABEL network=host
LABEL privileged=true
LABEL zone_env=dmz 

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG spring_config_label

# can be passed during Docker build as build time environment for spring profiles active
ARG active_profile

# can be passed during Docker build as build time environment for config server URL
ARG spring_config_url

# can be passed during Docker build as build time environment for zone
ARG zone

# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

# environment variable to pass zone i.e dmz or secure at docker runtime
ENV zone_env=${zone}

# environment variable to pass github branch to pickup configuration from, at docker runtime
ENV spring_config_label_env=${spring_config_label}

# environment variable to pass spring configuration url, at docker runtime
ENV spring_config_url_env=${spring_config_url}


# copying shaded jar into docker image
COPY ./target/registration-processor-common-camel-bridge-*.jar registration-processor-common-camel-bridge.jar


# running java jar
CMD java -jar -Dspring.cloud.config.label=${spring_config_label_env} -Dspring.profiles.active=${active_profile_env} -Dspring.cloud.config.uri=${spring_config_url_env} -Dregistration.processor.zone=${zone_env} registration-processor-common-camel-bridge.jar