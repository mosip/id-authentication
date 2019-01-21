
# Approach for create pre-registration

**Background**
- Exposing the REST API to create pre-registration for a citizen, where the data captured in the pre-registration portal should be stored.

The target users are -
   - Pre-Registration portal
   
The key requirements are -

-   Create the API to store the demographic details and supporting document of the citizen for a pre-registration, where the data captured in the pre-registration portal should be stored.

-   Demographic form should have the detail of:

    -   Name

    -   Age/DOB

    -   Gender Type

    -   Address

    -   Location details

    -   Contatct details

    -   CINE / PIN number.

- Document should have following type:
     - Proof of Identity

     - Proof of Address

     - Proof of DOB

- Pre-Registration Id should get generated and assigned to demographic details and it should be stored in JSON format in database.

-  Once the pre-registation form is stored in database then it should be in "Pending Appointment" status.

-   The API should return the success / failure status code along with
    the respective message.

- For a document upload, it should be a different microservice as per the technical architecture. 

- For each document upload it should hit the rest api to store the document in database.

- There should be a copy document option in the UI prospective, Once it get checked then an REST API call should happen with source pre-registation id, destination pre-registartion id and document catageory type, with this parameter document get copied to the destination pre-registartion id in database.

- we should get all the documents associated with pre-registartion id. 

- we should able to delete particular document by providing the document id and pre-registartion id.


The key non-functional requirements are

-   Security :

    -  The Pre-Registartion JSON form should be encrypt and stored in the     pre-registartion database.

-   Log the each state of the pre-registration creation:

    -   As a security measures the Pre-Id or applicant information should
        not be logged.

-   Audit :

    -   Each state of the Pre-Registration creation should be stored into the DB
        for audit purpose.

    -   Pre-reg Id and important detail of the applicant should not be audited.

-   Exception :

    -   Any exception occurred during the pre-registration, the same will
        be reported to the user with the user understandable exception.


**Out of Scope**

-   Key management for encrypting JSON.

-   Integrating with Pre-Registartion UI client. \[will be
    taken care in the next sprint\]

- Scanning the uploaded document for virus.

- Document quality checking to meet the threshold acceptence level.

- Encrypting the uploaded document and store it in database.

**Solution**

**Create a new pre-registration :**

-   Create a REST API as '/applications' accept the ID Definition JSON object (#id-definition-structure) from the pre-registration application portal.

- Once all the mandatory fields enter then only "Save" button get enable to call the REST API to create. 

-   Validate the request object against the ID Definition schema and render the respective error message.

-   If the provided request JSON object is valid then continue with the rest
    of the process.

-   Generate the pre-registration id and assign to the requested JSON object.

-  Encrypte the requested JSON and store it in database with pre-registration id.

-   Audit the exception/start/exit of the each stages of the Pre-registration create mechanism using AuditManager component.


**Document Upload:**

-   Create a REST Service as '/documents' accept the JSON object (#document-json-structure) and a multipart file from the pre-registration application portal..

-   In the document JSON object it should contain the pre-registration id, uploaded by, uploaded timestamp, document type, document category and document format.

- 	Following operation need to be happen:

	Step1: Document Virus scanning, if successful go to next step otherwise throw an exception.{will be taken care in the next sprint}
	
	Step2: Document quality checking, if successful go to next step otherwise throw an exception.{will be taken care in the next sprint}
	
	Step3: Document encryption, if successful go to next step otherwise throw an exception.{will be taken care in the next sprint}
	
	Step4: Document Id Generation, if successful go to next step otherwise throw an exception
	
	Step5: Store in document table along with the pre-registartion id.


**View All Pre-Registration associated with User-id:**

- Once the user login to the pre-registration application, An REST API call should happen to retrieve all the pre-registartion created by him.

- Each pre-registration details should contain pre-registration id, name of the applicant,appointment date time,status.

**Discard Pre-Registration:**

- Discard pre-registration application those are in "Pending Appointment" status, An REST API call should happen with a parameter of "Pre-Registration ID" to delete pre-registartion appliaction and associated document.

- While discarding an pre-registartion application an REST API call should happen to delete all associated doument first with "Pre-Registartion ID" if any exception occured an error response need to send back.

- After successful deletion of all documents an application should get deleted. if any exception occured an error response need to send back.


**Class Diagram**

![pre-registration appliaction service class diagram](_images/_class_diagram/_class_diagram/pre-registration-appliaction-service-classDiagram.png)

![pre-registration document service class diagram](_images/_class_diagram/_class_diagram/pre-registration-document-service-classDiagram.png)

**Sequence Diagram**

![pre-registration appliaction create](_sequence_diagram/pre-registration-appliaction-create.png)

![pre-registration get all appliaction](_sequence_diagram/pre-registration-get-all-appliaction.png)

![pre-registration upload document](_sequence_diagram/pre-registration-upload-document.png)

![pre-registration get document](_sequence_diagram/pre-registration-get-document.png)

![pre-registration delete document](_sequence_diagram/pre-registration-delete-document.png)

![pre-registration copy document](_sequence_diagram/pre-registration-copy-document.png)

**Success / Error Code** 

 While processing the Pre-Registration if there is any error or successfully
 then send the respective success or error code to the UI from API layer as  Response object.

  Code   |       Type  | Message|
-----|----------|-------------|
  0000      |             Success |   Packet Successfully created
  PRG_PAM_APP_001 |  Error   |   Unable create the pre-registration.
  PRG_PAM_APP_002  | Error   |   Registration table not accessible
  PRG_PAM_APP_003 |  Error   |   Delete operation not allowed
  PRG_PAM_APP_004  | Error   |   Failed to delete the registration
  PRG_PAM_APP_005 |  Error    |  Unable to fetch the registartions
  PRG_PAM_DOC_006 |  Error   |   Document failed to upload
  PRG_PAM_DOC_007  | Error   |   Document exceeding premitted size
  PRG_PAM_DOC_008 |  Error   |   Document type not supported
  PRG_PAM_DOC_009  | Error   |   Document invalid format
  PRG_PAM_DOC_010 |  Error    |  Document failed in virus scan
  PRG_PAM_DOC_011 |  Error   |  Document failed in quality check
  PRG_PAM_DOC_012  | Error   |   Document failed in encryption
  PRG_PAM_DOC_013  | Error    |  Document failed in decryption
  PRG_PAM_DOC_014  | Error   |   Document not present
  PRG_PAM_DOC_015 |  Error   |   Document failed to delete
  PRG_PAM_DOC_016  | Error   |   Document failed to copy

**Dependency Modules**

Component Name | Module Name | Description | 
-----|----------|-------------|
  Audit Manager     |   Kernel        |    To audit the process while creating the pre-registation.
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
