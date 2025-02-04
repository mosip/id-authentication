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


## Dependency matrix for id-authentication services

| Chart                                                                                                       | Chart version |
|-------------------------------------------------------------------------------------------------------------|---------------|
| [Keycloak](https://github.com/mosip/mosip-infra/tree/v1.2.0.2/deployment/v3/external/iam)                   | 7.1.18        |
| [Keycloak-init](https://github.com/mosip/mosip-infra/tree/v1.2.0.2/deployment/v3/external/iam)              | 1.2.0.2       |
| [Postgres](https://github.com/mosip/mosip-infra/tree/v1.2.0.2/deployment/v3/external/postgres)              | 10.16.2       |
| [Postgres Init](https://github.com/mosip/mosip-infra/tree/v1.2.0.2/deployment/v3/external/postgres)         | 1.2.0.2       |
| [Minio](https://github.com/mosip/mosip-infra/tree/v1.2.0.2/deployment/v3/external/object-store)             | 10.1.6        |
| [Kafka](https://github.com/mosip/mosip-infra/tree/v1.2.0.2/deployment/v3/external/kafka)                    | 0.4.2         |
| [Config-server](https://github.com/mosip/mosip-infra/tree/v1.2.0.2/deployment/v3/mosip/config-server)       | 1.2.0.2       |
| [Websub](https://github.com/mosip/mosip-infra/tree/v1.2.0.2/deployment/v3/mosip/websub)                     | 1.2.0.2       |
| [Artifactory server](https://github.com/mosip/mosip-infra/tree/v1.2.0.1-B3/deployment/v3/mosip/artifactory) | 12.0.1-B3     |
| [Keymanager service](https://github.com/mosip/mosip-infra/blob/v1.2.0.1-B3/deployment/v3/mosip/keymanager)  | 12.0.1-B2     |
| [Kernel services](https://github.com/mosip/mosip-infra/blob/v1.2.0.1-B3/deployment/v3/mosip/kernel)         | 12.0.1-B2     |
| [Biosdk service](https://github.com/mosip/mosip-infra/tree/v1.2.0.1-B3/deployment/v3/mosip/biosdk)          | 12.0.1-B3     |
| [Idrepo services](https://github.com/mosip/mosip-infra/blob/v1.2.0.1-B3/deployment/v3/mosip/idrepo)         | 12.0.1-B2     |
| [Pms services](https://github.com/mosip/mosip-infra/blob/v1.2.0.1-B3/deployment/v3/mosip/pms)               | 12.0.1-B3     |
| [IDA services](https://github.com/mosip/mosip-infra/blob/v1.2.0.1-B3/deployment/v3/mosip/ida)               | 12.0.1-B3     |