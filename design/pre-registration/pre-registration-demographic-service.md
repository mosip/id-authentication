# Approach for pre-registration demographic service 

**Background**
- Exposing the REST API to create pre-registration for a citizen, where the data captured in the pre-registration portal should be stored.

The target users are -
   - Pre-Registration portal
   
The key requirements are -

-   Create the API to store the demographic details of the citizen for a pre-registration, where the data captured in the pre-registration portal should be stored.

- Pre-Registration Id should get generated and assigned to demographic details and it should be stored in encrypted JSON in database.

- Once the pre-registration form is stored in database then it should be in "Pending Appointment" status.

- The API should return the success / failure status code along with the respective message.


The key non-functional requirements are

- Security :
    - The Pre-Registration JSON form should be encrypted.
	- Generate hash out of encrypted JSON.
	- Compare hash while retrieving the data.

-   Log the each state of the pre-registration creation:
    -   As a security measures the Pre-Id or applicant information should not be logged.

-   Audit :
    -   Each state of the Pre-Registration creation should be stored into the DB for audit purpose.
    -   Pre-reg Id and important detail of the applicant should not be audited.

-   Exception :

    -   Any exception occurred during the pre-registration, the same will  be reported to the user with the user understandable exception.


**Solution**

**Class Diagram**

![Pre-Registration-Demographic-Service](_images/_class_diagram/demographic-service.png)

