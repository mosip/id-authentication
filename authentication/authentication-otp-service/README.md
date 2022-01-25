# OTP Request Service
## About
OTP Request is used by Authentication/E-KYC Partners to generate OTP for an individual's UIN/VID. The generated OTP is stored in IDA DB for validation during [OTP Authentication]() request in [Authentication Service]()

## Partner/MISP validation:
* Below partner/MISP data are validated before processing the authentication request:
  1. MISP License Key
  2. Partner ID
  3. Partner API Key

## Endpoints:
* Authentication:

```
POST /idauthentication/v1/otp/{MISP-LicenseKey}/{Auth-Partner-ID}/{Partner-Api-Key}
```

# Callbacks for Websub:
* Partner/MISP data update callback - to process websub message sent from Partner Management Service
* Master data update callback- to process websub message sent from Master data service

## Dependencies
* Kernel OTP Manager service - to generate OTP.
* Kernal Notification Service - for sending notifications for Authentication Success/Failure
* Kernel Audit Service
* Keycloak serivce - to get authentication token for connecting to the above kernel services
* Websub - for getting events for Credential data/ IDentity data/ Partner data/ Master data updates.
* HSM - for retrieving encryption/decryption keys.


