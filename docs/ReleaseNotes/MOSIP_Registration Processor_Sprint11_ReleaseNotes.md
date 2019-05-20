# MOSIP Release Notes
## 1. Registration Processor

### 1.1 Introduction : <br><sub>This document highlights Sprint 11 features for Registration Processor that are released for QA phase.</sub></br>

### 1.2 Release Summary : 
|         |          |
|----------|----------|
SubmittedBy|Monobikash Das
Date Raised | 16-MAY-2019
Impact of Release|NA
Implementation Start Date |26-APR-2019
Implementation end date	|17-MAY-2019
Jenkins Build #	|Min Version : 1548  [Any later version also]
Objective & Scope of Release| Sprint11 & Scope: Refer the section 3
Acceptance Criteria	| Unit Testing and Code Coverage > 85%
Role/Job Title|Technical Lead
RFC(s) #|	NA


### 1.3 Features Delivered : <br><sub>List of Features Delivered as part of this release should be listed here</sub></br>
Requirement ID | Requirement Type <br>(New\\Enhancement\\Defect)</br> | Description
-----|----------|-------------
MOS-21753|New|As Registration Processor, I should be able to Decrypt a Packet using Center ID and Machine ID.
MOS-21717|New|As Registration Processor, I should be able to Fetch the Packet from DMZ and Store it in Secure Zone
MOS-21715|New|As Registration Processor, I should be able to Perform Virus Scan on the Encrypted Packet in Memory when I receive it from Registration Client
MOS-21714|New|As Registration Processor, I should be able to Perform Size Validation on the Encrypted Packet when I receive it from Registration Client.
MOS-21713|New|As Registration Processor, I should be able to Perform Check Sum Validation on the Encrypted Packet before I Store the Packet in Packet Store.
MOS-21712|New|As Registration Processor, I should be able to Perform Check Sum Validation on the Encrypted Packet when I receive it from Registration Client
MOS-21711|New|As Registration Processor, I should be able to Receive Encrypted Sync Meta Information for a Packet from Registration Client
MOS-21754|New|As Registration Processor, I should be able to Digitally Sign all Responses sent using a MOSIP Private Key.
MOS-21753|New|As Registration Processor, I should be able to Decrypt a Packet using Center ID and Machine ID.

#### 1.4 Prerequisites : <br><sub>Dependent module/component with their respective versions should be mentioned here</sub></br>
Module/Files|Component|Version|Description (If any)
-----|-------------|----------------|--------------
Java|JDK + JRE [Installed]|Main Version: Java 8 Min Version: 181|The Java Version is to be installed in the machine.
kernel-core|NA|NA|Basic core kernel packages.
kernel-logger-logback|NA|0.11.0|Use for the logging.
kernel-dataaccess-hibernate|NA|0.11.0|Used for the communicating to the DB.
kernel-auditmanager-api|NA|0.11.0|Used to audit the reocrds into the DB.
kernel-idgenerator-rid|NA|0.11.0|Used to generate the PRID format.
kernel-idvalidator-uin|NA|0.11.0|Used to validate the PRID format.
kernel-crypto-signature|NA|0.11.0|Used to validate the signature response from server.
kernel-templatemanager|NA|0.11.0|Used to generate the template manager.
kernel-qrcodegenerator-zxing|NA|0.11.0|Used to generate the QR code in acknowledgment page.
kernel-pdfgenerator-itext|NA|0.11.0|Used to scan the document in PDF format.
kernel-crypto-jce|NA|0.11.0|Used to encrypt the pre-redistaion data information.
kernel-jsonvalidator|NA|0.11.0|Used to validate the JSON.
kernel-virusscanner-clamav|NA|0.11.0|Used to communicate to the Antivirus Clam AV.
kernel-applicanttype-api|NA|0.11.0|Used to get the applicant types. 
kernel-masterdat-api|NA|0.11.0|Used to get the master data. 
kernel-notification-api|NA|0.11.0|Used to notify the user.
idrepo-service|NA|0.11.0|Used to save user details and uin in idrepository
pre-registration-datasync-api|NA|0.11.0|used to reverse datasync


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
MOS-21757|As the MOSIP system, I should be able to implement digital signature for auth/eKYC/OTP request|Sprint12|Kernel Java API needs to be converted to REST API


### 1.8 DB Changes :
Table modified: regprc.registration_list


Column Name|Description
-----------------|----------------------
Packet_checksum |Datatype : varchar(128)
Packet_size |Datatype : Datatype : bigint
Client_status_code |Datatype : varchar(36)
Client_status_comment |Datatype : varchar(256)
Additional_info |Datatype : bytearray




