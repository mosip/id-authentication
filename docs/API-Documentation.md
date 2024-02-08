This section details about the REST services in ID Authentication module.

* [Authentication Service](#authentication-service-public)
* [eKYC Service](#ekyc-service-public)
* [OTP Request Service](#otp-request-service-public)

# Authentication Service (Public)
This service details authentication methods that can be used by authentication partners to authenticate an individual. Below are various authentication types currently supported by this service:

* OTP based - OTP (Time based OTP)
* Demographic based - Name, Date of Birth, Age, Gender, Address, Full Address
* Biometric based - Fingerprint, Iris and Face

## Users of Authentication service
1. **MISP (MOSIP Infrastructure Service Provider)** - MISP's role is limited to infrastructure provisioning and acting as a gate keeper for all authentication requests sent to this service. The MISP is also responsible for the policy creation on the MOSIP servers so their partners will follow the set policy.
2. **Authentication Partners** - Authentication Partners register themselves with MOSIP, under a MISP. Authentication requests are captured by authentication partners and sent to MOSIP, via MISP.
3. **Partner-API-Key** - Associated against a policy.

## POST /idauthentication/v1/auth/
This request will authenticate an individual, based on provided authentication type(s).

### Resource URL
`https://{base_url}/idauthentication/v1/auth/{:MISP-LicenseKey}/{:Auth-Partner-ID}/{:Partner-Api-Key}`

### Resource Details
Resource Details | Description
-----------------|--------------
Response format | JSON
Requires Authentication | Yes

### Request Header Parameters
Name | Required | Description
-----|----------|-------------
Authorization | Y | For consent token
Signature | Y | For signature of the authentication request

### Request Path Parameters
Name | Required | Description
-----|----------|-------------
MISP-LicenseKey | Y | License key provided to the MISP
eKYC-Partner-ID | Y | Partner ID of the authentication partner sending the request
Partner-API-Key | Y | API Key associated to the partner and the policy

### Request Body Parameters
Name | Required | Description
-----|----------|-------------
id | Y | This represents the API ID. The value here should be "mosip.identity.auth".
version | Y | This represents the version of the API.
transactionID | Y | This represents the Transaction ID of the request.
requestTime | Y | This represents the time when request was created. Ex: "2019-02-15T10:01:57.086+05:30".
env | Y | This represents the environment. Allowed values are "Staging" , "Developer" , "Pre-Production" , "Production". These allowed values are configured by the property `mosip.ida.allowed.enviromemnts` in ID-Authentication Properties.
domainUri | Y | This represents the Unique URI per auth providers. This can be used to federate across multiple providers or countries or unions. The allowed values are configured by the property `mosip.ida.allowed.domain.uris` in ID-Authentication Properties.
individualId | Y | This represents the ID of resident (VID or UIN). Ex: "9830872690593682".
consentObtained | Y | If consent of residnet is obtained? Default value here is true.
thumbprint | Y | Thumbprint of public key certificate used for encryption of sessionKey. This will be used during key rotation
requestSessionKey | Y | Symmetric Key to be created, and then encrypt the generated Symmetric Key using 'MOSIP Public Key' shared to Partner, and then Base-64-URL encoded. Algorithm used for encryption can be  RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING.
requestHMAC | Y | SHA-256 hash of request block before encryption. Encryption is done using 'requestSessionKey' and then base64URL encoded. Algorithm used for encryption can be AES/GCM/PKCS5Padding.
request | Y | Request block to be used for authenticating the resident. This is encrypted using 'requestSessionKey' and then base64URL encoded. Algorithm used for encryption can be AES/GCM/PKCS5Padding.
request.otp | N | OTP used for authentication.
request.timestamp | N | Timestamp when request block was captured.
request.demographics | N | Demographic data of the residnet.
request.biometrics | N | Biometric data of an Individual which is sent in the response from the Capture API of SBI spec v1.0. Refer to the [SBI spec v1.0](Secure-Biometric-Interface-Specification.md#capture) specification for complete information.

### Request Body
```JSON
{
  "id": "mosip.identity.auth",
  "version": "v1",
  "requestTime": "2019-02-15T10:01:57.086+05:30",
  "env": "<Target environment>",
  "domainUri": "<URI of the authentication server>",
  "transactionID": "<Transaction ID of the authentication request>",
  "consentObtained": true,
  "individualId": "9830872690593682",
  "thumbprint": "<Thumbprint of the public key certficate used for enryption of sessionKey. This is necessary for key rotaion>",
  "requestSessionKey": "<Encrypted and Base64-URL-encoded session key>",
  "requestHMAC": "<SHA-256 of request block before encryption and then hash is encrypted using the requestSessionKey>",
  //Encrypted with session key and base-64-URL encoded
  "request": {
    "timestamp": "2019-02-15T10:01:56.086+05:30 - ISO format timestamp",
    "otp": "123456",
	"demographics": {
      "name": [
        {
          "language": "ara",
          "value": "ابراهيم بن علي"
        },
        {
          "language": "fra",
          "value": "Ibrahim Ibn Ali"
        }
      ],
      "gender": [
        {
          "language": "ara",
          "value": "الذكر"
        },
        {
          "language": "fra",
          "value": "mâle"
        }
      ],
      "age": "25",
      "dob": "25/11/1990",
      "fullAddress": [
        {
          "language": "ara",
          "value": "عنوان العينة سطر 1, عنوان العينة سطر 2"
        },
        {
          "language": "fra",
          "value": "exemple d'adresse ligne 1, exemple d'adresse ligne 2"
        }
      ]
    },
    //Same as the response from the Capture API of SBI v1.0. Refer to the [SBI v1.0 specification]() for complete information.
    "biometrics": [
      {
        "specVersion" : "<SBI specification version>",
        "data": "<JWS signature format of data containing encrypted biometrics and device details>",
        "hash": "<SHA-256 hash of (SHA-256 hash of previous data block in hex format + SHA-256 of current data block before encrypting in hex format) in hex format>", // For the first entry assume empty string as previous data block
        "sessionKey": "<Encrypted and base64-URL-encoded session key>",
        "thumbprint": "<SHA256 representation of thumbprint of the certificate that was used for encryption of session key>"
      },
      {
        "specVersion" : "<SBI specification version>",
        "data": "<JWS signature format of data containing encrypted biometrics and device details>",
        "hash": "<SHA-256 hash of (SHA-256 hash of previous data block in hex format + SHA-256 of current data block before encrypting in hex format) in hex format>",
        "sessionKey": "<Encrypted and base64-URL-encoded session key>",
        "thumbprint": "<SHA256 representation of thumbprint of the certificate that was used for encryption of session key>"
      }
    ]
  }
}
```

### Responses

#### Success Response
**Response Code : 200 (OK)**
```JSON
{
  "id": "mosip.identity.auth",
  "version": "v1",
  "responseTime": "2019-02-15T07:23:19.590+05:30",
  "transactionID": "<transaction_id used in request>",
  "response": {
    "authStatus": true,
    "authToken": "<authentication_token>"
  },
  "errors": null
}
```

#### Failed Response
**Response Code : 200 (OK)**
```JSON
{
  "id": "mosip.identity.auth",
  "version": "v1",
  "responseTime": "2019-02-15T07:23:19.590+05:30",
  "transactionID": "<transaction_id used in request>",
  "response": {
    "authStatus": false,
    "authToken": null
  },
  "errors": [
    {
      "errorCode": "IDA-MLC-002",
      "errorMessage": "Invalid UIN",
      "actionMessage": "Please retry with the correct UIN"
    }
  ]
}
```

### Failure Details
For details about the error codes view the section [common error codes and messages](#common-error-codes-and-messages).



# e-KYC Service (Public)
This service details authentication (eKYC auth) that can be used by authentication partners to authenticate an individual and send individual's KYC details as response. Below are various authentication types supported by e-KYC authentication:

1. OTP Authentication - OTP
2. Biometric Authentication - Fingerprint, IRIS and Face

## Users of KYC service
1. `MISP (MOSIP Infrastructure Service Provider)` - MISP's role is limited to infrastructure provisioning and acting as a gate keeper for all KYC requests sent to this service. The MISP is also responsible for policy creation on the MOSIP servers so their partners will follow the set policy.
2. `Partners` - *eKYC-Partners* register themselves with MOSIP, under a MISP. KYC requests are captured by eKYC-Partners and sent to MOSIP, via MISP.
3. `Partner-Api-Key` - Associated against a policy.

## POST /idauthentication/v1/kyc/
This request will provide KYC details of an individual, once the individual is successfully authenticated.

### Resource URL
`https://{base_url}/idauthentication/v1/kyc/:MISP-LicenseKey/:eKYC-Partner-ID/:Partner-Api-Key`

### Resource Details
Resource Details | Description
------------ | -------------
Response format | JSON
Requires Authentication | Yes

### Request Header Parameters
Name | Required | Description
-----|----------|-------------
Authorization | Y | For consent token
Signature | Y | For signature of the authentication request

### Request Path Parameters
Name | Required | Description
-----|----------|-------------
MISP-LicenseKey | Y | License key provided to the MISP
eKYC-Partner-ID | Y | Partner ID of the authentication partner sending the request
Partner-API-Key | Y | API Key associated to the partner and the policy

### Request Body Parameters
Name | Required | Description
-----|----------|-------------
id | Y | This represents the API ID. The value here should be "mosip.identity.kyc".
version | Y | This represents the version of the API.
transactionID | Y | This represents the Transaction ID of the request.
requestTime | Y | This represents the time when request was created. Ex: "2019-02-15T10:01:57.086+05:30".
env | Y | This represents the environment. Allowed values are "Staging" , "Developer" , "Pre-Production" , "Production". These allowed values are configured by the property `mosip.ida.allowed.enviromemnts` in ID-Authentication Properties.
domainUri | Y | This represents the Unique URI per auth providers. This can be used to federate across multiple providers or countries or unions. The allowed values are configured by the property `mosip.ida.allowed.domain.uris` in ID-Authentication Properties.
individualId | Y | This represents the ID of resident (VID or UIN). Ex: "9830872690593682".
consentObtained | Y | If consent of residnet is obtained? Default value here is true.
thumbprint | Y | Thumbprint of public key certificate used for encryption of sessionKey. This will be used during key rotation
requestSessionKey | Y | Symmetric Key to be created, and then encrypt the generated Symmetric Key using 'MOSIP Public Key' shared to Partner, and then Base-64-URL encoded. Algorithm used for encryption can be  RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING.
requestHMAC | Y | SHA-256 hash of request block before encryption. Encryption is done using 'requestSessionKey' and then base64URL encoded. Algorithm used for encryption can be AES/GCM/PKCS5Padding.
request | Y | Request block to be used for authenticating the resident. This is encrypted using 'requestSessionKey' and then base64URL encoded. Algorithm used for encryption can be AES/GCM/PKCS5Padding.
request.otp | N | OTP used for authentication.
request.timestamp | N | Timestamp when request block was captured.
request.demographics | N | Demographic data of the residnet.
request.biometrics | N | Biometric data of an Individual which is sent in the response from the Capture API of SBI spec v1.0. Refer to the [SBI spec v1.0](Secure-Biometric-Interface-Specification.md#capture) specification for complete information.

### Request Body
```JSON
{
  "id": "mosip.identity.kyc",
  "version": "v1",
  "requestTime": "2019-02-15T10:01:57.086+05:30",
  "env": "<Target environment>",
  "domainUri": "<URI of the authentication server>",
  "transactionID": "<Transaction ID of the authentication request>",
  "consentObtained": true,
  "individualId": "9830872690593682",
  "thumbprint": "<SHA256 representation of thumb-print of the MOSIP public key certificate used for encryption of sessionKey>",
  "requestSessionKey": "<Encrypted using MOSIP public key and base64-URL-encoded session key>",
  "requestHMAC": "<SHA-256 of request block before encryption and then hash is encrypted using the requestSessionKey>",
  //request section is first encrypted with the session key and then base64-URL-encoded
  "request": {
    "timestamp": "2019-02-15T10:01:56.086+05:30 - ISO format time-stamp",
    "otp": "123456",
    //biometric section is same as the response from Capture API mentioned in [SBIv1.0 specification]()
    "biometrics": [
      {
        "specVersion" : "<SBI specification version>",
        "data": "<JWS signature format of data containing encrypted biometrics and device details>",
        "hash": "<SHA-256 hash of (SHA-256 hash of previous data block in hex format + SHA-256 of current data block before encrypting in hex format) in hex format>", // For the first entry assume empty string as previous data block
        "sessionKey": "<Encrypted with MOSIP public key and base64-URL-encoded session key>",
        "thumbprint": "<SHA256 representation of thumb-print of the MOSIP public key that was used for encryption of session key>"
      },
      {
        "specVersion" : "<SBI specification version>",
        "data": "<JWS signature format of data containing encrypted biometrics and device details>",
        "hash": "<SHA-256 hash of (SHA-256 hash of previous data block in hex format + SHA-256 of current data block before encrypting in hex format) in hex format>",
        "sessionKey": "<Encrypted and base64-URL-encoded session key>",
        "thumbprint": "<SHA256 representation of thumb-print of the MOSIP public key that was used for encryption of session key>"
      }
    ]
  }
}
```

### Responses

#### Success Response
**Response Code : 200 (OK)**
```JSON
{
  "id": "mosip.identity.kyc",
  "version": "v1",
  "responseTime": "2019-02-15T07:23:19.590+05:30",
  "transactionID": "<Transaction ID received in request>",
  "response": {
    "kycStatus": true,
    "authToken": "<Authentication response token>",
    //Encrypted KYC info using Partner's public key and base64-URL-encoded
	"identity": {
      "name": [
        {
          "language": "ara",
          "value": "ابراهيم"
        },
        {
          "language": "fra",
          "value": "Ibrahim"
        }
      ],
      "dob": "25/11/1990",
      "gender": [
        {
          "language": "ara",
          "value": "الذكر"
        }
      ],
      "phoneNumber": "+212-5398-12345",
      "emailId": "sample@samplamail.com",
      "addressLine1": [
        {
          "language": "ara",
          "value": "عنوان العينة سطر 1"
        },
        {
          "language": "fra",
          "value": "exemple d'adresse ligne 1"
        }
      ],
      "addressLine2": [
        {
          "language": "ara",
          "value": "عنوان العينة سطر 2"
        },
        {
          "language": "fra",
          "value": "exemple d'adresse ligne 2"
        }
      ],
      "addressLine3": [
        {
          "language": "ara",
          "value": "عنوان العينة سطر 3"
        },
        {
          "language": "fra",
          "value": "exemple d'adresse ligne 3"
        }
      ]
    },
    "thumbprint": "<SHA256 representation of thumb-print of the Partner's public key used for encryption of identity block>",
	"sessionKey": "<Encrypted and base64-URL-encoded session key used to encrypt the identity json>"
  },
  "errors": null
}
```

#### Failed Response
**Response Code : 200 (OK)**
```JSON
{
  "id": "mosip.identity.kyc",
  "version": "v1",
  "responseTime": "2019-02-15T07:23:19.590+05:30",
  "transactionID": "<Transaction ID received in request>",
  "response": {
    "kycStatus": false,
    "authToken": null,
    "identity": null,
    "thumbprint": null,
    "sessionKey": null
  },
  "errors": [
    {
      "errorCode": "IDA-MLC-002",
      "errorMessage": "Invalid UIN",
      "actionMessage": "Please retry with the correct UIN"
    }
  ]
}
```

### Failure Details
For details about the error codes view the section [common error codes and message](#common-error-codes-and-messages).


# OTP Request Service (Public)
This service enables authentication partners to request for an OTP for an individual. The OTP will be send via message or email as requested to the individual. This OTP can then be used to authenticate the individual using authentication or eKYC service.

## Users of OTP Request service
1. `MISP (MOSIP Infrastructure Service Provider)` - MISP acts as a gate keeper for any OTP requests sent to this service. MISP is also responsible for the policy creation on the MOSIP servers so their partners will follow the set policy.
2. `Partners` - *Auth-Partners* and *eKYC-Partners* can send OTP Request to MOSIP on behalf of the individual for Authentication and eKYC requests respectively, via MISP.
3. `Partner-Api-Key` - Associated against a policy.

## POST /idauthentication/v1/otp/
This request will send an OTP to the individual whoes UIN/VID is entered.

### Resource URL
`https://{base_url}/idauthentication/v1/otp/:MISP-LicenseKey/:Partner-ID/:Partner-Api-Key`

### Resource details
Resource Details | Description
------------ | -------------
Response format | JSON
Requires Authentication | Yes

### Request Header Parameters
Name | Required | Description
-----|----------|-------------
Authorization | Y | This is for sending the consent token
Signature | Y | This is for sending the signature of the authentication request

### Request Body Parameters
Name | Required | Description
-----|----------|-------------
id | Y | This represents the API ID. The value here would be "mosip.identity.otp".
version | Y | This represents the API version.
transactionID | Y | Transaction ID of the request. Ex: "1234567890".
requestTime | Y | Time when request was captured. Ex:"2019-02-15T10:01:57.086+05:30"
individualId | Y | ID of resident (VID or UIN). Ex: "9830872690593682".
otpChannel | Y | OTP channel for sending OTP request. Allowed OTP Channels - EMAIL, PHONE.

### Request Body
```JSON
{
  "id": "mosip.identity.otp",
  "version": "v1",
  "requestTime": "2019-02-15T07:22:57.086+05:30",
  "transactionID": "<Transaction ID of the authentication request>",
  "individualId": "9830872690593682",
  "otpChannel": [
    "EMAIL",
    "PHONE"
  ]
}
```

### Responses

#### Success Response
**Response Code : 200 (OK)**
```JSON
{
  "id": "mosip.identity.otp",
  "version": "v1",
  "responseTime": "2019-02-15T07:23:19.590+05:30",
  "transactionID": "<Transaction ID of the authentication request>",
  "response": {
    "maskedMobile": "XXXXXXX123",
    "maskedEmail": "abXXXXXXXXXcd@xyz.com"
  },
  "errors": null
}
```

#### Failed Response
**Response Code : 200 (OK)**
```JSON
{
  "id": "mosip.identity.otp",
  "version": "v1",
  "responseTime": "2019-02-15T07:23:19.590+05:30",
  "transactionID": "<Transaction ID of the authentication request>",
  "response": null,
  "errors": [
    {
      "errorCode": "IDA-MLC-003",
      "errorMessage": "Invalid VID",
      "actionMessage": "Please retry with correct VID"
    }
  ]
}
```

### Failure Details
Error Code|Error Message|Description|Action Message
-----------|-------------|-----------|----------------
IDA-MLC-001|Request to be received at MOSIP within&lt;x&gt; seconds|Invalid Time stamp|Please send the request within &lt;x&gt; seconds
IDA-MLC-002|Invalid UIN|Invalid UIN|Please retry with the correct UIN.
IDA-MLC-003|UIN has been deactivated|UIN Deactivated|Your UIN status is not active.
IDA-MLC-004|Invalid VID|Invalid VID|Please retry with correct VID.
IDA-MLC-005|%s VID|Expired,Used,Revoked VID|Please regenerate VID and try again
IDA-MLC-006|Missing Input parameter- &lt;attribute&gt;  Example: Missing Input parameter- version|Missing Input parameter- attribute - all the mandatory attributes |
IDA-MLC-007|Request could not be processed. Please try again|Could not process request/Unknown error; Invalid Auth Request; Unable to encrypt eKYC response|
IDA-MLC-009|Invalid Input parameter- attribute  |Invalid Input parameter- attribute|
IDA-MLC-010|VID has been deactivated|VID corresponding to a deactivated UIN|
IDA-MLC-014|&lt;Notification Channel&gt; not registered. Individual has to register and try again|&lt;Notification Channel&gt; not Registered (Phone/e-mail/both)|Please register your &lt;Notification Channel&gt; and try again
IDA-MLC-015| Identity Type - &lt;Identity Type&gt; not configured for the country|ID Type (UIN/VID) not supported for a country|
IDA-MLC-017|Invalid UserID|Invalid UserID|
IDA-MLC-018|%s not available in database|UIN,VID, User ID not available in database|
IDA-MPA-004|MOSIP Public key expired. |MOSIP Public key expired|Please reinitiate the request with updated public key
IDA-MPA-005|OTP Request Usage not allowed as per policy|OTP Trigger Usage not allowed as per policy|
IDA-MPA-007|License key does not belong to a registered MISP|License key does not belong to a registered MISP/ License key invalid|
IDA-MPA-008|License key of MISP has expired|License key expired|
IDA-MPA-009|Partner is not registered|PartnerID Invalid|
IDA-MPA-010|MISP and Partner not mapped|MISP and Partner not |
IDA-MPA-011|License key of MISP is suspended|License key status of MISP is suspended|
IDA-MPA-012|Partner is deactivated|PartnerID is not active|
IDA-MPA-014|Partner is not assigned with any policy|PartnerID is not mapped to a policy|
IDA-MPA-017|License key of MISP is blocked|License key status of MISP is blocked|
IDA-OTA-001|In numerous OTP requests received|OTP Flooding error|
IDA-OTA-002|Could not generate/send OTP|Could not generate/send OTP|
IDA-OTA-006|UIN is locked for OTP generation. Please try again later |Try to generate OTP for a frozen Account|
IDA-OTA-008|OTP Notification Channel not provided.|No OTP Channel is provided in the input|
IDA-OTA-009|&lt;Notification Channel&gt; not configured for the country|&lt;Notification Channel&gt; not configured (Phone/e-mail/both)|
IDA-MLC-022|&lt;Partner ID/Individual ID/Device / Device Provider&gt; is blocked| |
IDA-MLC-030|Biometrics not captured within &lt;x&gt; seconds of previous biometrics|Please capture biometrics within &lt;x&gt; seconds of previous biometric capture|
IDA-MLC-031|DigitalId of Biometrics not captured within &lt;x&gt; seconds of previous biometrics|Please capture DigitalId of biometrics within &lt;x&gt; seconds of previous biometric capture
IDA-MPA-001|Digital signature verification failed for &lt;header or biometrics/data or biometrics/data/digitalId&gt;
IDA-MPA-020|Partner (Auth) Certificate not found in DB.
IDA-MPA-021|Partner (Auth) Certificate not matching with signature header certificate.
IDA-MPA-022|Partner (Auth) Certificate not found in Request signature header.
IDA-MPA-023|MISP Partner Policy not availble.
IDA-MPA-024|OIDC Client not availble.
IDA-MPA-025|Partner is unauthorised for KYC-Auth
IDA-MPA-026|Partner is unauthorised for KYC-Exchange
IDA-MPA-027|OIDC Client is deactivated
IDA-MPA-028|OIDC Client is not registered
IDA-MPA-029|&lt;DEMO/BIO/SPIN/OTP/PWD&gt; Authentication usage not allowed as per client AMR configuration
IDA-MLC-026|UIN length should be - &lt;x&gt;.
IDA-MLC-027|UIN should match checksum.
IDA-MLC-028|VID length should be - &lt;x&gt;.
IDA-MLC-029|VID should match checksum.
