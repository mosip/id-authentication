## kernel-auditmanager-api
kernel-auditmanager-api is used to audit data.

[Background & Design](../../design/kernel/kernel-auditmanager.md)

**Api Documentation**


```
mvn javadoc:javadoc
```


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
  
*Usage:*
 
 ```
@Autowired
	private AuditHandler auditHandlerImpl;
	
		AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();

		auditRequestBuilder.setActionTimeStamp(LocalDateTime.now()).setApplicationId("applicationId")
				.setApplicationName("applicationName").setCreatedBy("createdBy").setDescription("description")
				.setEventId("eventId").setEventName("eventName").setEventType("eventType").setHostIp("hostIp")
				.setHostName("hostName").setId("id").setIdType("idType").setModuleId("moduleId")
				.setModuleName("moduleName").setSessionUserId("sessionUserId").setSessionUserName("sessionUserName");

		AuditRequestDto auditRequest = auditRequestBuilder.build();
		auditHandlerImpl.addAudit(auditRequest);

 
 ```

