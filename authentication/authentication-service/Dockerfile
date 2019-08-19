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

# environment variable to pass github branch to pickup configuration from, at docker runtime
ENV spring_config_label_env=${spring_config_label}

# environment variable to pass spring configuration url, at docker runtime
ENV spring_config_url_env=${spring_config_url}


ADD target/authentication-service-*.jar authentication-service.jar

#Downloading external dependencies 
ADD http://13.71.87.138:8040/artifactory/libs-release-local/io/mosip/identy-BioSDK/ABISTech5FaceSDKAdapter_v2.jar ABISTech5FaceSDKAdapter_v2.jar
ADD http://13.71.87.138:8040/artifactory/libs-release-local/io/mosip/identy-BioSDK/t5-finger-iris-mosip-auth-v1.2.jar t5-finger-iris-mosip-auth-v1.2.jar 
ADD http://13.71.87.138:8040/artifactory/libs-release-local/io/mosip/identy-BioSDK/tech5-sdk-v1.5.1.jar tech5-sdk-v1.5.1.jar 
ADD http://13.71.87.138:8040/artifactory/libs-release-local/io/mosip/identy-BioSDK/so-files.zip so-files.zip

RUN apt-get update && apt-get install -y unzip
RUN unzip so-files.zip

EXPOSE 8090
RUN ls -la
RUN ls -la so-files/
CMD ["java","-jar","-Dspring.cloud.config.label=${spring_config_label_env}","-Dspring.profiles.active=${active_profile_env}","-Dspring.cloud.config.uri=${spring_config_url_env}", "-Dcom.sun.management.jmxremote=true", "-Dcom.sun.management.jmxremote.rmi.port=8190" , "-Dcom.sun.management.jmxremote.port=8190", "-Dcom.sun.management.jmxremote.ssl=false" ,"-Dcom.sun.management.jmxremote.authenticate=false",  "-Dcom.sun.management.jmxremote.local.only=false", "-Djava.rmi.server.hostname=13.71.81.26", "-Dcom.sun.management.jmxremote.local.only=false", "authentication-service.jar"]
