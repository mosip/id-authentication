FROM openjdk:8

#Uncomment below and Comment above line(i.e. FROM openjdk:8) for OS specific (e.g. Alpine OS ) docker base image
#FROM openjdk:8-jdk-alpine

COPY ./target/kernel-uingenerator-service-*.jar kernel-uingenerator-service.jar

COPY ./swagger-ui swagger-ui

EXPOSE 8080

CMD if [ "$active_profile_env" = "preprod" ]; then \
    wget 'http://13.71.87.138:8040/artifactory/libs-release-local/io/mosip/testing/glowroot.zip' ; \
    apt-get update && apt-get install -y unzip ; \
    unzip glowroot.zip ; \
    rm -rf glowroot.zip ; \
    
    sed -i 's/<service_name>/kernel-uingenerator-service/g' glowroot/glowroot.properties ; \
    java -jar -javaagent:glowroot/glowroot.jar kernel-uingenerator-service.jar ; \
    else \
    java -jar kernel-uingenerator-service.jar ; \
    fi

#CMD ["java","-jar","kernel-uingenerator-service.jar"]
