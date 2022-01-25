# Authentication Service
## About
Authentication Service is used by Authentication/E-KYC Partners 
* to authenticate an individual's UIN/VID using one ore more authentication types.
* to request E-KYC for an individul's UIN/VID using one ore more authentication types.

## Authentication Types:
* Below are the authentication types supported in MOSIP, which can be used seperately or combined in any combination, 
  1. OTP Authentication 
  2. Demographic Authentication - Name, Date of Birth, Age, Gender, Address, Full Address, etc...
  3. Biometric Authentication
  
* Below are the modalities used in Biometrics authentication. These biometrics can be one or more segments of same modality or multiple modalities combined.
  1. Finger
  2. Iris
  3. Face

* Above authentication types can be allowed/disallowed/mandated by the [configuraion]() and the [Authentication/E-KYC Partner's Policy]().

## Partner/MISP validation:
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
POST /idauthentication/v1/ekyc/{MISP-LicenseKey}/{Auth-Partner-ID}/{Partner-Api-Key}
```

## Dependencies
* Kernal Notification Service - for sending notifications for Authentication Success/Failure
* Kernel Audit Service
* Keycloak serivce - to get authentication token for connecting to the above kernel services
* Websub - for getting events for Credential data/ IDentity data/ Partner data/ Master data updates.
* Bio-SDK HTTP service - for biometric authentication
* HSM - for retrieving encryption/decryption keys.


