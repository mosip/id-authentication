# Internal Authentication Service
## About
Internal Authentication Service is used by internal MOSIP modules such as Registration Processor, Registration Client and Resident to authenticate an individual's UIN/VID using one ore more authentication types.

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

## Endpoints:
* Internal Authentication - used by Internal MOSIP modules for authenticating an individual's UIN/VID

```
POST /idauthentication/v1/internal/auth
```

* Authentication Transaction History - Used by Resident service to retrieve the authentication transaction history for an individual (UIN and VIDs).

```
GET /authTransactions/individualId/{ID}
```

# Callbacks for Websub:
* Partner/MISP data update callback - to process websub message sent from Partner Management Service
* Master data update callback- to process websub message sent from Master data service


## Dependencies
* Kernal Notification Service - for sending notifications for Authentication Success/Failure
* Kernel Audit Service
* Keycloak serivce - To verify authentication token in incoming request, and to get authentication token for connecting to the above kernel services
* Websub - for getting events for Credential data/ IDentity data/ Partner data/ Master data updates.
* Bio-SDK HTTP service - for biometric authentication
* HSM - for retrieving encryption/decryption keys.


