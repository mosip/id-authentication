# Configure the Esignet MISP Partner
Below are the steps to create the Esignet MISP Partner as a standard steps in DevOps after Esignet  deployment.

## Preparations:
1. Have a user created in keycloak with below roles needed for the Authorization token needed in the API requests:
	i. ZONAL_ADMIN, 
	ii. PARTNER_ADMIN, 
	iii. POLICY_MANAGER, 
	iv. MISP_PARTNER, 
	v. PMS_ADMIN


2. Authenticating user to take the token and use it in all APIs invoked in further steps:
* Swagger url - https://api-internal.dev.mosip.net/v1/authmanager/swagger-ui/index.html?configUrl=/v1/authmanager/v3/api-docs/swagger-config#/authmanager/getAllAuthTokens

* request body:
````
{
  "id": "string",
  "version": "string",
  "requesttime": "2023-01-04T11:49:29.007Z",
  "metadata": {},
  "request": {
    "userName": "******",
    "password": "**********",
    "appId": "partner",
    "clientId": "mosip-pms-client",
    "clientSecret": "************"
  }
}
````

Steps:
## I. Create MISP Partner for Esignet:

### 1. Creating a policy group for Esignet-MISP-partner :
* Swagger URL - https://api-internal.dev.mosip.net/v1/policymanager/swagger-ui/index.html?configUrl=/v1/policymanager/v3/api-docs/swagger-config#/policy-management-controller/definePolicyGroup


* request body:
````
{
  "id": "string",
  "version": "string",
  "requesttime": "2023-01-04T11:52:19.734Z",
  "metadata": {},
  "request": {
    "name": "Esignet-partner-policy-group",
    "desc": "Esignet partner policy group"
  }
}
````

* Make note of the policyGroupId from the response.

### 2. Creating a policy for Esignet-MISP-partner :
* Swagger URL - https://api-internal.dev1.mosip.net/v1/policymanager/swagger-ui/index.html?configUrl=/v1/policymanager/v3/api-docs/swagger-config#/policy-management-controller/definePolicy

request body:
````
{
  "id": "string",
  "metadata": {},
  "request": {
    "desc": "Esignet-partner-policy",
    "name": "esignet-partner-policy",
    "version" : "1.0",
    "policies": {
        "allowAuthRequestDelegation": true,
        "allowKycRequestDelegation":true,
        "trustBindedAuthVerificationToken": true,
        "allowKeyBindingDelegation":true
        },
    "policyGroupName": "Esignet-partner-policy-group",
    "policyType": "MISP"
  },
  "requesttime": "2023-01-04T11:52:19.734Z",
  "version": "LTS"
}
````

### 3. Publishing policy and policy group:
* Swagger URL - https://api-internal.dev1.mosip.net/v1/policymanager/swagger-ui/index.html?configUrl=/v1/policymanager/v3/api-docs/swagger-config#/policy-management-controller/publishPolicy
Path params: 
	* `policyId` - esignet-partner-policy
	* `policyGroupId` - from previous response

### 4. Esignet MISP Partner self registration:

* Swagger url: https://api-internal.dev.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/partner-service-controller/partnerSelfRegistration

* Request Body:
````
{
  "id": "string",
  "version": "string",
  "requesttime": "2023-01-04T12:01:37.001Z",
  "metadata": {},
  "request": {
    "partnerId": "Esignet-MISP-partner",
    "policyGroup": "Esignet-partner-policy-group",
    "organizationName": "IITB",
    "address": "bangalore",
    "contactNumber": "6483839992",
    "emailId": "Esignet-partner-group1@mosip.com",
    "partnerType": "MISP_Partner",
    "langCode": "eng"
  }
}
````

### 5. Upload Certificates for the partner :

i. Get CA certificate from esignet with below parameters:
* make sure to use Bearer Token as authorization type and use the token provided from step-2
* URL: {{external-url}}/v1/esignet/system-info/certificate?applicationId=ROOT


ii. Uploaded it as CA certificate: 
*Swagger URL - https://api-internal.dev.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/partner-service-controller/uploadCACertificate

