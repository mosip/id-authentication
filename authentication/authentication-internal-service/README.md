# Internal Authentication Service

## About
Internal Authentication Service is used by internal MOSIP modules such as Registration Processor, Registration Client and Resident to authenticate an individual's UIN/VID using one ore more authentication types.

## Authentication types
Any combination of the supported [authentication types](https://docs.mosip.io/1.2.0/id-authentication#authentication-types) may be used.
  
## Modalities
* Refer [biometric modalities](https://docs.mosip.io/1.2.0/biometrics#modalities).
* Above authentication types can be allowed/disallowed/mandated by the [configuraion](../../docs/configuration.md#allowed-authentication-types) and the [Authentication/E-KYC Partner's Policy](../../docs/configuration.md).

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

