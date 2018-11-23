## kernel-auditmanager-service
This service can be used to audit events and operations. Service has REST API exposed to save event details as audit in database.

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

**Exceptions to be handled while using this functionality:**

1. AuditHandlerException ("KER-AUD-001", "Invalid Audit Request. Required parameters must be present")
2. InvalidFormatException ("KER-AUD-002", "Audit Request format is invalid");

**Usage Sample**


  *Usage 1:*
  
  *Request*
  
  ```
OkHttpClient client = new OkHttpClient();

MediaType mediaType = MediaType.parse("application/json");
RequestBody body = RequestBody.create(mediaType, "{\r\n  \"eventId\": \"EventId12333\",\r\n  \"eventName\": \"Event Name1\",\r\n  \"eventType\": \"EventType3\",\r\n  \"actionTimeStamp\": \"2018-11-04T10:52:48.838Z\",\r\n  \"hostName\": \"Host Name6\",\r\n  \"hostIp\": \"10.89.213.89\",\r\n  \"applicationId\": \"ApplicationId89\",\r\n  \"applicationName\": \"Application Name22\",\r\n  \"sessionUserId\": \"SessionUserId22\",\r\n  \"sessionUserName\": \"Session UserName22\",\r\n  \"id\": \"id3333\",\r\n  \"idType\": \"idType333\",\r\n  \"createdBy\": \"user1\",\r\n  \"moduleName\": \"Module Name22\",\r\n  \"moduleId\": \"ModuleId22\",\r\n  \"description\": \"Description for event\"\r\n}");
Request request = new Request.Builder()
  .url("http://104.211.214.143:8081/auditmanager/audits")
  .post(body)
  .addHeader("Content-Type", "application/json")
  .build();

Response response = client.newCall(request).execute();
  ```
  
  *Response*
  Status: 200
  
  ```

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

