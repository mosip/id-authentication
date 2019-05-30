## kernel-smsnotification-service
 
[Background & Design](../../docs/design/kernel/kernel-smsnotification.md)

[Api Documentation](https://github.com/mosip/mosip/wiki/Kernel-APIs#sms-notification)

** Infobip Registartion**

- This service is developed based on **infobip** SMS gateway. 

- Register on [Infobip](https://www.infobip.com/en/get-started) and configure same username and password in properties.

- Update **mosip.kernel.sms.api** property with the api got after registration, Follow [Infobib-Api](https://dev.infobip.com/send-sms/single-sms-message) and get api from **Resource** .

Default Port and Context Path

```
server.port=8084
server.servlet.path=/smsnotifier

```

localhost:8084/smsnotifier/swagger-ui.html


**Application Properties**

[kernel-smsnotification-service-dev.properties](../../config/kernel-smsnotification-service-dev.properties)
 
 
 ```
mosip.kernel.sms.api=https://9qqdy.api.infobip.com/sms/2/text/single
mosip.kernel.sms.username=MDTMOSIP
mosip.kernel.sms.password=Start$01
mosip.kernel.sms.country.code=91
mosip.kernel.sms.sender=MOSMSG
mosip.kernel.sms.route=4

mosip.kernel.sms.number.length=10
 
 ```
 
 
**Usage Sample**
 
Request

 ```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");

RequestBody body = RequestBody.create(mediaType, "{\"message\": \"OTP-432467\",\"number\": \"98*****897\"}");

Request request = new Request.Builder()
  .url("http://localhost:8084/smsnotifier/sms")
  .post(body)
  .addHeader("content-type", "application/json")
  .build();

Response response = client.newCall(request).execute();
 
 ```


Response body model for POST **/notifier/sms**

HttpStatus: 202 Accepted
  
 ```
{
  "message": "Sms Request Sent",
  "status": "success"
}
 ```
 
Exception Scenario-

1.Null or empty inputs provided-

HttpStatus : 406 Not Acceptable

```
{
    "errors": [
        {
            "errorCode": "KER-NOS-001",
            "errorMessage": "Number and message can't be empty, null"
        }
    ]
}

```

2.Invalid number present-

HttpStatus : 406 Not Acceptable


```
{
   "errors": [
        {
            "errorCode": "KER-NOS-002",
            "errorMessage": "Contact number cannot contains alphabet,special character or less than 10 digits"
        }
    ]
}

```









