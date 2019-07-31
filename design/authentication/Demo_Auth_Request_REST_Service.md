# Demographic Auth REST Service


**1. Background**


Demographic Auth REST service can be used to authenticate an Individual using below types of Demographics - 
1.	Name
2.	Address
3.	Date of birth
4.  Gender
5.  Phone
6.  Email-ID


**1.1.Target users**  
Partner can use Auth service to authenticate an Individual by using one or more types of authentication supported by MOSIP and retrieve Auth details as a response.


 **1.2. Key requirements**   
-	Partner can authenticate an Individual using one or more authentication types
-	Partner will send Individual's UIN/VID to enable authentication of Individual
-	Partner will send Partner ID and MISP License Key to authenticate and authorize a Partner to authenticate an Individual
-	Check Individual's UIN/VID for authenticity and validity
-	Validate demographic details of the Individual against the one stored in database
-	Inform authentication status (success/failure) to the Individual in the form of message and/or email


**1.3. Key non-functional requirements**   
-	Log :
	-	Log each stage of authentication process
	-	Log all the exceptions along with error code and short error message
	-	As a security measure, Individual's UIN or PI/PA should not be logged
-	Audit :
	-	Audit all transaction details during authentication process in database
	-	Individual's UIN should not be audited
	-	Audit any invalid UIN or VID incidents
-	Exception :
	-	Any failure in authentication/authorization of Partner and validation of UIN and VID needs to be handled with appropriate error code and message in Auth response
	-	Any error in Individual authentication also should be handled with appropriate error code and message in Auth Response 
-	Security
	-	Auth details of an individual is a sensitive information, hence should be encrypted before sending to Partner
	-	Auth Request contains sensitive identity information of an Individual. This information should be encrypted by Partner before sending to IDA. On receiving this request, Partner should decrypt identity element before validating Individual's details for authentication purpose 


**2. Solution**   
1.	Partner needs to construct a POST request with below details and send to Request URL `/idauthentication/v1/identity/auth/` - [Sample Request](https://github.com/mosip/mosip-docs/wiki/ID-Authentication-APIs#post-idauthenticationv1auth)
2.	Authenticate and Authorize Partner and MISP using their Policy and LicenseKey respectively
3.	Validate 'requestTime' for incoming Auth Requests for valid format and timestamp < 30 minutes (configurable value) from current time
4.	Integrate with Kernel UIN Validator and VID Validator to check UIN/VID for validity. 
5.	Once the above validations are successful, Auth request is then validated based on biometric - Name, Address, Date of birth,Gender, Phone and Email-ID - authentications present in input request.  
6.	For this authentication retrieve Identity details of the Individual based on UIN from ID Repository. Validate UIN/VID for authenticity.
7.	Retrieve mode of communication with Individual using admin config to send authentication success/failure information
8.	When the Individual is successfully authenticated based on one or more of the above authentication types, a sms/email notification is sent to them using Kernel's SmsNotifier and EmailNotifier to their stored phone/email respectively.
9.	Respond to Partner with below success Auth response - [Sample Response](https://github.com/mosip/mosip-docs/wiki/ID-Authentication-APIs#success-response)


**2.1. Class Diagram:**   
The below class diagram shows relationship between all the classes which are required for Bio authentication service.

![Demo Auth Class Diagram](_images/Demo_Auth_Class_Diagram.PNG)

**2.2. Sequence Diagram:**   
![Demo Auth Sequence Diagram](_images/Demo_Auth_Sequence_Diagram.PNG)

**2.3. Demographic Data Normalization** 
<br>
For authentication based on name/address values, normalization will be performed on both name/address from the request and their respective values in the database. The normalization rules are made configurable for multiple languages. 

Below is the description of the demographic data normalization in configuration.
1. Demograpic Name/Address data Normalization uses Java Regular Expressions and their replacement values from the configurations.
2. The `ida.norm.sep` property defines the separator to be used in the configuration, and its default value is set to '='. This configuration should be defined befor all other demographic data normalization configuration.
For example, `ida.norm.sep==`
3. The format for the configuration is: 

````
ida.demo.<name/address/common>.normalization.regex.<languageCode/any>[<sequential index starting from 0>]=<reqular expression>${ida.norm.sep}<replacement string>
`````

For example,
````
ida.demo.address.normalization.regex.eng[0]=[CcSsDdWwHh]/[Oo]
ida.demo.address.normalization.regex.eng[1]=[aA][pP][aA][rR][tT][mM][eE][nN][tT]${ida.norm.sep}apt 
ida.demo.address.normalization.regex.eng[2]=[sS][tT][rR][eE][eE][tT]${ida.norm.sep}st

ida.demo.name.normalization.regex.eng[0]=(M|m)(rs?)(.)
ida.demo.name.normalization.regex.eng[1]=(D|d)(r)(.)

ida.demo.common.normalization.regex.any[0]=[\\.|,|\\-|\\*|\\(|\\)|\\[|\\]|`|\\'|/|\\|#|\"]
````

4. In the above configuration format, if only `<reqular expression>` term is provided and if the term `${ida.norm.sep}<replacement string>` is not provided, that regular expression will be replaced with empty string, means it will be removed.
5. The index sequence for one type of configuration should not break in the middle, otherwise normalization properties will not be read for that type further.
6. The **common** normalization attributes will be replaced at the end after replacing all other attributes.

**Note:** For name value normalization we additionally perform Gender Title normalization, where we obtain the title values for the particular language and remove them from the name value.


**3. Proxy Implementations -**   
Below are the proxy implementations used in ID-Authentication:
- ***MISP verification*** - Mocked the verification of MISP by using mocked *License Key*.
- ***Partner verification*** - Mocked the verification of Partner by using mocked *Policy* for the partner which provides the information on whether the Demographic Authentication request is allowed.
- ***MOSIP public key for encrypting Request block*** - The private key used for decrypting the request would be maintained in Partner Management Service, which is currently mocked using reference ID **PARTNER** and application ID **IDA**, and public key of the same should be used while encrypting the request.
- ***keyIndex*** - No validation is performed for `keyIndex` which is present in the Authentication Request. This will be part of V2 implementation when Kernel Crypto would accept keyIndex based key validation.
- ***Digital Signature in request*** - Any digital signature added in the Auth request is not currently validated .
- ***Digital Signature in response*** - The Auth response is digitally signed using the MOSIP private key with reference ID **SIGN** and application ID **KERNEL**, and public key of the same should be used to verify the signature.
