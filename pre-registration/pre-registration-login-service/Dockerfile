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

# environment variable to pass spring configuration url, at docker runtime
ENV spring_config_url_env=${spring_config_url}

# environment variable to pass github branch to pickup configuration from, at docker runtime
ENV spring_config_label_env=${spring_config_label}

copy ./target/pre-registration-login-service-*.jar pre-registration-login-service.jar

EXPOSE 9090

EXPOSE 4000

CMD ["java","-javaagent:ssh madmin@13.71.92.43 /home/Glowroot/glowroot.jar","-Dspring.cloud.config.label=${spring_config_label_env}","-Dspring.profiles.active=${active_profile_env}","-Dspring.cloud.config.uri=${spring_config_url_env}","-jar","pre-registration-login-service.jar"] 