FROM eclipse-temurin:21-jre

ARG SOURCE
ARG COMMIT_HASH
ARG COMMIT_ID
ARG BUILD_TIME
LABEL source=${SOURCE}
LABEL commit_hash=${COMMIT_HASH}
LABEL commit_id=${COMMIT_ID}
LABEL build_time=${BUILD_TIME}

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG spring_config_label

# can be passed during Docker build as build time environment for spring profiles active
ARG active_profile

# can be passed during Docker build as build time environment for config server URL
ARG spring_config_url

#ARG bio_sdk_folder=mock/0.9
ARG biosdk_zip_path

ARG demosdk_zip_path

# can be passed during Docker build as build time environment for hsm client zip file path
#ARG client_zip_path
ARG hsm_client_zip_path

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

# environment variable to pass iam_adapter url, at docker runtime
ENV iam_adapter_url_env=${iam_adapter_url}

#ENV bio_sdk_folder_env=${bio_sdk_folder}
ENV biosdk_zip_file_path=${biosdk_zip_path}

ENV demosdk_zip_file_path=${demosdk_zip_path}

# environment variable to pass hsm client zip file path, at docker runtime
#ENV zip_file_path=${client_zip_path}
ENV hsm_zip_file_path=${hsm_client_zip_path}

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG container_user=mosip

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG container_user_group=mosip

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG container_user_uid=1002

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG container_user_gid=1001

ARG hsm_local_dir=hsm-client

ENV hsm_local_dir_name=${hsm_local_dir}

#ARG biosdk_local_dir=biosdk-client

#ARG demosdk_local_dir=demosdk

ENV biosdk_local_dir_name=${biosdk_local_dir}

ENV demosdk_local_dir_name=${demosdk_local_dir}

# install packages and create user
RUN apt-get -y update \
&& apt-get install -y unzip sudo adduser \
&& groupadd -g ${container_user_gid} ${container_user_group} \
&& useradd -u ${container_user_uid} -g ${container_user_group} -s /bin/sh -m ${container_user} \
&& adduser ${container_user} sudo \
&& echo "%sudo ALL=(ALL) NOPASSWD:/home/${container_user}/${hsm_local_dir}/install.sh" >> /etc/sudoers

# set working directory for the user
WORKDIR /home/${container_user}

ENV work_dir=/home/${container_user}

ARG loader_path=${work_dir}/additional_jars/

RUN mkdir -p ${loader_path}

ENV loader_path_env=${loader_path}

ENV current_module_env=authentication-internal-service

ADD configure_start.sh /home/${container_user}/configure_start.sh

ADD ./lib/* "${loader_path_env}"/

RUN chmod +x /home/${container_user}/configure_start.sh

#ADD target/${current_module_env}-*.jar ${current_module_env}.jar


RUN chmod a-w /home/${container_user}/configure_start.sh



ADD ./target/*.jar /target/

RUN find /target -name '*.jar' -executable -type f "-print0" | xargs "-0" cp -t / \
    && rm -rf /target \
    && mv /*.jar ${work_dir}/authentication-internal-service.jar


# change permissions of file inside working dir
RUN chown -R ${container_user}:${container_user} /home/${container_user}

# select container user for all tasks
USER ${container_user_uid}:${container_user_gid}

EXPOSE 8093
EXPOSE 9010

ENTRYPOINT [ "./configure_start.sh" ]

CMD wget -q "${iam_adapter_url_env}" -O "${loader_path_env}/kernel-auth-adapter.jar" && java -XX:-UseG1GC -XX:+ExplicitGCInvokesConcurrent -XX:-UseParallelGC -XX:+UseZGC -XX:+ZGenerational -XX:-UseShenandoahGC -XX:MaxGCPauseMillis=200  -XX:+UnlockExperimentalVMOptions -XX:+UseStringDeduplication -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8 -XX:+UseCompressedOops \
-jar -Dloader.path="${loader_path_env}" -Dspring.cloud.config.label="${spring_config_label_env}" -Dspring.profiles.active="${active_profile_env}" -Dspring.cloud.config.uri="${spring_config_url_env}" --add-opens java.base/sun.reflect.annotation=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-modules=ALL-SYSTEM --add-opens java.xml/jdk.xml.internal=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.lang.stream=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED --add-opens java.base/java.time.LocalDate=ALL-UNNAMED --add-opens java.base/java.time.LocalDateTime=ALL-UNNAMED --add-opens java.base/java.time.LocalDateTime.date=ALL-UNNAMED --add-opens java.base/jdk.internal.reflect.DirectMethodHandleAccessor=ALL-UNNAMED ./authentication-internal-service.jar ; \

#Sample docker run command:
# sudo docker run --rm -it -e artifactory_url_env="http://artifactory" -e spring_config_label_env="1.0.9" -e active_profile_env="dev" -e spring_config_url_env="http://config-server/config" -e PKCS11_PROXY_SOCKET=tcp://softhsm-ida:5666 -p 8093:8093 authentication-internal-service:1.0.9