# kernel-licensekeymanager-service

[Background & Design -TBA-](../../docs/design/kernel/kernel-licensekeymanager.md)

 
[API Documentation](https://github.com/mosip/mosip/wiki/Kernel-APIs#9-license-key-manager)


Default Port and Context Path

```
server.port=8093
server.servlet.path=/licensekeymanager

```

localhost:8093/licensekeymanager/swagger-ui.html


**Application Properties**

[kernel-otpmanager-service-dev.properties](../../config/kernel-otpmanager-service-dev.properties)

 
**Usage Sample**
 
 Usage1:
 
 License Generation Request:
 
 ```
{ 
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");
RequestBody body = RequestBody.create(mediaType, "{\"tspId\":\"TSPID1\",\"licenseExpiryTime\":\"2019-02-07T05:35:53.476Z\"}");
Request request = new Request.Builder()
  .url("http://localhost:8080/v1.0/license/generate")
  .post(body)
  .addHeader("content-type", "application/json")
  .addHeader("cache-control", "no-cache")
  .addHeader("postman-token", "7d3b19f4-5a6c-d926-4975-1f228f8caa3e")
  .build();

Response response = client.newCall(request).execute();
}
 ```
 
License Generation Responses :
Successful Generation :

HttpStatus : 200 OK

```
{
    "licenseKey": "rAx2TRvemovtZ0to"
}
```



Usage2:

License Mapping Request:
 
```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");
RequestBody body = RequestBody.create(mediaType, "{ \"lkey\": \"rAx2TRvemovtZ0to\",\"permissions\": [\"OTP Trigger\",\"OTP Authentication\"],\"tspId\": \"TSPID1\"}");
Request request = new Request.Builder()
  .url("http://localhost:8080/v1.0/license/map")
  .post(body)
  .addHeader("content-type", "application/json")
  .addHeader("cache-control", "no-cache")
  .addHeader("postman-token", "86230d1c-f33d-0ab1-6726-8f7f6ade6072")
  .build();

Response response = client.newCall(request).execute();

```
License Mapping Responses:
Case : Mapping Successful

 HttpStatus : 200 OK
 

 ```
{
    "status": "Mapped License with the permissions"
}
 ```
 
 
Case : Mapping UnSuccessful, Permission entered is not present in the master list(in .properties)

 HttpStatus : 200 OK

 ```
{
    "timestamp": 1549431842799,
    "status": 200,
    "errors": [
        {
            "errorCode": "KER-LKM-003",
            "errorMessage": "Permission value entered is not accepted."
        }
    ]
}
 ```
 
 Usage3:

License Fetch Request:
 
```
 OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url("http://localhost:8080/v1.0/license/fetch?licenseKey=rAx2TRvemovtZ0to&tspId=TSPID1")
  .get()
  .addHeader("cache-control", "no-cache")
  .addHeader("postman-token", "ac4daf24-2cef-f5f5-50f4-32b0d1938177")
  .build();

```

License Fetch Responses:

Case : Fetch Successful

 HttpStatus : 200 OK
 

 ```
{
    "mappedPermissions": [
        "OTP Trigger",
        "OTP Authentication"
    ]
}
 ```
 
 Case : Fetch UnSuccessful, When TSPID and license key entered are not mapped(i.e. entered wrong).

 HttpStatus : 200 OK
 
 ```
 {
    "timestamp": 1549432163046,
    "status": 200,
    "errors": [
        {
            "errorCode": "KER-LKM-004",
            "errorMessage": "LicenseKey Not Found."
        }
    ]
}
 ```
 
 
 
