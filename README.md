[![Maven Package upon a push](https://github.com/mosip/id-authentication/actions/workflows/push-trigger.yml/badge.svg?branch=develop-java21)](https://github.com/mosip/id-authentication/actions/workflows/push-trigger.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mosip_id-authentication&id=mosip_id-authentication&branch=develop-java21&metric=alert_status)](https://sonarcloud.io/dashboard?id=mosip_id-authentication&branch=develop-java21)

# ID-Authentication 

## Overview
This repository contains source code and design documents for MOSIP ID Authentication which is the server-side module to manage [ID Authentication](https://docs.mosip.io/1.2.0/modules/id-authentication-services). The modules exposes API endpoints.  

## Databases
Refer to [SQL scripts](db_scripts).

## Build & run (for developers)
The project requires JDK 21.0.3
and mvn version - 3.9.6

### Remove the version-specific suffix (PostgreSQL95Dialect) from the Hibernate dialect configuration
   ```
   hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   ```
This is for better compatibility with future PostgreSQL versions.

### Configure ANT Path Matcher for Spring Boot 3.x compatibility.
   ```
   spring.mvc.pathmatch.matching-strategy=ANT_PATH_MATCHER
   ```
This is to maintain compatibility with existing ANT-style path patterns.

1. Build and install:
    ```
    $ cd authentication
    $ mvn install -DskipTests=true -Dmaven.javadoc.skip=true -Dgpg.skip=true
    ```
2. Build Docker for a service:
    ```
    $ cd <service folder>
    $ docker build -f Dockerfile
    ```
### Add auth-adapter in a class-path to run a services
   ```
   <dependency>
       <groupId>io.mosip.kernel</groupId>
       <artifactId>kernel-auth-adapter</artifactId>
       <version>${kernel.auth.adapter.version}</version>
   </dependency>
   ```

## Configuration
[Configuration-id-authentication](https://github.com/mosip/mosip-config/blob/master/id-authentication-default.properties)and
[Configuration-id-authentication-external](https://github.com/mosip/mosip-config/blob/master/id-authentication-external-default.properties) and
[Configuration-id-authentication-internal](https://github.com/mosip/mosip-config/blob/master/id-authentication-internal-default.properties) and
[Configuration-id-authentication-otp](https://github.com/mosip/mosip-config/blob/master/id-authentication-otp-default.properties) and
[Configuration-Application](https://github.com/mosip/mosip-config/blob/master/application-default.properties) defined here.


## Deployment in K8 cluster with other MOSIP services:
### Pre-requisites
* Set KUBECONFIG variable to point to existing K8 cluster kubeconfig file:
    ```
    export KUBECONFIG=~/.kube/<k8s-cluster.config>
    ```
	
	Below are the dependent services required for IDA:
   | Chart | Chart version |
   |---|---|
   |[Keycloak](https://github.com/mosip/keycloak/tree/release-1.3.x/deploy) | 7.1.18 |
   |[Keycloak-init](https://github.com/mosip/keycloak/tree/release-1.3.x/deploy) | 1.3.0-beta.1 |
   |[Postgres](https://github.com/mosip/postgres-init/tree/release-1.3.x/deploy) | 13.1.5 |
   |[Postgres Init](https://github.com/mosip/postgres-init/tree/release-1.3.x/deploy) | 1.3.0-beta.1 |
   |[Minio](https://github.com/mosip/mosip-infra/blob/v1.2.0.2/deployment/v3/external/object-store) | 10.1.6 |
   |[Kafka](https://github.com/mosip/mosip-infra/tree/v1.2.0.2/deployment/v3/external/kafka) | 0.4.2 |
   |[Config-server](https://github.com/mosip/commons/tree/v1.3.0-beta.2/deploy/config-server) | 1.3.0-beta.2 |
   |[Websub](https://github.com/mosip/websub/tree/v1.3.0-beta.1/deploy) | 1.3.0-beta.1 |
   |[Artifactory server](https://github.com/mosip/artifactory-ref-impl/tree/v1.3.0-beta.2/deploy) | 1.3.0-beta.2 |
   |[Keymanager service](https://github.com/mosip/keymanager/tree/v1.3.0-beta.2/deploy/keymanager) | 1.3.0-beta.2 |
   |[Kernel](https://github.com/mosip/commons/tree/v1.3.0-beta.2/deploy/kernel) | 1.3.0-beta.2 |
   |[Biosdk service](https://github.com/mosip/biosdk-services/tree/v1.3.0-beta.1/deploy) | 1.3.0-beta.1 |
   |[Idrepository](https://github.com/mosip/id-repository/tree/v1.3.0-beta.1/deploy) | 12.0.1-B2 |
   |[PMS](https://github.com/mosip/partner-management-services/tree/v1.2.2.0/deploy) | 1.2.2.0 |
   |[Datashare](https://github.com/mosip/durian/tree/v1.3.0-beta.1/deploy) | 1.3.0-beta.1 |
   
   
### Install
  ```
    $ cd deploy
    $ ./install.sh
   ```
### Delete
  ```
    $ cd deploy
    $ ./delete.sh
   ```
### Restart
  ```
    $ cd deploy
    $ ./restart.sh
   ```

## Test
Automated functional tests available in [api-test folder](api-test).

## APIs
API documentation is available [here](https://mosip.github.io/documentation/).

## License
This project is licensed under the terms of [Mozilla Public License 2.0](LICENSE).
