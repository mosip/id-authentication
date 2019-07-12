## ID Authentication
ID Authentication (IDA) is the authentication module of MOSIP, used to authenticate Individuals using their UIN/VID, via a Partner. 

List of authentication types supported by MOSIP are - 
1. OTP Auth
2. Demographic Auth
3. Biometric Auth (includes Fingerprint, IRIS and Face)
4. Static Pin Auth

Refer wiki page for [ID Authentication API](https://github.com/mosip/mosip/wiki/ID-Authentication-APIs)   


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
 
