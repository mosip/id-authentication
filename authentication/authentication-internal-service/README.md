# Internal Authentication Service

## About
Internal Authentication Service is used by internal MOSIP modules such as Registration Processor, Registration Client and Resident to authenticate an individual's UIN/VID using one ore more authentication types.

## Authentication types
Any combination of the supported [authentication types](https://docs.mosip.io/1.2.0/id-authentication#authentication-types) may be used.
  
## Modalities
* Refer [biometric modalities](https://docs.mosip.io/1.2.0/biometrics#modalities).
* Above authentication types can be allowed/disallowed/mandated by the [configuration](../../docs/configuration.md)
  and the [Authentication/E-KYC Partner's Policy](../../docs/configuration.md).

## Endpoints
* Internal Authentication - used by Internal MOSIP modules for authenticating an individual's UIN/VID

```
POST /idauthentication/v1/internal/auth
```

* Authentication Transaction History - Used by Resident service to retrieve the authentication transaction history for an individual (UIN and VIDs).

```
GET /idauthentication/v1/internal/authTransactions/individualId/{ID}
```

## Callbacks for WebSub 
* Credential Issuance callback - to process incoming credentials and store it into IDA DB
* ID Activate/De-activate/Remove callback - to process UIN/VID Activate/De-activate/Remove events sent from ID Repository and update them in IDA DB.
* Authentication Type Status update callback - to process event of Authentication type lock status sent from ID Repository and update them in IDA DB.
* Partner/MISP data update callback - to process  WebSub message sent from Partner Management Service and store partner/policy/MISP license key data into IDA DB
* Master data update callback- to process WebSub message sent from Master data service and clear master data cache, so that in next authentication the master data is re-cached


## Dependencies
* Kernel OTP Manager service: To generate OTP.
* Kernal Notification Service - for sending notifications for Authentication Success/Failure
* Kernel Audit Service
* Keycloak serivce - To verify authentication token in incoming request, and to get authentication token for connecting to the above kernel services
* WebSub - for getting events for Credential data/ IDentity data/ Partner data/ Master data updates.
* Bio-SDK HTTP service - for biometric authentication
* HSM - for retrieving encryption/decryption keys.

## Overview
This repository contains source code and design documents for MOSIP ID Authentication internal service which is the server-side module to manage [ID Authentication](https://docs.mosip.io/1.2.0/modules/id-authentication-services). The modules exposes API endpoints.

## Databases
Refer to [SQL scripts](../../db_scripts).

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
    $ cd authentication/authentication-internal-service
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
Automated functional tests available in [Functional Tests](../../api-test).

## APIs
API documentation is available [here](https://mosip.github.io/documentation/).

## License
This project is licensed under the terms of [Mozilla Public License 2.0](../../LICENSE).
