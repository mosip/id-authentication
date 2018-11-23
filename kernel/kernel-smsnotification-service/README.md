## kernel-smsnotification-msg91-service
This folder has smsnotification module which sends sms on mobile number provided. 
 
 1- [Background & Design](../../design/kernel/kernel-smsnotification.md)
 

 2- [API Documentation <TBA>](TBA)
 
 ```
 mvn javadoc:javadoc

 ```
 
 3- Usage Sample
 
Request body model for POST **/notification/sms**
 
 ```
{
  "message": "OTP-432467",
  "number": "98******79"
}
 
 ```

Response body model for POST **/notification/sms**
  
 ```
{
  "message": "Sms Request Sent",
  "status": "success"
}
 ```








