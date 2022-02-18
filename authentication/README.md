## ID Authentication
ID Authentication (IDA) is the authentication module of MOSIP, used to authenticate Individuals using their UIN/VID, via a Partner. 

List of authentication types supported by MOSIP are - 
1. OTP Authentication
2. Demographic Authentication
3. Biometric Authentication (includes Fingerprint, IRIS and Face)

Refer wiki page for [ID Authentication API](https://github.com/mosip/mosip-docs/wiki/ID-Authentication-API)   


**Configuration**
Configurations used for ID Repo are available in [mosip-config](../docs/configuration.md)

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
 
