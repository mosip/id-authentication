# Identity Event Notification Handler REST Service


**1. Background**

Identity Event Notification Handler REST Service REST service can be used by ID-Repository Module to notify the UIN/VID create/update event. It supports below types of event types –
 - Create UIN, 
 - Create VID, 
 - Update UIN, 
 - Update VID


 ***1.1.Target Users -***  
ID-Repository module can use Identity Event Notification Handler REST Service to notify the UIN/VID create/update events.

 ***1.2. Key Requirements -***   
-	ID-Repository module should invoke the API with the same authentication session of the ID Repo APIs that involve UIN/VID creation/update.

 ***1.3. Key Non-Functional Requirements -***   
-	Logging :
	-	Log each stage of authentication process
	-	Log all the exceptions along with error code and short error message
	-	As a security measure, Individual's UIN or PI/PA should not be logged
-	Audit :
	-	Audit all transaction details during authentication process in database
	-	Individual's UIN not be audited
	-	Audit any invalid UIN or UserID incidents
-	Exception :
	-	Any error should be handled with appropriate error code and message in Auth Response 
-	Security :
	-	Auth details of an individual is a sensitive information, hence should be encrypted and stored.
	-	Auth Request contains sensitive identity information of an Individual.


**2. Solution**   
Identity Event Notification Handler REST Service addresses the above requirements as explained below.

1.	ID-Repository to construct a **POST** request with below details and send to Request URL `/idauthentication/v1/internal/notify` - [Sample Request Body](https://github.com/mosip/mosip-docs/wiki/ID-Authentication-APIs#authentication-service-internal)
2.	Integrate with Kernel UIN Validator and VID Validator to check UIN/VID for validity. 
3.	Once the above validations are successful, the notification event will be processed as below: 
- Create UIN -  The UIN record will be fetched from ID Repo for the UIN and it will be encrypted and inserted in IDA DB against the UIN.
- Update UIN -  The UIN record will be fetched from ID Repo for the UIN and it will be encrypted and updated in IDA DB along with any expiry time provided in the event details.
- Create VID - This Event details should contain UIN value also, and that UIN record will be fetched from ID Repo for the UIN and it will be encrypted and stored against the VID and also the expiry time and transaction limit value of the VID will be stored.
- Update VID - If UIN is provided with the event details, that UIN will be used to fetch record from ID Repo and it will be encrypted and updated against the VID. Also the expiry time and transaction limit value of the VID will be updated against the VID.
6.	Retrieve Identity details of the Individual based on UIN from ID Repository
7.	Respond with below success Auth response - [Sample Response](https://github.com/mosip/mosip-docs/wiki/ID-Authentication-APIs#success-response-3)

**2.1. Class Diagram:**   
The below class diagram shows relationship between all the classes which are required for Bio authentication service.

![Internal Auth Class Diagram](_images/Internal_Auth_Class_Diagram.PNG)

**2.2. Sequence Diagram:**   
![Internal Auth Sequence Diagram](_images/Internal_Auth_Sequence_Diagram.PNG)

