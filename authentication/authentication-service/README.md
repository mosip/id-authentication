# Authentication Service

## About
Authentication Service is used by Authentication/E-KYC Partners 
* to authenticate an individual's UIN/VID using one ore more authentication types.
* to request E-KYC for an individul's UIN/VID using one ore more authentication types.

## Authentication types
Any combination of the supported [authentication types](https://docs.mosip.io/1.2.0/id-authentication#authentication-types) may be used.
  
## Modalities
* Refer [biometric modalities](https://docs.mosip.io/1.2.0/biometrics#modalities).
* Above authentication types can be allowed/disallowed/mandated by the [configuraion](../../docs/configuration.md#allowed-authentication-types) and the [Authentication/E-KYC Partner's Policy](../../docs/configuration.md).

## Partner/MISP validation
* Below partner/MISP data are validated before processing the authentication request:
  1. MISP License Key
  2. Partner ID
  3. Partner API Key

## Endpoints:
* Authentication:

```
POST /idauthentication/v1/auth/{MISP-LicenseKey}/{Auth-Partner-ID}/{Partner-Api-Key}
```

* E-KYC

```
POST /idauthentication/v1/kyc/{MISP-LicenseKey}/{Auth-Partner-ID}/{Partner-Api-Key}
```

## Callbacks for WebSub
* Master data update callback- to process WebSub message sent from Master data service and clear master data cache, so that in next authentication the master data is re-cached

## Dependencies
* Kernal Notification Service: For sending notifications for Authentication Success/Failure
* Kernel Audit Service
* Keycloak serivce: To get authentication token for connecting to the above kernel services
* WebSub: For getting events for Credential data/ IDentity data/ Partner data/ Master data updates.
* Bio:SDK HTTP service: For biometric authentication
* HSM: For retrieving encryption/decryption keys.

## Build & run (for developers)
The project requires JDK 1.21.
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
[Configuration-Application](https://github.com/mosip/mosip-config/blob/develop/application-default.properties) defined here.
