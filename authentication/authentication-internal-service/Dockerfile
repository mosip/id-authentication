ARG docker_registry_url=docker-registry.mosip.io:5000
FROM ${docker_registry_url}/tech5-java8-base:1.0

#Uncomment below and Comment above line(i.e. FROM openjdk:8) for OS specific (e.g. Alpine OS ) docker base image
#FROM openjdk:8-jdk-alpine

# can be passed during Docker build as build time environment for github branch to pickup configuration from.
ARG spring_config_label

# can be passed during Docker build as build time environment for spring profiles active 
ARG active_profile

# can be passed during Docker build as build time environment for config server URL 
ARG spring_config_url

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

ADD target/authentication-internal-service-*.jar ./opt/Tech5/authentication-internal-service.jar

RUN cd /opt/Tech5
WORKDIR /opt/Tech5

EXPOSE 8093

CMD if [ "$is_glowroot_env" = "present" ]; then \
    wget "${artifactory_url_env}"/artifactory/libs-release-local/io/mosip/testing/glowroot.zip ; \
    apt-get update && apt-get install -y unzip ; \
    unzip glowroot.zip ; \
    rm -rf glowroot.zip ; \
    
    sed -i 's/<service_name>/authentication-internal-service/g' glowroot/glowroot.properties ; \
    java -javaagent:glowroot/glowroot.jar -Dspring.cloud.config.label="${spring_config_label_env}" -Dspring.profiles.active="${active_profile_env}" -Dspring.cloud.config.uri="${spring_config_url_env}" -Dloader.path=commons-codec-1.9.jar kernel-cbeffutil-api-0.12.18.jar kernel-core-0.12.18.jar t5-finger-iris-mosip-auth-v1.2.jar ABISTech5FaceSDKAdapter_v1.5.jar tech5-sdk-docker-v1.0.jar -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.rmi.port=8190 -Dcom.sun.management.jmxremote.port=8190 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.local.only=false -Djava.rmi.server.hostname=13.71.81.26 -Dcom.sun.management.jmxremote.local.only=false -jar authentication-internal-service.jar ; \
    else \
    java -Dspring.cloud.config.label="${spring_config_label_env}" -Dspring.profiles.active="${active_profile_env}" -Dspring.cloud.config.uri="${spring_config_url_env}" -Dloader.path=commons-codec-1.9.jar kernel-cbeffutil-api-0.12.18.jar kernel-core-0.12.18.jar t5-finger-iris-mosip-auth-v1.2.jar ABISTech5FaceSDKAdapter_v1.5.jar tech5-sdk-docker-v1.0.jar -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.rmi.port=8190 -Dcom.sun.management.jmxremote.port=8190 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.local.only=false -Djava.rmi.server.hostname=13.71.81.26 -Dcom.sun.management.jmxremote.local.only=false -jar authentication-internal-service.jar ; \
    fi


#CMD ["java","-jar","-Dspring.cloud.config.label=${spring_config_label_env}","-Dspring.profiles.active=${active_profile_env}","-Dspring.cloud.config.uri=${spring_config_url_env}", "-Dcom.sun.management.jmxremote=true", "-Dcom.sun.management.jmxremote.rmi.port=8190" , "-Dcom.sun.management.jmxremote.port=8190", "-Dcom.sun.management.jmxremote.ssl=false" ,"-Dcom.sun.management.jmxremote.authenticate=false",  "-Dcom.sun.management.jmxremote.local.only=false", "-Djava.rmi.server.hostname=13.71.81.26", "-Dcom.sun.management.jmxremote.local.only=false", "authentication-internal-service.jar"]
