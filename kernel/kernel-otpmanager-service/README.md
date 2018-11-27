# kernel-otpmanager-service

1- [Background & Design](../../design/kernel/kernel-otpmanager.md)

 
2- API Documentation

 ```
localhost:8085/swagger-ui.html

 ```
 
3- Usage Sample
 
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
 