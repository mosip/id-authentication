# mosip-platform
This repository contains the source code of MOSIP platform. To know what is MOSIP, its architecture, external integrations, releases, etc., please check [Wiki](https://github.com/mosip/mosip-docs/wiki)

### Introduction
MOSIP consists of below modules - 
1. `Kernel` - Kernel module provides a bedrock to build and run services by providing several significant necessary technical functions. It contains common functionalities which are used by more than one modules.
2. `Pre-Registration` - Pre-Registration module enables Individuals to book for an appointment in a Registration Centre, by providing basic demographic details.
3. `Registration` - Registration module provides a desktop application for Registration Officers/Supervisors to register an Individual in MOSIP, by capturing demographic and biometric details of an Individual.
4. `Registration Processor` - Registration Processor validates and processes Individual's data received from Registration and eventually generate a UIN (Unique Identification Number) for the Individual.
5. `ID Repository` - ID Repository module acts as a repository of Individual's data along with UIN mapped.
6. `ID Authentication` - ID Authentication module enables a Partner to authenticate an Individual.

### Build
Below commands should be run in the parent project to build all the modules - 
`mvn clean install`
The above command can be used to build individual modules when run in their respective folders

### Deploy
Below command should be executed to run any service locally in specific profile and local configurations - 
`java -Dspring.profiles.active=<profile> -jar <jar-name>.jar`

Below command should be executed to run any service locally in specific profile and `remote` configurations - 
`java -Dspring.profiles.active=<profile> -Dspring.cloud.config.uri=<config-url> -Dspring.cloud.config.label=<config-label> -jar <jar-name>.jar`

Below command should be executed to run a docker image - 
`docker run -it -p <host-port>:<container-port> -e active_profile_env={profile} -e spring_config_label_env= {branch} -e spring_config_url_env={config_server_url} <docker-registry-IP:docker-registry-port/<dcker-image>`

### Configurations
All the configurations used by the codebase in `mosip-platform` is present in [mosip-config](https://github.com/mosip/mosip-config) repository.

### Functional Test-cases
Functional tests run against the codebase in `mosip-platform` is present in [mosip-functional-tests](https://github.com/mosip/mosip-functional-tests) repository.

### Documentation
Relevant documents to get started with MOSIP can be found in [mosip-docs](https://github.com/mosip/mosip-docs) repository. 
In order to get started, please refer - [Getting-Started](https://github.com/mosip/mosip-docs/wiki/Getting-Started)