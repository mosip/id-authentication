FROM openjdk:8

#Uncomment below and Comment above line(i.e. FROM openjdk:8) for OS specific (e.g. Alpine OS ) docker base image
#FROM openjdk:8-jdk-alpine

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG spring_config_label

# can be passed during Docker build as build time environment for spring profiles active 
ARG active_profile

# can be passed during Docker build as build time environment for config server URL 
ARG spring_config_url

ARG bio_sdk_folder=mock/0.9

ARG softhsm_client_zip=client.zip

# can be passed during Docker build as build time environment for glowroot 
ARG is_glowroot

# can be passed during Docker build as build time environment for artifactory URL
ARG artifactory_url

# environment variable to pass active profile such as DEV, QA etc at docker runtime
ENV active_profile_env=${active_profile}

# environment variable to pass github branch to pickup configuration from, at docker runtime
ENV spring_config_label_env=${spring_config_label}

# environment variable to pass spring configuration url, at docker runtime
ENV spring_config_url_env=${spring_config_url}

# environment variable to pass glowroot, at docker runtime
ENV is_glowroot_env=${is_glowroot}

# environment variable to pass artifactory url, at docker runtime
ENV artifactory_url_env=${artifactory_url}

ENV bio_sdk_folder_env=${bio_sdk_folder}

ENV softhsm_client_zip_env=${softhsm_client_zip}

ENV work_dir_env=/

ENV current_module_env=authentication-service

ADD configure_start.sh configure_start.sh

RUN chmod +x configure_start.sh

ADD target/${current_module_env}-*.jar ${current_module_env}.jar

EXPOSE 8090

EXPOSE 9010

ENTRYPOINT [ "/configure_start.sh" ]

CMD if [ "$is_glowroot_env" = "present" ]; then \
    wget "${artifactory_url_env}"/artifactory/libs-release-local/io/mosip/testing/glowroot.zip ; \
    apt-get update && apt-get install -y unzip ; \
    unzip glowroot.zip ; \
    rm -rf glowroot.zip ; \
    sed -i "s/<service_name>/${current_module_env}/g" glowroot/glowroot.properties ; \
    java -jar -Djava.security.debug=sunpkcs11 -javaagent:glowroot/glowroot.jar -Dspring.cloud.config.label="${spring_config_label_env}" -Dspring.profiles.active="${active_profile_env}" -Dspring.cloud.config.uri="${spring_config_url_env}" -Dloader.path="${loader_path_env}" -Dfile.encoding="UTF-8" ${current_module_env}.jar ; \
    else \
    java -jar -Djava.security.debug=sunpkcs11 -Dspring.cloud.config.label="${spring_config_label_env}" -Dspring.profiles.active="${active_profile_env}" -Dspring.cloud.config.uri="${spring_config_url_env}" -Dloader.path="${loader_path_env}" -Dfile.encoding="UTF-8" ${current_module_env}.jar ; \
    fi

#Sample docker run command:
# sudo docker run --rm -it -e artifactory_url_env="http://artifactory" -e spring_config_label_env="1.0.9" -e active_profile_env="dev" -e spring_config_url_env="http://config-server/config" -e PKCS11_PROXY_SOCKET=tcp://softhsm-ida:5666 -p 8090:8090 authentication-service:1.0.9