FROM openjdk:8

#Uncomment below and Comment above line(i.e. FROM openjdk:8) for OS specific (e.g. Alpine OS ) docker base image
#FROM openjdk:8-jdk-alpine

# can be passed during Docker build as build time environment for  keystore location for encryption
ARG encrypt_keyStore_location

# can be passed during Docker build as build time environment for  keystore password for encryption
ARG encrypt_keyStore_password

# can be passed during Docker build as build time environment for  keystore alias for encryption
ARG encrypt_keyStore_alias

# can be passed during Docker build as build time environment for  keystore secret for encryption
ARG encrypt_keyStore_secret

# environment variable to pass GIT URL at docker runtime
ENV git_url_env=${git_url}

# environment variable to pass github configuration folder to pickup configuration from, at docker runtime
ENV git_config_folder_env=${git_config_folder}

# environment variable to pass encryption keystore location at docker runtime
ENV encrypt_keyStore_location_env=${encrypt_keyStore_location}

# environment variable to pass encryption keystore password at docker runtime
ENV encrypt_keyStore_password_env=${encrypt_keyStore_password}

# environment variable to pass encryption keystore alias at docker runtime
ENV encrypt_keyStore_alias_env=${encrypt_keyStore_alias}

# environment variable to pass encryption keystore secret at docker runtime
ENV encrypt_keyStore_secret_env=${encrypt_keyStore_secret}

ADD execute-jar.sh execute-jar.sh

RUN chmod 777 execute-jar.sh

COPY ./target/kernel-config-server-*.jar kernel-config-server.jar

EXPOSE 51000

CMD  ./execute-jar.sh
