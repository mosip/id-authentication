[![Maven Package upon a push](https://github.com/mosip/id-authentication/actions/workflows/push-trigger.yml/badge.svg?branch=release-1.3.x)](https://github.com/mosip/id-authentication/actions/workflows/push-trigger.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mosip_id-authentication&id=mosip_id-authentication&branch=release-1.3.x&metric=alert_status)](https://sonarcloud.io/dashboard?id=mosip_id-authentication&branch=develop-java21)

# ID-Authentication 

## Overview
This repository contains source code and design documents for MOSIP ID Authentication which is the server-side module to manage [ID Authentication](https://docs.mosip.io/1.2.0/modules/id-authentication-services). The modules exposes API endpoints.  

## Databases
Refer to [SQL scripts](db_scripts).

## Build & run (for developers)
The project requires JDK 21.0.3
and mvn version - 3.9.6

### Spring Boot 4 configuration
The services run on Spring Boot 4.1.1 (Spring Framework 7, Hibernate 7, Spring Batch 6). Review these settings in the [mosip-config](https://github.com/mosip/mosip-config) properties used by IDA:

1. Required: keep Jackson 2 for HTTP message conversion (Boot 4 defaults to Jackson 3).
   ```
   spring.http.converters.preferred-json-mapper=jackson2
   ```
2. Remove the Boot 3-era settings; they are not needed any more:
   ```
   hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect          # Hibernate 7 selects the dialect itself (HHH90000025)
   spring.mvc.pathmatch.matching-strategy=ANT_PATH_MATCHER           # Spring MVC now uses PathPatternParser
   ```
3. Renamed or changed keys:
   ```
   spring.datasource.initialization-mode            -> spring.sql.init.mode
   management.metrics.export.prometheus.enabled     -> management.prometheus.metrics.export.enabled
   server.max-http-header-size                      -> server.max-http-request-header-size   (e.g. 10000000 to allow 10MB headers)
   management.endpoint.<id>.enabled                 -> management.endpoint.<id>.access (unrestricted | read-only | none)
   ```
4. Spring Batch job metadata is stored in the `ida.batch_*` tables (`spring-boot-starter-batch-jdbc`). Spring Batch 6 renamed the sequence `BATCH_JOB_SEQ` to `BATCH_JOB_INSTANCE_SEQ`; apply [1.3.1_to_1.4.0_upgrade.sql](db_upgrade_scripts/mosip_ida/sql/1.3.1_to_1.4.0_upgrade.sql) on existing databases.
5. Local runs: `bootstrap.properties` has a placeholder `spring.cloud.config.uri=localhost`; override it (for example `--spring.cloud.config.uri=http://localhost:51000/config`) and add `--spring.cloud.loadbalancer.enabled=false`.

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
(In the Docker image the adapter is downloaded at container start by `configure_start.sh` from `iam_adapter_url_env`; for a local run of the jar put `kernel-auth-adapter` on `-Dloader.path`.)

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
