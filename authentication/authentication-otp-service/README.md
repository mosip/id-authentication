# OTP Request Service

## About
OTP Request is used by Authentication/E-KYC Partners to generate OTP for an individual's UIN/VID. The generated OTP is stored in IDA DB for validation during [OTP Authentication](https://docs.mosip.io/1.2.0/id-authentication)

## Partner/MISP validation
* Below partner/MISP data are validated before processing the authentication request:
  1. MISP License Key
  2. Partner ID
  3. Partner API Key

## Endpoints
* OTP Request:

```
POST /idauthentication/v1/otp/{MISP-LicenseKey}/{Auth-Partner-ID}/{Partner-Api-Key}
```

## Callbacks for WebSub
* Master data update callback- to process WebSub message sent from Master data service and clear master data cache, so that in next authentication the master data is re-cached

## Dependencies
* Kernel OTP Manager service: To generate OTP.
* Kernal Notification Service: For sending notifications for Authentication Success/Failure
* Kernel Audit Service
* Keycloak serivce: To get authentication token for connecting to the above kernel services
* WebSub: for getting events for Credential data/ IDentity data/ Partner data/ Master data updates.
* HSM: for retrieving encryption/decryption keys.

## Overview
This repository contains source code and design documents for MOSIP ID Authentication OTP service which is the server-side module to manage [ID Authentication](https://docs.mosip.io/1.2.0/modules/id-authentication-services). The modules exposes API endpoints.

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
1. Build Docker for a service:
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
