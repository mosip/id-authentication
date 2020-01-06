## id-repository

ID Repository acts as a repository of Identity details of an Individual, and provides API based mechanism to store and retrieve Identity details by 1Registration Processor module.

Following are the pre-requisites for storing or retrieving Identity authentication of an individual

1. ID Repository accepts ID JSON in the format as provided by the country in ID Schema
2. ID JSON present in ID Repository APIs gets validated against IdObjectValidator.

[Api Documentation]( https://github.com/mosip/mosip-docs/wiki/ID-Repository-API)

**Configuration**
Configurations used for ID Repo are available in [mosip-config](https://github.com/mosip/mosip-config)


### Build
Below command should be run in the parent project **authentication**
`mvn clean install`

### Deploy
Below command should be executed to run any service locally in specific profile and local configurations - 
`java -Dspring.profiles.active=<profile> -jar <jar-name>.jar`

Below command should be executed to run any service locally in specific profile and `remote` configurations - 
`java -Dspring.profiles.active=<profile> -Dspring.cloud.config.uri=<config-url> -Dspring.cloud.config.label=<config-label> -jar <jar-name>.jar`

Below command should be executed to run a docker image - 
`docker run -it -p <host-port>:<container-port> -e active_profile_env={profile} -e spring_config_label_env= {branch} -e spring_config_url_env={config_server_url} <docker-registry-IP:docker-registry-port/<docker-image>`

`Sample Build and Deployment commands:`
docker run -it -d -p 8092:8092 -e active_profile_env={profile}  -e spring_config_label_env= {branch} -e spring_config_url_env={config_server_url} -e schema_name={schema} -e table_name={table} docker-registry.mosip.io:5000/id-repository-salt-generator

docker run -it -d -p 8090:8090 -e active_profile_env={profile}  -e spring_config_label_env= {branch} -e spring_config_url_env={config_server_url} docker-registry.mosip.io:5000/id-repository-identity-service

docker run -it -d -p 8091:8091 -e active_profile_env={profile}  -e spring_config_label_env= {branch} -e spring_config_url_env={config_server_url} docker-registry.mosip.io:5000/id-repository-vid-service