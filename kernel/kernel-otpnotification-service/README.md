# kernel-otpnotification-service

[Background & Design -TBA-](../../docs/design/kernel/kernel-otpnotification.md)

[Api Documentation](https://github.com/mosip/mosip/wiki/Kernel-APIs#7-otp-notification)

Default Port and Context Path

```
server.port=8082
server.servlet.path=/otpnotifier

```

localhost:8082/otpnotifier/swagger-ui.html

**Application Properties**

[kernel-otpnotification-service-dev.properties](../../config/kernel-otpnotification-service-dev.properties)


** Usage Sample**
 
 Usage1:
 
 OTP Notification Request:
 
 ```
HttpResponse<String> response = Unirest.post("http://localhost:8080/v1.0/otp/send")
  .header("content-type", "application/json")
  .header("cache-control", "no-cache")
  .header("postman-token", "16196521-5e9d-7d61-ae46-8fda4e3220ca")
  .body("{\r\n  \"emailBodyTemplate\": \"YOUR LOGIN OTP IS $otp\",\r\n  \"emailId\": \"abc@gmail.com\",\r\n  \"emailSubjectTemplate\": \"OTP ALERT\",\r\n  \"mobileNumber\": \"8989898989\",\r\n  \"notificationTypes\": [\r\n      \"sms\"\r\n  ],\r\n  \"smsTemplate\": \"YOUR LOGIN OTP IS $otp\"\r\n}")
  .asString();
 ```
 
OTP Notification Responses :

Successful Notification :

HttpStatus : 200 Ok

```
{
    "status": "success",
    "message": "Otp notification request submitted"
}
```


