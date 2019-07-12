## Registration Processor
Registration Processor validates and processes Individual's data received from Registration and eventually generate a UIN (Unique Identification Number) for the Individual.

Registration Processor module receives registration packets from Registration and then, validates and processes Individual's data and generated UIN. This UIN along with IDentity Details of the Individual is stored in ID Repository module.

Registration Processor has APIs to check status and upload packets. The API specs are at https://github.com/mosip/mosip-docs/wiki/Registration-Processor-APIs

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
