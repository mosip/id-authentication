# MOSIP Release Notes
## 1. Registration Processor

### 1.1 Introduction : <br><sub>This document highlights Sprint 12 features for Registration Processor that are released for QA phase.</sub></br>

### 1.2 Release Summary : 
|         |          |
----------|----------
SubmittedBy|Monobikash Das
Date Raised | 24-MAY-2019
Impact of Release|NA
Implementation Start Date |20-MAY-2019
Implementation end date	|07-JUNE-2019
Jenkins Build #	|Min Version : 1826  [Any later version also]
Objective & Scope of Release| Sprint11 & Scope: Refer the section 3
Acceptance Criteria	| Unit Testing and Code Coverage > 85%
Role/Job Title|Technical Lead
RFC(s) #|	NA


### 1.3 Features Delivered : <br><sub>List of Features Delivered as part of this release should be listed here</sub></br>
Requirement ID | Requirement Type <br>(New\\Enhancement\\Defect)</br> | Description
-----|----------|-------------
MOS-13140|New|As ABIS Handler Stage, I should be able to Create ABIS Requests and Route Camel Requests.
MOS-13141|New|As ABIS Middle-ware, I should be able to Communicate with ABISs
MOS-22023|New|Tech Story - As Registration Processor, I should be able to Authenticate My Self when I fetch file from File System
MOS-24251|New|Tech Story - Trigger VID generation post UIN generation.
MOS-24252|New|Tech Story - Validate Sync List Data with Packet Meta Data in Packet Validator and Implement Changes in Manual Verification.
MOS-22026|New|As Registration Processor, I should be able to Authenticate Officer and Supervisor using their RID
MOS-21718|New|As Registration Processor, I should be able to Perform Parent or Guardian (Introducer) Bio-metric Validation for a Child
MOS-24649|Change|Tech Story - Unit Testiing  for Update Flow Packet
MOS-1084|New|Tech Story - As the MOSIP system, I should be able to evaluate the Quality Score of Finger Print, Iris and Face Image
MOS-17678|New|As the Registration Processor, I should be able to Process a Lost UIN Packet
MOS-24252|Change|Tech Story - Validate Sync List Data with Packet Meta Data in Packet Validator and Implement Changes in Manual Verification
MOS-24331|Change|Tech Story - Sync Meta API changes for Packet Generator Stage



#### 1.4 Prerequisites : <br><sub>Dependent module/component with their respective versions should be mentioned here</sub></br>
Module/Files|Component|Version|Description (If any)
-----|-------------|----------------|--------------
Java|JDK + JRE [Installed]|Main Version: Java 8 Min Version: 181|The Java Version is to be installed in the machine.
kernel-core|NA|NA|Basic core kernel packages.
kernel-logger-logback|NA|0.12.0|Use for the logging.
kernel-dataaccess-hibernate|NA|0.12.0|Used for the communicating to the DB.
kernel-auditmanager-api|NA|0.12.0|Used to audit the reocrds into the DB.
kernel-idgenerator-rid|NA|0.12.0|Used to generate the PRID format.
kernel-idvalidator-uin|NA|0.12.0|Used to validate the PRID format.
kernel-crypto-signature|NA|0.12.0|Used to validate the signature response from server.
kernel-templatemanager|NA|0.12.0|Used to generate the template manager.
kernel-qrcodegenerator-zxing|NA|0.12.0|Used to generate the QR code in acknowledgment page.
kernel-pdfgenerator-itext|NA|0.12.0|Used to scan the document in PDF format.
kernel-crypto-jce|NA|0.12.0|Used to encrypt the pre-redistaion data information.
kernel-jsonvalidator|NA|0.12.0|Used to validate the JSON.
kernel-virusscanner-clamav|NA|0.12.0|Used to communicate to the Antivirus Clam AV.
kernel-applicanttype-api|NA|0.12.0|Used to get the applicant types. 
kernel-masterdat-api|NA|0.12.0|Used to get the master data. 
kernel-notification-api|NA|0.12.0|Used to notify the user.
idrepo-service|NA|0.12.0|Used to save user details and uin in idrepository
pre-registration-datasync-api|NA|0.12.0|used to reverse datasync


### 1.5 Open Issues : <br><sub>List of Open Issues, which would be resolved or fixed in another release version, but same Sprint</sub></br>
Open Items|Description
-----------------|----------------------
|

### 1.6 Defects list :
Defect JIRA ID|Status|Availabilty in the branch
---------------|-------------|------------------
||

### 1.7 Features Pending : <br><sub>List of Features (Requirement) which are still pending at the time of this release for current sprint only, specifying details and date/sprint in which it would be released</sub></br>
Requirement Id|Description|Future Date / Sprint when expected to release | Reason
--------------|-----------|-----------|-------------



### 1.8 DB Changes :
Below are the changes in registration processor DB -

1.	Removal of uin column from regprc.undividual_demogrphic_dedupe table
2.	Deletion of regprc.reg_uin table from DB
3.	New table for lost uin regprc.reg_lost_uin_det