* Request Body (Example only):
````
{
  "id": "string",
  "version": "string",
  "requesttime": "2023-01-04T12:05:36.167Z",
  "metadata": {},
  "request": {
    "certificateData": "-----BEGIN CERTIFICATE-----\nMIIDpzCCAo+gAwIBAgII6l7mtDAeV24wDQYJKoZIhvcNAQELBQAwcDELMAkGA1UE\nBhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDTALBg77Q0So1xoHL18aNvQTvB2pjhW9BOFpXDrrF/nzI2sd\nye/pypM97dktpncIm9v/vTenyFOJRrtU9DzkBkuI+TfjQDPHoYGiLtT8OaFwZ5OD\nf6XVCptIm0IAeoqbEA9n+ovQ8s8iuKRUyYOnOHNqMffYBBCfXKOJwtRvrzHykvLI\n31RWye2NllNrT6cpz8f7v8QSZlIpcg8J0n62hao+NLbjWvqLYS9DVoqKjM/+gHOK\nqrxDD9brR8Tbi8DKm+wGk6yK/ebW2CtrK6euV0zCD7Qu2mZ1wOttyAbID0bXUV+o\nyoPx1FYS4jHjATEzypS9IftVYvU53W/TUMgt\n-----END CERTIFICATE-----\n",
    "partnerDomain": "MISP"
  }
}
````
iii. Get SUBCA certificate from esignet with below parameters:
* make sure to use Bearer Token as authorization type and use the token provided from step-2
* URL: {{external-url}}/v1/esignet/system-info/certificate?applicationId=OIDC_SERVICE


iv. Uploaded it as CA certificate: 
*Swagger URL - https://api-internal.dev.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/partner-service-controller/uploadCACertificate

* Request Body (Example only):
````
{
  "id": "string",
  "version": "string",
  "requesttime": "2023-01-04T12:05:36.167Z",
  "metadata": {},
  "request": {
    "certificateData": "-----BEGIN CERTIFICATE-----\nMIIDpzCCAo+gAwIBAgII6l7mtDAeV24wDQYJKoZIhvcNAQELBQAwcDELMAkGA1UE\nBhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDTALBg77Q0So1xoHL18aNvQTvB2pjhW9BOFpXDrrF/nzI2sd\nye/pypM97dktpncIm9v/vTenyFOJRrtU9DzkBkuI+TfjQDPHoYGiLtT8OaFwZ5OD\nf6XVCptIm0IAeoqbEA9n+ovQ8s8iuKRUyYOnOHNqMffYBBCfXKOJwtRvrzHykvLI\n31RWye2NllNrT6cpz8f7v8QSZlIpcg8J0n62hao+NLbjWvqLYS9DVoqKjM/+gHOK\nqrxDD9brR8Tbi8DKm+wGk6yK/ebW2CtrK6euV0zCD7Qu2mZ1wOttyAbID0bXUV+o\nyoPx1FYS4jHjATEzypS9IftVYvU53W/TUMgt\n-----END CERTIFICATE-----\n",
    "partnerDomain": "MISP"
  }
}
````

v. Get Partner certificate from esignet with below parameters:
* make sure to use Bearer Token as authorization type and use the token provided from step-2
* URL: {{external-url}}/v1/esignet/system-info/certificate?applicationId=OIDC_PARTNER


vi. Uploaded it as partner certificate: 
* make sure to copy the certificate from the response of this request, will need the same in upcoming steps.
*Swagger URL - https://api-internal.dev.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/partner-service-controller/uploadPartnerCertificate

