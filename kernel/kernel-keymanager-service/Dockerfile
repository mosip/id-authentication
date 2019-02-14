FROM docker-registry.mosip.io:5000/kernel-keymanager-softhsm:0.1

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG spring_config_label

# can be passed during Docker build as build time environment for spring profiles active 
ARG active_profile

# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

# environment variable to pass github branch to pickup configuration from, at docker runtime
ENV spring_config_label_env=${spring_config_label}

RUN mkdir /config

COPY softhsm-application.conf /config

COPY ./target/kernel-keymanager-service-*.jar kernel-keymanager-service.jar

VOLUME /softhsm

EXPOSE 8088

CMD ["java","-jar","-Dspring.cloud.config.label=${spring_config_label_env}","-Dspring.profiles.active=${active_profile_env}","kernel-keymanager-service.jar"]
