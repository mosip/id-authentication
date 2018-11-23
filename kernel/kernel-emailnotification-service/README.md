# kernel-emailnotification-service

### 1. Background & Design
```
 This project facilitates mail notification services.
 The mandatory required parameters in the form-data request are:
 * mailTo
 * mailSubject
 * mailContent
The optional required parameters in the form-data request are:
 - mailCc
 - attachments
```
 
 ### 2. [API Documentation <TBA>](TBA)
 ```
 mvn javadoc:javadoc

 ```
 
### 3- Usage Sample
 
 `Usage1:`
 `Email Notification Request:`
 ```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"mailTo\"\r\n\r\ntmail@testmail.com\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"mailSubject\"\r\n\r\n test subject\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"mailContent\"\r\n\r\ntest content\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"mailCc\"\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"attachments\"\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
Request request = new Request.Builder()
  .url("http://localhost:8083/notifier/email")
  .post(body)
  .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
  .addHeader("cache-control", "no-cache")
  .addHeader("postman-token", "501bd591-fa33-d768-2de9-08997aed267b")
  .build();

Response response = client.newCall(request).execute();
 ```
`Email Notification Response :`
##### Successful submission of request :
```
HttpStatus : 202 Accepted
```
```
{
    "status": "Email request submitted"
}
```
##### Failure in argument validations : 

`No mandatory arguments is passed and request is submitted :`
```
HttpStatus : 406 Not Acceptable
```
```
{
    "errors": [
        {
            "errorCode": "KER-NOE-002",
            "errorMessage": "Subject must be valid. It can't be empty or null."
        },
        {
            "errorCode": "KER-NOE-001",
            "errorMessage": "To must be valid. It can't be empty or null."
        },
        {
            "errorCode": "KER-NOE-003",
            "errorMessage": "Content must be valid. It can't be empty or null."
        }
    ]
}
```
`Few mandatory arguments not passed and request is submitted :`
```
HttpStatus : 406 Not Acceptable
```
```
{
    "errors": [
        {
            "errorCode": "KER-NOE-002",
            "errorMessage": "Subject must be valid. It can't be empty or null."
        },
        {
            "errorCode": "KER-NOE-003",
            "errorMessage": "Content must be valid. It can't be empty or null."
        }
    ]
}
```