**Create a new pre-registration :**
- Create a REST API as '/applications' with POST method which accept the ID Definition JSON object (#id-definition-structure) from the request body.

- Validate the request object against the ID Definition schema, If the provided request ID JSON object is valid then continue with the rest of the process otherwise throw an respective error message.

- Generate the pre-registration id and assign to the requested ID JSON object and set it to the database entity.

- Encrypt the ID JSON with current time-stamp and set it to the database entity. If the encryption is successful continue otherwise throw an respective error message. 

- Generate hash out of encrypted ID JSON and set it to the database entity. If the hash function is successful continue otherwise throw an respective error message. 

- Save the per-registration entity in the database. If data save successful in database render the successful response otherwise any database exception occurs then rollback the current transaction and throw the respective exception otherwise.

- Audit the exception/start/exit of the each stages of the Pre-registration save mechanism using AuditManager component.


**Sequence Diagram**
![Pre-Registration-Demographic-Create](_images/_sequence_diagram/demographic-create.png)




**Update existing pre-registration :**
- Create a REST API as '/applications' with PUT method which accept the ID Definition JSON object (#id-definition-structure) from the request body and pre-registration id from request parameter.

- Get the existing per-registration entity from database by pre-reg id. If data is not present in the database throw an respective exception.

- Validate the requested ID JSON against the ID Definition schema, If the requested ID JSON is valid then continue otherwise throw an respective error message.

- Encrypt the ID JSON with current time-stamp and set it to the database entity. If the encryption is successful continue otherwise throw an respective error message. 

- Generate hash out of encrypted ID JSON and sand set it to the database entity. If the hash function is successful continue otherwise throw an respective error message. 

- Update the per-registration entity in the database. If data update successful in database render the successful response otherwise any database exception occurs then rollback the current transaction and throw the respective exception otherwise.

- Audit the exception/start/exit of the each stages of the Pre-registration update mechanism using AuditManager component.

**Sequence Diagram**

![Pre-Registration-Demographic-Update](_images/_sequence_diagram/demographic-update.png)



**Update existing pre-registration status:**
- Create a REST API as '/applications/status' with PUT method which accept the pre-registration id from request parameter and status in query parameter.

- Get the existing per-registration entity from database by pre-reg id. If data is not present in the database throw an respective exception.

- Set the per-registration entity with new status.

- Update the per-registration entity in the database. If data update successful in database render the successful response otherwise any database exception occurs then rollback the current transaction and throw the respective exception otherwise.

- Audit the exception/start/exit of the each stages of the Pre-registration update status mechanism using AuditManager component.

**Sequence Diagram**

![Pre-Registration-Demographic-Update-Status](_images/_sequence_diagram/demographic-update-status.png)


**Retrieve existing pre-registration :**
- Create a REST API as '/applications' with GET method which accept the pre-registration id from request parameter.

- Get the existing per-registration entity from database by pre-reg id. If data is not present in the database throw an respective exception.

- Retrieve the user-id from the authorization token and compare with user-id present in entity, if it success then proceed with next operation otherwise throw an respective exception.

- Generate hash out of encrypted ID JSON and compare with the existing hash from the entity, if it success then proceed with next operation otherwise throw an respective exception.

- Then decrypt ID JSON using time-stamp of encryption by using KERNEL API, send a successful response with ID JSON otherwise throw an respective exception.

- Audit the exception/start/exit of the each stages of the Pre-registration retrieve mechanism using AuditManager component.

**Sequence Diagram**

![Pre-Registration-Demographic-Retrieve](_images/_sequence_diagram/demographic-retrieve-by-preRegId.png)



**Retrieve existing pre-registration status:**
- Create a REST API as '/applications/status' with GET method which accept the pre-registration id from request parameter.

- Get the existing per-registration entity from database by pre-reg id. If data is not present in the database throw an respective exception.

- Then status of the pre-registration given into the response.

- Audit the exception/start/exit of the each stages of the Pre-registration retrieve mechanism using AuditManager component.


**Sequence Diagram**

![Pre-Registration-Demographic-Retrieve-Status](_images/_sequence_diagram/demographic-retrieve-status.png)


**Retrieve All existing pre-registration for the User:**
- Create a REST API as '/applications' with GET method.

- Retrieve the user-id from the authorization token.

- Get the all per-registration entity from database by user-id. If data is not present in the database throw an respective exception.

- Loop all per-registration entities and generate hash out of encrypted ID JSON and compare with the existing hash from the entity, if it success then proceed with next operation otherwise throw an respective exception.

- Then decrypt ID JSON using time-stamp of encryption by using KERNEL API, Get the required field values from the entity and construct an object and assign to the list with ID JSON otherwise throw an respective exception.

- Send the list of object into the response.

- Audit the exception/start/exit of the each stages of the Pre-registration retrieve all mechanism using AuditManager component.

**Sequence Diagram**

![Pre-Registration-Demographic-Retrieve-All](_images/_sequence_diagram/demographic-retrieve-by-userId.png)


**Discard Pre-Registration:**
- Create a REST API as '/applications' with DELETE method which accept the pre-registration id from request parameter.

- Retrieve the user-id from the authorization token.

- Get the existing per-registration entity from database by pre-reg id. If data is not present in the database throw an respective exception.

- Compare user-id with entity's user-id. if it matches then delete the record from the database and send the success response otherwise throw an respective exception.

- Audit the exception/start/exit of the each stages of the Pre-registration delete mechanism using AuditManager component.

**Sequence Diagram**

![Pre-Registration-Demographic-Delete](_images/_sequence_diagram/demographic-delete.png)



**Success / Error Code** 

 While processing the Pre-Registration if there is any error or successfully
 then send the respective success or error code to the UI from API layer as  Response object.

  Code   |       Type  | Message|
-----|----------|-------------|
  0000      |             Success |   Packet Successfully created
  PRG_PAM_APP_001 |  Error   |   Unable create the pre-registration.
  PRG_PAM_APP_002  | Error   |   Pre-Registration table not accessible
  PRG_PAM_APP_003 |  Error   |   Delete operation not allowed
  PRG_PAM_APP_004  | Error   |   Failed to delete the registration
  PRG_PAM_APP_005 |  Error    |  Unable to fetch the registrations

**Dependency Modules**

Component Name | Module Name | Description | 
-----|----------|-------------|
  Audit Manager     |   Kernel        |    To audit the process while creating the pre-registration.
  Exception Manager  |  Kernel     |       To prepare the user defined exception and render to the user.
  Log        |          Kernel         |   To log the process.
  JOSN Utility    |     Kernel       |     To validate the ID definition JSON object over ID Definition Schema
  Pre-Id Generator    |    Kernel      |      To get the generated Pre-Registration Id
  Database Access   |    Kernel      |      To get the database connectivity


**User Story References**

  **User Story No.** |  **Reference Link** |
  -----|----------|
  **MOS-623**      |     <https://mosipid.atlassian.net/browse/MOS-623>
  **MOS-626**      |     <https://mosipid.atlassian.net/browse/MOS-626>
  **MOS-805**       |    <https://mosipid.atlassian.net/browse/MOS-805>
