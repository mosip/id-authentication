## kernel-smsnotification-service
 
 1- [Background & Design](../../design/kernel/kernel-smsnotification.md)
 

 2- [API Documentation <TBA>](TBA)
 
 ```
 mvn javadoc:javadoc

 ```
 
 3- Usage Sample
 
Request

 ```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");

RequestBody body = RequestBody.create(mediaType, "{\n\"message\": \"OTP-432467\",\n\"number\": \"98*****897\"\n}");

Request request = new Request.Builder()
  .url("http://104.211.214.143:8084/notifier/sms")
  .post(body)
  .addHeader("content-type", "application/json")
  .build();

Response response = client.newCall(request).execute();
 
 ```


Response body model for POST **/notifier/sms**
Status: 202
  
 ```
{
  "message": "Sms Request Sent",
  "status": "success"
}
 ```
 
Exception Scenario-

1.Null or empty inputs provided-

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









