FROM openjdk:8

COPY ./target/kernel-pridgenerator-service-*.jar kernel-pridgenerator-service.jar

EXPOSE 8100

# CMD if [ "$active_profile_env" = "c3a9236e12bb95f086275ff4647a7909dc8a0636e44b1a00f033a0c181255fc9" ]; then \
#     wget 'http://13.71.87.138:8040/artifactory/libs-release-local/io/mosip/testing/glowroot.zip' ; \
#     apt-get update && apt-get install -y unzip ; \
#     unzip glowroot.zip ; \
#     rm -rf glowroot.zip ; \
    
#     sed -i 's/<service_name>/kernel-pridgenerator-service/g' glowroot/glowroot.properties ; \
#     java -jar -javaagent:glowroot/glowroot.jar -Dspring.cloud.config.label="${spring_config_label_env}" -Dspring.profiles.active="${active_profile_env}" -Dspring.cloud.config.uri="${spring_config_url_env}" kernel-pridgenerator-service.jar ; \
#     else \
#     java -jar -Dspring.cloud.config.label="${spring_config_label_env}" -Dspring.profiles.active="${active_profile_env}" -Dspring.cloud.config.uri="${spring_config_url_env}" kernel-pridgenerator-service.jar ; \
#     fi

CMD ["java","-jar","kernel-pridgenerator-service.jar"]