* Request Body (Example only):
````
{
  "id": "string",
  "metadata": {},
  "request": {
    "certificateData":"-----BEGIN CERTIFICATE-----\nMIIDpzCCAo+gAwIBAgII6l7mtDAeV24wDQYJKoZIhvcNAQELBQAwcDELMAkGA1UE\nBhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDTALBg77Q0So1xoHL18aNvQTvB2pjhW9BOFpXDrrF/nzI2sd\nye/pypM97dktpncIm9v/vTenyFOJRrtU9DzkBkuI+TfjQDPHoYGiLtT8OaFwZ5OD\nf6XVCptIm0IAeoqbEA9n+ovQ8s8iuKRUyYOnOHNqMffYBBCfXKOJwtRvrzHykvLI\n31RWye2NllNrT6cpz8f7v8QSZlIpcg8J0n62hao+NLbjWvqLYS9DVoqKjM/+gHOK\nqrxDD9brR8Tbi8DKm+wGk6yK/ebW2CtrK6euV0zCD7Qu2mZ1wOttyAbID0bXUV+o\nyoPx1FYS4jHjATEzypS9IftVYvU53W/TUMgt\n-----END CERTIFICATE-----\n",
    "partnerDomain": "MISP",
    "partnerId": "Esignet-MISP-partner"
 
  },
  "requesttime": "2023-01-04T12:05:36.167Z",
  "version": "string"
}
````
### 6. Activate MISP partner :
i. Use Below Swagger URL to activate the partner:
* Swagger URL: https://api-internal.dev1.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/partner-management-controller/activateDeactivatePartner

* Request Body (Example only):
````
{
  "id": "string",
  "metadata": null,
  "requesttime": "2023-01-04T12:05:36.167Z",
  "request": {
    "status": "ACTIVE"
 
  },
  
  "version": "string"
}
````
### 7. Upload the signed certificate: 
*URL - https://api.dev.mosip.net/v1/esignet/system-info/uploadCertificate
* make sure to use Bearer Token as authorization type and use the token provided from step-2
* Request Body (Example only):
````
{
  "request": {
    "certificateData": {{certificate from step-vi of 6th step}},
    "applicationId": "OIDC_PARTNER",
    "referenceId" : ""
  },
  "requestTime": "2023-01-04T12:05:36.167Z"

}
````

### 7. Create policy Mapping request:
* Swagger url: 
https://api-internal.dev2.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/partner-service-controller/mapPolicyToPartner
* Path param: 
	* `partnerId` : Esignet-MISP-partner
* Request Body:
````
{
  "id": "string",
  "version": "string",
  "requesttime": "2023-01-04T13:18:11.206Z",
  "metadata": {},
  "request": {
    "policyName": "esignet-partner-policy",
    "useCaseDescription": "esignet-partner-policy-mapping"
  }
}
````

* output:
````
{
  "id": "string",
  "version": "string",
  "responsetime": "2023-01-04T12:10:57.353Z",
  "metadata": null,
  "response": {
    "mappingkey": "******",
    "message": "Policy mapping request submitted successfully."
  },
  "errors": []
}
````
* Make note of the `mappingKey`.

### 8. Approve policy mapping:
* Swagger url - https://api-internal.dev.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/partner-management-controller/approveRejectPolicyMappings
* Path param: 
	* `mappingKey` : as noted from above request


* Request Body:
````
{
  "id": "string",
  "version": "string",
  "requesttime": "2023-01-04T12:13:15.114Z",
  "metadata": {},
  "request": {
    "status": "APPROVED"
  }
}
````

### 9. Create MISP license key 
* Swagger url - https://api-internal.dev1.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/misp-license-controller/generateLicense

* Request Body:
````
{
  "id": "string",
  "version": "string",
  "requesttime": "2023-09-08T10:55:16.563Z",
  "metadata": {},
  "request": {
    "providerId": "{{same as partner id i.e Esignet-MISP-partner }}"
  }
}
````

* Make note of the license key in response of this request as it is vital to working of Esignet.


### B. Create the Esignet demo OIDC client  :
## Preparations:
1. Have a user created in keycloak with below roles needed for the Authorization token needed in the API requests:
	i. ZONAL_ADMIN, 
	ii. PARTNER_ADMIN, 
	iii. POLICY_MANAGER, 
	iv. MISP_PARTNER, 
	v. PMS_ADMIN


2. Authenticating user to take the token and use it in all APIs invoked in further steps:
* Swagger url - https://api-internal.dev.mosip.net/v1/authmanager/swagger-ui/index.html?configUrl=/v1/authmanager/v3/api-docs/swagger-config#/authmanager/getAllAuthTokens

