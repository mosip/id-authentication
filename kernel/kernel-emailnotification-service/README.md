# kernel-emailnotification-service

[Background & Design](../../docs/design/kernel/kernel-emailnotification.md)
 
[Api Documentation](https://github.com/mosip/mosip/wiki/Kernel-APIs#6-email-notification)

Default Port and Context Path
 
 ```
server.port=8083
server.servlet.path=/emailnotifier

 ```
 
 localhost:8083/emailnotifier/swagger-ui.html
 
  **Application proprties**

[kernel-emailnotification-service-dev.properties](../../config/kernel-emailnotification-service-dev.properties)

```

# SMTP (Gmail-SMTP-Properties)
#host being used.
spring.mail.host=smtp.gmail.com
#user mail id, from which the mail will be sent.
spring.mail.username=username@gmail.com
#user password, password to authenticate the above mail address.
spring.mail.password=mailpwd
#port being used.
spring.mail.port=587
#protocol being used.
spring.mail.properties.mail.transport.protocol=smtp
#property to enable/disable tls.
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.starttls.enable=true
#property to enable/disable authorization.
spring.mail.properties.mail.smtp.auth=true
#property to set the mail debugging.
spring.mail.debug=false
#-------------------------------------
# MULTIPART (Multipart-Properties)
# Enable multipart uploads
spring.servlet.multipart.enabled=true
# Max file size.
spring.servlet.multipart.max-file-size=5MB 

```
 
 The mandatory required parameters in the form-data request are:
 * mailTo
 * mailSubject
 * mailContent
The optional required parameters in the form-data request are:
 - mailCc
 - attachments
 
**Usage Sample:**
 
 Usage1:
 Email Notification Request:
 
```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");

RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gWContent-Disposition: form-data; name=\"mailTo\"tmail@testmail.com------WebKitFormBoundary7MA4YWxkTrZu0gWContent-Disposition: form-data; name=\"mailSubject\" test subject------WebKitFormBoundary7MA4YWxkTrZu0gWContent-Disposition: form-data; name=\"mailContent\"test content------WebKitFormBoundary7MA4YWxkTrZu0gWContent-Disposition: form-data; name=\"mailCc\"------WebKitFormBoundary7MA4YWxkTrZu0gWContent-Disposition: form-data; name=\"attachments\"------WebKitFormBoundary7MA4YWxkTrZu0gW--");

Request request = new Request.Builder()
  .url("http://localhost:8083/emailnotifier/email")
  .post(body)
  .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
  .build();

Response response = client.newCall(request).execute();

 ```
 
Email Notification Response :

Successful submission of request :
HttpStatus : 202 Accepted

```
{
    "status": "Success",
    "message": "Email request sent"
    
}
```

Failure in argument validations : 

If No mandatory arguments is passed and request is submitted :

HttpStatus : 406 Not Acceptable


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

If Few mandatory arguments not passed and request is submitted :

HttpStatus : 406 Not Acceptable

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