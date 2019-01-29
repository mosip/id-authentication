# kernel-otpmanager-service

[Background & Design](../../docs/design/kernel/kernel-otpmanager.md)

[Api Documentation](https://github.com/mosip/mosip/wiki/Kernel-APIs#7-otp-manager)

Default Port and Context Path

```
server.port=8085
server.servlet.path=/otpmanager

```

localhost:8085/otpmanager/swagger-ui.html


**Application Properties**

[kernel-otpmanager-service-dev.properties](../../config/kernel-otpmanager-service-dev.properties)


```
javax.persistence.jdbc.driver=org.h2.Driver
javax.persistence.jdbc.url=jdbc:h2\:mem\:testdb;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS kernel
javax.persistence.jdbc.user=sa
javax.persistence.jdbc.password=


#OTP Properties
#-------------------------------
#the default length for otp(in number)
mosip.kernel.otp.default-length=6
#the default crypto function
#It can be: HmacSHA512, HmacSHA256, HmacSHA1.
mosip.kernel.otp.mac-algorithm=HmacSHA512
#the default shared key
mosip.kernel.otp.shared-key=123456
#the OTP expires after the given time(in seconds).
mosip.kernel.otp.expiry-time=40
#the key is freezed for the given time(in seconds).
mosip.kernel.otp.key-freeze-time=40
#the number of validation attempts allowed(in number).
#mosip.kernel.otp.validation-attempt-threshold =3 means , the validation and generation will be blocked from 4th time.
mosip.kernel.otp.validation-attempt-threshold=3
#minimum length of key(in number).
mosip.kernel.otp.min-key-length=3
#maximum length of key(in number).
mosip.kernel.otp.max-key-length=255


```
 

** Usage Sample**
 
 Usage1:
 
 OTP Generation Request:
 
 ```
 {
  OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");
RequestBody body = RequestBody.create(mediaType, "{\"key\":\"testkey\"}");
Request request = new Request.Builder()
  .url("http://localhost:8085/otp/generate")
  .post(body)
  .addHeader("content-type", "application/json")
  .build();
  
Response response = client.newCall(request).execute();
 }
 ```
 
OTP Generation Responses :
Successful Generation :

HttpStatus : 201 Created

```
{
    "status": "true",
    "message": "GENERATION_SUCCESSFUL"
}
```

UnSuccessful Generation, Key Freezed :
 
 ttpStatus : 201 Created

```
{
    "otp": "null",
    "status": "USER_BLOCKED"
}
```

Usage2:

OTP Validation Request:
 
```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");
RequestBody body = RequestBody.create(mediaType, "{\"key\" :\"test\"}");
Request request = new Request.Builder()
  .url("http://localhost:8085/otp/validate?key=testkey&otp=279230")
  .get()
  .addHeader("content-type", "application/json")
  .build();

Response response = client.newCall(request).execute();

```
OTP Validation Responses:
Case : Validation Successful

 HttpStatus : 200 OK
 

 ```
{
    "status": "success",
    "message": "VALIDATION_SUCCESSFUL"
}
 ```
 
 
Case : Validation UnSuccessful, Wrong OTP

 HttpStatus : 406 Not Acceptable

 ```
 {
    "status": "failure",
    "message": "VALIDATION_UNSUCCESSFUL"
}
 ```
 
Case : Validation UnSuccessful, OTP Expired

HttpStatus : 406 Not Acceptable

 ```
 {
    "status": "failure",
    "message": "OTP_EXPIRED"
}
 ```
 
Case : Validation UnSuccessful, user Blocked

HttpStatus : 406 Not Acceptable

 ```
 {
    "status": "failure",
    "message": "USER_BLOCKED"
}
 ```
 