* request body:
````
{
  {
  "id": "string",
  "version": "string",
  "requesttime": "2023-01-04T11:49:29.007Z",
  "metadata": {},
  "request": {
    "userName": "******",
    "password": "**********",
    "appId": "partner",
    "clientId": "mosip-pms-client",
    "clientSecret": "************"
  }
}
````

Steps:
## I. Create Auth Partner for Esignet demo OIDC Client:

### 1. Creating a policy group for Esignet Auth Partner:

* Swagger URL - https://api-internal.dev.mosip.net/v1/policymanager/swagger-ui/index.html?configUrl=/v1/policymanager/v3/api-docs/swagger-config#/policy-management-controller/definePolicyGroup

* make note of the policygroupID from the response of this request
* request body:
````
{
  "id": "string",
  "version": "string",
  "requesttime": "2023-01-04T11:52:19.734Z",
  "metadata": {},
  "request": {
    "name": "Esignet-auth-partner-policyGroup",
    "desc": "Esignet auth partner PolicyGroup"
  }
}
````

* Make note of the policyGroupId from the response.

### 2. Creating a policy for Esignet Auth Partner:
* Swagger URL - https://api-internal.dev.mosip.net/v1/policymanager/swagger-ui/index.html?configUrl=/v1/policymanager/v3/api-docs/swagger-config#/policy-management-controller/definePolicy
 

request body:
````
{
	"id": "",
	"metadata": null,
	"request": {
		"policyId": "Esignet-auth-partner-policy",
		"name": "Esignet-auth-partner-policy",
		"desc": "Esignet auth partner policy",
		"policies": {"authTokenType":"policy","allowedKycAttributes":[{"attributeName":"fullName"},{"attributeName":"gender"},{"attributeName":"phone"},{"attributeName":"email"},{"attributeName":"dateOfBirth"},{"attributeName":"city"},{"attributeName":"face"},{"attributeName":"addressLine1"},{"attributeName":"individual_id"}],"allowedAuthTypes":[{"authSubType":"IRIS","authType":"bio","mandatory":false},{"authSubType":"FINGER","authType":"bio","mandatory":false},{"authSubType":"","authType":"otp","mandatory":false},{"authSubType":"FACE","authType":"bio","mandatory":false},{"authSubType":"","authType":"otp-request","mandatory":false},{"authSubType":"","authType":"kyc","mandatory":false},{"authSubType":"","authType":"demo","mandatory":false},{"authSubType":"","authType":"kycauth","mandatory":false},{"authSubType":"","authType":"kycexchange","mandatory":false},{"authSubType":"","authType":"keybinding","mandatory":false},{"authSubType":"","authType":"kbt","mandatory":false},{"authSubType":"","authType":"wla","mandatory":false}]},
		"policyGroupName": "Esignet-auth-partner-policyGroup",
		"policyType": "Auth",
		"version": "1.1"
	},
	"version": "1.0",
	"requesttime": "2022-12-29T13:12:28.479Z"
}
````

### 3. Publishing policy:
* Swagger URL - https://api-internal.dev.mosip.net/v1/policymanager/swagger-ui/index.html?configUrl=/v1/policymanager/v3/api-docs/swagger-config#/policy-management-controller/publishPolicy
Path params: 
	* `policyId` - Esignet-auth-partner-policy
	* `policyGroupId` - from previous response

### 4. Esignet Auth Partner self registration:

* Swagger url: https://api-internal.dev.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/partner-service-controller/partnerSelfRegistration

* Request Body:
````
{
  "id": "string",
  "version": "string",
  "requesttime": "2023-01-04T12:01:37.001Z",
  "metadata": {},
  "request": {
    "partnerId": "Esignet-auth-partner",
    "policyGroup": "Esignet auth partner PolicyGroup",
    "organizationName": "IITB",
    "address": "bangalore",
    "contactNumber": "6483839992",
    "emailId": "Esignet-auth-partner-partner1@mosip.com",
    "partnerType": "Auth_Partner",
    "langCode": "eng"
  }
}
````

### 5. Upload Certificates for the partner :

i. Get CA, SUBCA, PARTNER certificateData for the Auth_Partner


ii. Uploaded CA certificate: 
*Swagger URL - https://api-internal.dev.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/partner-service-controller/uploadCACertificate

* Request Body (Example only):
````
{
  "id": "string",
  "version": "string",
  "requesttime": "2023-01-04T12:05:36.167Z",
  "metadata": {},
  "request": {
    "certificateData": "-----BEGIN CERTIFICATE-----\nMIIDpzCCAo+gAwIBAgII6l7mtDAeV24wDQYJKoZIhvcNAQELBQAwcDELMAkGA1UE\nBhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDTALBg77Q0So1xoHL18aNvQTvB2pjhW9BOFpXDrrF/nzI2sd\nye/pypM97dktpncIm9v/vTenyFOJRrtU9DzkBkuI+TfjQDPHoYGiLtT8OaFwZ5OD\nf6XVCptIm0IAeoqbEA9n+ovQ8s8iuKRUyYOnOHNqMffYBBCfXKOJwtRvrzHykvLI\n31RWye2NllNrT6cpz8f7v8QSZlIpcg8J0n62hao+NLbjWvqLYS9DVoqKjM/+gHOK\nqrxDD9brR8Tbi8DKm+wGk6yK/ebW2CtrK6euV0zCD7Qu2mZ1wOttyAbID0bXUV+o\nyoPx1FYS4jHjATEzypS9IftVYvU53W/TUMgt\n-----END CERTIFICATE-----\n",
    "partnerDomain": "Auth"
  }
}
````

iv. Upload SUBCA certificate: 
*Swagger URL - https://api-internal.dev.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/partner-service-controller/uploadCACertificate

* Request Body (Example only):
````
{
  "id": "string",
  "version": "string",
  "requesttime": "2023-01-04T12:05:36.167Z",
  "metadata": {},
  "request": {
    "certificateData": "-----BEGIN CERTIFICATE-----\nMIIDpzCCAo+gAwIBAgII6l7mtDAeV24wDQYJKoZIhvcNAQELBQAwcDELMAkGA1UE\nBhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDTALBg77Q0So1xoHL18aNvQTvB2pjhW9BOFpXDrrF/nzI2sd\nye/pypM97dktpncIm9v/vTenyFOJRrtU9DzkBkuI+TfjQDPHoYGiLtT8OaFwZ5OD\nf6XVCptIm0IAeoqbEA9n+ovQ8s8iuKRUyYOnOHNqMffYBBCfXKOJwtRvrzHykvLI\n31RWye2NllNrT6cpz8f7v8QSZlIpcg8J0n62hao+NLbjWvqLYS9DVoqKjM/+gHOK\nqrxDD9brR8Tbi8DKm+wGk6yK/ebW2CtrK6euV0zCD7Qu2mZ1wOttyAbID0bXUV+o\nyoPx1FYS4jHjATEzypS9IftVYvU53W/TUMgt\n-----END CERTIFICATE-----\n",
    "partnerDomain": "Auth"
  }
}
````

vi. Upload partner certificate: 
* make sure to copy the certificate from the response of this request, will need the same in upcoming steps.
*Swagger URL - https://api-internal.dev.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/partner-service-controller/uploadPartnerCertificate

* Request Body (Example only):
````
{
  "id": "string",
  "metadata": {},
  "request": {
    "certificateData":"-----BEGIN CERTIFICATE-----\nMIIDpzCCAo+gAwIBAgII6l7mtDAeV24wDQYJKoZIhvcNAQELBQAwcDELMAkGA1UE\nBhMCSU4xCzAJBgNVBAgMAktBMRIwEAYDVQQHDAlCQU5HQUxPUkUxDTALBg77Q0So1xoHL18aNvQTvB2pjhW9BOFpXDrrF/nzI2sd\nye/pypM97dktpncIm9v/vTenyFOJRrtU9DzkBkuI+TfjQDPHoYGiLtT8OaFwZ5OD\nf6XVCptIm0IAeoqbEA9n+ovQ8s8iuKRUyYOnOHNqMffYBBCfXKOJwtRvrzHykvLI\n31RWye2NllNrT6cpz8f7v8QSZlIpcg8J0n62hao+NLbjWvqLYS9DVoqKjM/+gHOK\nqrxDD9brR8Tbi8DKm+wGk6yK/ebW2CtrK6euV0zCD7Qu2mZ1wOttyAbID0bXUV+o\nyoPx1FYS4jHjATEzypS9IftVYvU53W/TUMgt\n-----END CERTIFICATE-----\n",
    "partnerDomain": "Auth",
    "partnerId": "Esignet-auth-partner"
 
  },
  "requesttime": "2023-01-04T12:05:36.167Z",
  "version": "string"
}
````
### 6. Activate Auth partner :
i. Use Below Swagger URL to activate the partner:
* Swagger URL: https://api-internal.dev1.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/partner-management-controller/activateDeactivatePartner

* Request Body (Example only):
````
{
  "id": "string",
  "metadata": null,
  "requesttime": "2023-01-04T12:05:36.167Z",
  "request": {
    "status": "ACTIVE"
 
  },
  
  "version": "string"
}
````
### 7. Create policy Mapping request:
* Swagger url: 
https://api-internal.dev.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/partner-service-controller/mapPolicyToPartner
* Path param: 
	* `partnerId` : Esignet-auth-partner
* Request Body:
````
{
  "id": "string",
  "version": "string",
  "requesttime": "2023-01-04T13:18:11.206Z",
  "metadata": {},
  "request": {
    "policyName": "Esignet-auth-partner-policy",
    "useCaseDescription": "Esignet-auth-partner-policy"
  }
}
````

* output:
````
{
  "id": "string",
  "version": "string",
  "responsetime": "2023-01-04T12:10:57.353Z",
  "metadata": null,
  "response": {
    "mappingkey": "834602",
    "message": "Policy mapping request submitted successfully."
  },
  "errors": []
}
````
Make note of the `mappingKey`.

### 8. Approve policy mapping:
* Swagger url - https://api-internal.dev.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/partner-management-controller/approveRejectPolicyMappings
* Path param: 
	* `mappingKey` : as noted from above request

*Note: This mapping key will be returned as an output from policy mapping request.*

* Request Body:
````
{
  "id": "string",
  "version": "string",
  "requesttime": "2023-01-04T12:13:15.114Z",
  "metadata": {},
  "request": {
    "status": "APPROVED"
  }
}
````

### 9. Create the OIDC client in PMS:
* Swagger url: https://api-internal.dev.mosip.net/v1/partnermanager/swagger-ui/index.html?configUrl=/v1/partnermanager/v3/api-docs/swagger-config#/client-management-controller/createClient

In the request body make sure to replace below attributes:
1. `publicKey` - the JWKS public key JSON from the partner or for demo purposes from jwks.org
2. `logoUri` -  for the OIDC-UI
3. `redirectUris` - For redirecting from Esignet page. 

* Request Body (Example only):
````
{
	"id": "string",
	"version": "string",
	"requesttime": "2023-01-04T12:15:14.854Z",
	"metadata": {},
	"request": {
		"name": "Demo-oidc-client",
		"policyName": "Esignet-auth-partner-policy",
		"publicKey": {
			"kty": "RSA",
			"e": "AQAB",
			"use": "sig",
			"kid": "RbW-bNIihYlr2GWVyqIgshHHFxe2pIkkvdTp_Iedmic",
			"alg": "RS256",
			"n": "AMROKZuU_9xeybzmZdRHLCpJqh1ThfHtEf_Vbbm11TpfDno0-eoYga-y8YuLBTW8jKIffhB8UdScnkmtG0m71qNMvyjNa01IcX_C2yGZCmMZt6o57R6Pyc4ygIQojnb-_iumbiJBlwdm4alyCAxbZes4EaodFjWZakmdEGt7cezKF3RCaeateAPQ8slWq6RREn3BmKdE1VMOmvNNQTbbSh5wJzSwlgSbaNHuhhsjci98bkbnvssxs5ad9-UuT4T4_0yi9nQFQ530kXRW_IAhavzY-g_RjLpRwakUFxPsb8BL2Y6TbGk0WOm9kN-Rir1ef7woK4pVMX5_SbOT785Iczs="
		},
		"authPartnerId": "Esignet-auth-partner",
		"logoUri": "https://healthservices.dev.mosip.net/logo.png",
		"redirectUris": [
			"ttps://healthservices.dev.mosip.net/userprofile**"
		],
		"grantTypes": [
			"authorization_code"
		],
		"clientAuthMethods": [
			"private_key_jwt"
		]
	}
}
````
 * Make sure to note the ClientID from the response of this request.