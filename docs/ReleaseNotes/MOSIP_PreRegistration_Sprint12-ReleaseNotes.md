**MOSIP Release Notes**
#### 1. Pre Registration

#### 1.1 Introduction : <br><sub>This document highlights Sprint 12 features for Pre-Registration that are released for QA phase.</sub></br>

#### 1.2 Release Summary : 
|         |          |
|----------|----------|
SubmittedBy|Ravi C Balaji
Date Raised | 29-MAY-2019
Impact of Release|NA
Implementation Start Date |20-MAY-2019
Implementation end date	|07-JUN-2019
Jenkins Build #	|Min Version : 1953  [Any later version also]
Objective & Scope of Release| Sprint12 & Scope: Refer the section 3
Acceptance Criteria	| Unit Testing and Code Coverage > 88%
Role/Job Title|Technical Lead
RFC(s) #|	NA


#### 1.3 Features Delivered : <br><sub>List of Features Delivered as part of this release should be listed here</sub></br>
Requirement ID | Requirement Type <br>(New\\Enhancement\\Defect)</br> | Description
-----|----------|-------------
MOS-22497 |New|Response signature for all services registration client
MOS-19821 |Enhancement|Demographic Service Pagination implementation
MOS-22495 |Enhancement|Auth service integration of Kernel
MOS-23331 |Technical Debts of FIT-4



#### 1.4 Prerequisites : <br><sub>Dependent module/component with their respective versions should be mentioned here</sub></br>
Module/Files|Component|Version|Description (If any)
-----|-------------|----------------|--------------
Java|JDK + JRE [Installed]|Main Version: Java 8 Min Version: 181|The Java Version is to be installed in the machine. 
kernel-core|NA|0.12.2|Basic core kernel packages.
kernel-logger-logback|NA|0.12.2|Use for the logging.
kernel-dataaccess-hibernate|NA|0.12.2|Used for the communicating to the DB.
kernel-auditmanager-api|NA|0.12.2|Used to audit the reocrds into the DB.
kernel-idgenerator-prid|NA|0.12.2|Used to generate the PRID format.
kernel-idvalidator-prid|NA|0.12.2|Used to validate the PRID format.
kernel-crypto-signature|NA|0.12.2|Used to validate the signature response from server.
kernel-templatemanager|NA|0.12.2|Used to generate the template manager.
kernel-qrcodegenerator-zxing|NA|0.12.2|Used to generate the QR code in acknowledgment page.
kernel-pdfgenerator-itext|NA|0.12.2|Used to scan the document in PDF format.
kernel-crypto-jce|NA|0.12.2|Used to encrypt the pre-redistaion data information.
kernel-jsonvalidator|NA|0.12.2|Used to validate the JSON.
kernel-virusscanner-clamav|NA|0.12.2|Used to communicate to the Antivirus Clam AV.
kernel-applicanttype-api|NA|0.12.2|Used to get the applicant types. 
kernel-masterdat-api|NA|0.12.2|Used to get the master data. 
kernel-notification-api|NA|0.12.2|Used to notify the user. 

#### 1.5 Open Issues : <br><sub>List of Open Issues, which would be resolved or fixed in another release version, but same Sprint</sub></br>
Open Items|Description
-----------------|----------------------
Dashboard API change |  Names without POA displaying in same as. MOS-24119


#### 1.6 Defects list
Defect JIRA ID|Status|Availabilty in the branch
---------------|-------------|------------------
MOS-24696|FIXED|YES
MOS-24693|FIXED|YES
MOS-24622|FIXED|YES
MOS-24463|FIXED|YES
MOS-24378|FIXED|YES
MOS-24333|FIXED|YES
MOS-24300|FIXED|YES
MOS-24247|FIXED|YES
MOS-24232|FIXED|YES
MOS-24112|FIXED|YES
MOS-24098|FIXED|YES
MOS-24095|FIXED|YES
MOS-23997|FIXED|YES
MOS-23989|FIXED|YES
MOS-23938|FIXED|YES
MOS-23903|FIXED|YES
MOS-23245|FIXED|YES
MOS-24028|FIXED|YES
MOS-23916|FIXED|YES
MOS-18849|FIXED|YES

#### 1.7 Features Pending : <br><sub>List of Features (Requirement) which are still pending at the time of this release for current sprint only, specifying details and date/sprint in which it would be released</sub></br>
Requirement Id|Description|Future Date / Sprint when expected to release | Reason
--------------|-----------|-----------|-------------





