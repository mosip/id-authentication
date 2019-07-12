## Kernel

Kernel module provides common functionality required by all the functional modules of MOSIP. The key components provided by kernel are

 - Audit Manager
 - PKI infra
 - File System connectors (CEPH & HDFS)
 - UIN Generator
 - Number ID generators (Pre-Reg ID, VID etc...)
 - Virus scanner
 - Transliteration library
 - logger
 - Notification (SMS and email)
 - ...

All MOSIP modules are dependent on Kernel components for common Java and REST APIs.

[Kernel REST APIs](https://github.com/mosip/mosip-docs/wiki/Kernel-APIs)
### Kernel Dependencies

https://github.com/mosip/mosip-docs/wiki/Kernel-Dependencies  

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

