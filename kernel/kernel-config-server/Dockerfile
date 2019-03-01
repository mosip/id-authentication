FROM openjdk:8

#Uncomment below and Comment above line(i.e. FROM openjdk:8) for OS specific (e.g. Alpine OS ) docker base image
#FROM openjdk:8-jdk-alpine

# can be passed during Docker build as build time environment for github URL to pickup configuration from.
ARG git_url

# can be passed during Docker build as build time environment for configuration URL in GIT 
ARG git_config_folder


# environment variable to pass GIT URL at docker runtime
ENV git_url_env=${git_url}

# environment variable to pass github configuration folder to pickup configuration from, at docker runtime
ENV git_config_folder_env=${git_config_folder}

COPY ./target/kernel-config-server-*.jar kernel-config-server.jar

EXPOSE 51000

CMD ["java","-jar","-Dspring.cloud.config.server.git.uri=${git_url_env}","-Dspring.cloud.config.server.git.search-paths=${git_config_folder_env}","kernel-config-server.jar"]
