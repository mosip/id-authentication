# Pre-Registration-notification-service:

[Background & Design](pre-registration-individual.md)

This service is used by Pre-Registration portal to trigger notification via SMS or Email and get QRCode.

#### Api Documentation

```
mvn javadoc:javadoc

```

#### POST Operation
#### Path - `notification/notify`
#### Summary

This request is used to notify the pre-registration acknowledgement via Email and SMS.

#### Request part Parameters

1. id
2. version
3. requestTime
4. request
5. request.name	
6. request.preRegistrationId	
7. request.appointmentDate	
8. request.appointmentTime	
9. request.mobNum	
10. request.emailID	
11. request.multipart file	
12. request.LangCode	

#### Response

On success it retuns a message saying 'Email and sms request successfully submitted' else gives appropriate error message.