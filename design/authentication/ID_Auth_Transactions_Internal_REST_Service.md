# Retrieve Authentication Transactions REST Service


**1. Background**

Retrieve Authentication Transactions REST Service can be used to retrieve the authenticate history of an Individual initiated by Partners. Individuals can provide their UIN/VID to access this service. Authentications done by MOSIP internal modules for any verifications will not be returned.

The authentication transaction history can be queried using this REST service for certain count of transactions which can be retrieved for given page-start number with page-count, where the entries per page is 10 in number.


 ***1.1.Target users -***  
Individual can use this service via Resident Services portal to get the authentication transaction history.


 ***1.2. Key requirements -***   
-	Resident Services portal will send Individual’s UIN/VID to get the authentication transactions history.
-	Check Individual’s UIN/VID for authenticity and validity
-	Respond with the list of auth transactions if the request is valid, otherwise, return error response.

 ***1.3. Key non-functional requirements -***   
-	Logging :
	-	Log each stage of authentication process
	-	Log all the exceptions along with error code and short error message
	-	As a security measure, Individual’s UIN or PI/PA should not be logged
-	Audit :
	-	Audit all transaction details during authentication process in database
	-	Individual’s UIN or PI/PA details should not be audited
	-	Audit any invalid UIN or VID incidents
-	Exception :
	-	Any failure in authentication/authorization of Partner and validation of UIN and VID needs to be handled with appropriate error code and message in Auth Response
	-	Any error in Individual authentication also should be handled with appropriate error code and message in Auth Response 
-	Security :


**2. Solution**   
ID Authentication Transactions REST Service addresses the above requirements as explained below. 

1.	Resident Services portal to construct and URL as below and send.

GET `/idauthentication/v1/internal/authTransactions/individualIdType/:IDType/individualId/:ID?pageStart=1&pageFetch=10
`
     
Sample Request URL: 
`http://mosip.io/idauthentication/v1/internal/authTransactions/individualIdType/UIN/individualId/9172985031?pageStart=1&pageFetch=4`

2.	Authenticate the request based on the authentication token. Authroize the request based on LDAP roles.
3.	Integrate with kernel UIN Validator and VID Validator to check UIN/VID for validity. Validate UIN/VID for authenticity and active status using ID Repo service.
7.	Fetch the Auth Transactions from database for the given ID and IDType for the given page start and count.
8.	Respond with the auth transaction details as below - 
```JSON
{

	"id": "mosip.identity.auth.transactions.read",
	"errors": [],
	"response": {
		"authTransactions": [{
				"transactionID": "1234567890",
				"requestdatetime": "2019-07-10T07:28:59.383",
				"authtypeCode": "FINGERPRINT-AUTH",
				"statusCode": "Y",
				"statusComment": "Finger Authentication Success",
				"referenceIdType": "UIN",
				"entityName": ""
			},
			{
				"transactionID": "1234567891",
				"requestdatetime": "2019-07-11T07:29:59.383",
				"authtypeCode": "OTP-REQUEST",
				"statusCode": "F",
				"statusComment": "OTP Authentication Failed",
				"referenceIdType": "UIN",
				"entityName": ""
			}
		]
	},
	"responseTime": "2019-07-11T07:30:59.383",
	"version": "1.0"
}
```

**2.1. Class Diagram:**   
The below class diagram shows relationship between all the classes which are required for Authentication Transactions service.

![Auth Transactions Class Diagram](_images/ID_Auth_Transactions_Class_Diagram.PNG)

**2.2. Sequence Diagram:**   
![Auth Transactions Sequence Diagram](_images/ID_Auth_Transactions_Sequence_Diagram.PNG)

**3. Proxy Implementations -**   
Below are the proxy implementations used in ID-Authentication:
- ***Digital Signature in request*** - Any digital signature added in the Auth request is not currently validated .
- ***Digital Signature in response*** - The Auth response is digitally signed using the MOSIP private key with reference ID **SIGN** and application ID **KERNEL**, and public key of the same should be used to verify the signature. 
