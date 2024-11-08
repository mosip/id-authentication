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
[Configuration-id-authentication](https://github.com/mosip/mosip-config/blob/develop/id-authentication-default.properties)and
[Configuration-id-authentication-external](https://github.com/mosip/mosip-config/blob/develop/id-authentication-external-default.properties) and
[Configuration-id-authentication-internal](https://github.com/mosip/mosip-config/blob/develop/id-authentication-internal-default.properties) and
[Configuration-id-authentication-otp](https://github.com/mosip/mosip-config/blob/develop/id-authentication-otp-default.properties) and
[Configuration-Application](https://github.com/mosip/mosip-config/blob/develop/application-default.properties) defined here.


## Deployment in K8 cluster with other MOSIP services:
### Pre-requisites
* Set KUBECONFIG variable to point to existing K8 cluster kubeconfig file:
    ```
    export KUBECONFIG=~/.kube/<k8s-cluster.config>
    ```
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
