# Approach for Reverse Data Sync with Registartion Processor

**Background**

-   Expose the API to Registration Processer will provide with the list of Pre-Registration IDs for which it wants to update Pre-Registration status.

The target users are -
   - Registration Processer

The key requirements are -
-   Create the REST API to Registration Processer will provide with the list of Pre-Registration IDs for which it as consumed.

The key non-functional requirements are

-   Log the each state of the reverse data sync:

    -   As a security measures the Pre-Id or registration processor information should not be logged.

-   Audit :

    -   Registartion processor ID, Transaction ID, Timestamp should be stored into the DB for audit purpose.

    -   Pre-reg Id and important detail of the applicant should not be audited.

-   Exception :

    -   Any exception occurred during the pre-registration reverse data sync, the same will be reported to the registartion processor in a understandable exception.


**Solution**

**Store all pre-registration Ids :**

-   Create a REST API as '/reverseDataSync' POST method accept the JSON object  from the registration-processor.

- The Registration Processor will provide the List of Pre-Registration IDs received by it(from Registration Client). 

- The System will generate a Transaction ID and store all the Pre-Registration ids in "prereg-i_processed_prereg_list" table and update in "prereg-processed_prereg_list" table.

- The "prereg-i_processed_prereg_list" table is not permanent, for maintanance purpose database team can truncate this table.

- A batch job need to be running to update the application.demoraphic table with "Processed" status.

-  Once Pre-Registration successfully processed. System will send an Acknowledgement of the Receipt ("need to be check BA(Vyas)")

-   Audit the exception/start/exit of the each stages of the reverse data sync mechanism using AuditManager component.

**Class Diagram**
![reverse data-sync service class diagram](_images/_class_diagram/reverse-data-sync-service-classDiagram.png)

**Sequence Diagram**
![reverse data-sync service sequence diagram](_images/_sequence_diagram/reverse-data-sync-service.png)


**Success / Error Code**
- While processing the Pre-Registration if there is any error or successfully then send the respective success or error code to the UI from API layer as  Response object.

Code | Type | Message | 
-----|----------|-------------|
0000      |   Success |   Packet Successfully created

**Dependency Modules**

Component Name | Module Name | Description | 
-----|----------|-------------|
  Audit Manager     |   Kernel        |    To audit the process while reverse data sync.
  Exception Manager  |  Kernel     |       To prepare the user defined exception and render to the user.
  Log        |          Kernel         |   To log the process.
  Database Access   |    Kernel      |      To get the database connectivity


**User Story References**

  **User Story No.**|**Reference Link**
  -----|----------|
  **MOS-1999**       |    <https://mosipid.atlassian.net/browse/MOS-1999>
