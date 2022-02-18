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

