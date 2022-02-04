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

