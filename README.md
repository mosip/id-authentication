[![Build Status](https://travis-ci.com/mosip/id-authentication.svg?branch=master)](https://travis-ci.com/mosip/id-authentication)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mosip_id-authentication&metric=alert_status)](https://sonarcloud.io/dashboard?id=mosip_id-authentication)

# ID-Authentication
This repository contains the source code and design documents for MOSIP ID-Authentication module. ID-Authentication module enables a Partner to authenticate an individual. To know more about MOSIP, its architecture, external integrations, releases, etc..., please check the [Platform Documentation](https://github.com/mosip/mosip-docs/wiki)

### Dependencies
ID-Authentication services' dependencies are mentioned below.  For all Kernel services refer to [commons repo](https://github.com/mosip/commons)
* Common dependencies for all IDA services:
  * kernel-auditmanager-service 
  * kernel-authmanager-service 
  * kernel-config-server 
  * id-repository-identity-service
  * id-repository-vid-service
  
* authentication-service
  * kernel-otpmanager-service - For OTP validation
  * kernel-smsnotification-service
  * kernel-emailnotification-service
  * kernel-masterdata-service
  
* authentication-internal-service
  * kernel-otpmanager-service - For OTP validation
  * kernel-masterdata-service
  
* authentication-otp-service
  * kernel-otpmanager-service - Transient Dependency invoked using kernel-authmanager-service's sendOTP service
  
* authentication-kyc-service
  * kernel-otpmanager-service - For OTP validation
  * kernel-masterdata-service

* Other Dependencies:
  * Bio-SDK used by IDA for Biometric Authentication
  * Soft HSM

* Transient Dependencies
  * kernel-otpmanager-service - Transient Dependency invoked by kernel-authmanager-service's sendOTP service
  * kernel-smsnotification - Transient Dependency invoked by kernel-otpmanager-service
  * kernel-emailnotification-service - Transient Dependency invoked by kernel-otpmanager-service
  
* Other Transient Dependencies
  * HDFS - used by ID-Repository
  * Keycloak/LDAP - Used by kernel-authmanager-service
  * SMTP/SMSE - for email/sms notification by kernel-emailnitification-service and kernel-smsnotification-service


### Build
The following commands should be run in the parent project to build all the modules - 
`mvn clean install`
The above command can be used to build individual modules when run in their respective folders

### Deploy

#### Pre-requesites to run ID-Authentication services in an environment setup
Following two pre-requisites things needs to be run only once when setting up an environment. It is not required to run them whenever any ID-Authentication service is re-deployed in the same environment.

1. ID-Authentication Keys Generator:

This is used to generate the encryption/decrption keys used in ID-Authentication services and populate them to the tables in ID-Authentication database. Below is the command to run the ID-Authentication Keys Generator
```
docker run -it -e artifactory_url_env=<artifactory-url> -e PKCS11_PROXY_SOCKET=<softhsm-url> -e spring_config_label_env=<config-label> -e active_profile_env=<profile> -e spring_config_url_env=<config-url> <docker-registry-IP:docker-registry-port>/authentication-keys-generator:<image_tag>
```

For example,
```
docker run -it -e artifactory_url_env="http://artifcatory-url:8040" -e PKCS11_PROXY_SOCKET="tcp://softhsm-server:5666" -e spring_config_label_env="master" -e active_profile_env="dev" -e spring_config_url_env="http://config-server/config" mosipdev/authentication-keys-generator:1.0.9
```

2. ID-Authentication Salt Generator:

This is used to generate the salts used in ID-Authentication services and populate them to the tables in ID-Authentication database.  Below is the command to run the ID-Authentication Salt Generator.
```
docker run -it -e active_profile_env=<profile>  -e spring_config_label_env=<config-label> -e spring_config_url_env=<config-url> -e spring_config_name_env=id-authentication -e table_name=<property_defining_the_table_name> <docker-registry-IP:docker-registry-port>/kernel-salt-generator:<image_tag>
```

Salts for ID-Authentication need to be populated in two tables-  **uin_hash_salt** and **uin_encrypt_salt**, as below:

i.	Sample command to populate salt in **uin_hash_salt** Table:
```
docker run -it -e active_profile_env=dev  -e spring_config_label_env=master -e spring_config_url_env=http://config-server/config -e spring_config_name_env=id-authentication -e table_name=javax.persistence.jdbc.uinHashTable mosipdev/kernel-salt-generator:1.0.9
```

 ii.	Sample command to populate salt in **uin_encrypt_salt** Table:
```
docker run -it -e active_profile_env=dev  -e spring_config_label_env=master -e spring_config_url_env=http://104.211.212.28:51000 -e spring_config_name_env=id-authentication -e table_name=javax.persistence.jdbc.uinEncryptTable mosipdev/kernel-salt-generator:1.0.9
```

#### Running ID-Authentication services
* The following command should be executed to run any service locally in specific profile and local configurations
````
java -Dspring.profiles.active=<profile> -jar <jar-name>.jar
````

* The following command should be executed to run any service locally in specific profile and `remote` configurations
````
java -Dspring.profiles.active=<profile> -Dspring.cloud.config.uri=<config-url> -Dspring.cloud.config.label=<config-label> -jar <jar-name>.jar
````

* The following command should be executed to run a docker image
````
docker run --rm -d -p <host-port>:<container-port> -e active_profile_env={profile} -e spring_config_label_env={branch} -e spring_config_url_env={config_server_url} <docker-registry-IP:docker-registry-port>/<dcker-image>
````

For example,
* Command run authentication-service
```
docker run --rm  -d -p 8090:8090 -v /softhsm:/softhsm/var/lib/softhsm/ -e spring_config_label_env=master -e active_profile_env=dev -e spring_config_url_env=http://config-server/config mosipdev/authentication-service:latest
```

* Command run authentication-internal-service
```
docker run --rm  -d -p 8093:8093 -v /softhsm:/softhsm/var/lib/softhsm/ -e spring_config_label_env=master -e active_profile_env=dev -e spring_config_url_env=http://config-server/config mosipdev/authentication-internal-service:latest
```

* Command run authentication-kyc-service
```
docker run --rm  -d -p 8091:8091 -v /softhsm:/softhsm/var/lib/softhsm/ -e spring_config_label_env=master -e active_profile_env=dev -e spring_config_url_env=http://config-server/config mosipdev/authentication-otp-service:latest
```

* Command run authentication-otp-service
```
docker run --rm  -d -p 8092:8092 -v /softhsm:/softhsm/var/lib/softhsm/ -e spring_config_label_env=master -e active_profile_env=dev -e spring_config_url_env=http://config-server/config mosipdev/authentication-otp-service:latest
```

### Configurations
All the configurations used by the codebase in `mosip-platform` is present in [mosip-config](https://github.com/mosip/mosip-config) repository.

### Functional Test-cases
Functional tests run against the codebase in `mosip-platform` is present in [mosip-functional-tests](https://github.com/mosip/mosip-functional-tests) repository.

### Documentation
Relevant documents to get started with MOSIP can be found in [mosip-docs](https://github.com/mosip/mosip-docs) repository. 
In order to get started, please refer to the [Getting-Started](https://github.com/mosip/mosip-docs/wiki/Getting-Started) guide.

### Infra
Automated scripts to build and deploy MOSIP modules are present in [mosip-infra](https://github.com/mosip/mosip-infra) repository.


---

### Contribute
You can contribute to MOSIP! 

We want to engage constructively with the community.  If you find a **vulnerability** or issue, please file a bug with the respective repository.  We welcome pull requests with fixes too.  Please see the [Contributor Guide](https://github.com/mosip/mosip-docs/wiki/Contributor-Guide) on how to file bugs, contribute code, and more.

### License
This project is licensed under the terms of [Mozilla Public License 2.0](https://github.com/mosip/mosip-platform/blob/master/LICENSE)

### Communication
Join the [developer mailing list](https://groups.io/g/mosip-dev)


You may also be interested in joining our community room on Gitter via [![Gitter](https://badges.gitter.im/mosip-community/community.svg)](https://gitter.im/mosip-community/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)  where you could get some great community support

