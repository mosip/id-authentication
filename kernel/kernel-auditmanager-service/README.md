## kernel-auditmanager-service
This folder has kernel-auditmanager-service module which can be used to audit data.

[Background & Design](../../design/kernel/kernel-auditmanager.md)

**Api Documentation**


```
mvn javadoc:javadoc
```

**Properties to be added in parent Spring Application environment**
[kernel-auditmanager-service-dev.properties](../../config/kernel-auditmanager-service-dev.properties)


**The inputs which have to be provided are:**
1. Audit Event ID - Mandatory
2. Audit Event name - Mandatory
3. Audit Event Type - Mandatory
4. Action DateTimestamp - Mandatory
5. Host - Name - Mandatory
6. Host - IP - Mandatory
7. Application Id - Mandatory
8. Application Name - Mandatory
9. Session User Id - Mandatory
10. Session User Name - Mandatory
11. Module Name – Optional
12. Module Id - Optional
13. ID - Mandatory
14. ID Type - Mandatory
15. Logged Timestamp - Mandatory
16. Audit Log Description - Optional
17. cr_by, (Actor who has done the event) - Mandatory
18. cr_dtimes, (When this row is inserted into DB) - Mandatory


**The response will be true is audit request is successful, otherwise false** 

**If there is any error which occurs while encryption and decryption, it will be thrown as Exception.** 

**Exceptions to be handled while using this functionality:**

1. AuditHandlerException ("KER-AUD-001", "Invalid Audit Request. Required parameters must be present")
2. InvalidFormatException ("KER-AUD-002", "Audit Request format is invalid");

**Usage Sample**

  *Usage 1:*
  
  *Request*
  
  ```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");
RequestBody body = RequestBody.create(mediaType, "{\r\n  \"eventId\": \"string\",\r\n  \"eventName\": \"string\",\r\n  \"eventType\": \"string\",\r\n  \"actionTimeStamp\": \"2018-11-23T08:42:59.632Z\",\r\n  \"hostName\": \"string\",\r\n  \"hostIp\": \"string\",\r\n  \"applicationId\": \"string\",\r\n  \"applicationName\": \"string\",\r\n  \"sessionUserId\": \"string\",\r\n  \"sessionUserName\": \"string\",\r\n  \"id\": \"string\",\r\n  \"idType\": \"string\",\r\n  \"createdBy\": \"string\",\r\n  \"moduleName\": \"string\",\r\n  \"moduleId\": \"string\",\r\n  \"description\": \"string\"\r\n}");
Request request = new Request.Builder()
  .url("http://104.211.214.143:8081/auditmanager/audits")
  .post(body)
  .addHeader("Content-Type", "application/json")
  .addHeader("Cache-Control", "no-cache")
  .addHeader("Postman-Token", "c8e0b189-346c-47ac-89dc-4b2ab5650e00")
  .build();

Response response = client.newCall(request).execute();
  ```
  
  *Response*
  
  ```
Status: 200
{
  "status": true
}
  ```
  
  


 *Invalid Audit Request*

```
{
    "code": "KER-AUD-001",
    "message": "Invalid Audit Request. Required parameters must be present"
}
```
 

 *Invalid Audit Format*

```
{
    "code": "KER-AUD-002",
    "message": "Audit Request format is invalid"
}
```

