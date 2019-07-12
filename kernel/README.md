## Kernel

Kernel module provides a bedrock to build and run services by providing several significant necessary technical functions. It contains common functionalities which are used by more than one modules.

All MOSIP modules are dependent on Kernel modules for common Java and REST APIs.

### Kernel Dependencies

https://github.com/mosip/mosip/wiki/Kernel-Dependencies


**MOSIP Modules Components**

![](../docs/design/kernel/_images/MOSIP_modules_components.png)   

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

