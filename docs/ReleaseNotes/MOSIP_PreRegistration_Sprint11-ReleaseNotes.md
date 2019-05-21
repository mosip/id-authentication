**MOSIP Release Notes**
#### 1. Pre Registration

#### 1.1 Introduction : <br><sub>This document highlights Sprint 11 features for Pre-Registration that are released for QA phase.</sub></br>

#### 1.2 Release Summary : 
|         |          |
|----------|----------|
SubmittedBy|Ravi C Balaji
Date Raised | 15-MAY-2019
Impact of Release|NA
Implementation Start Date |26-APR-2019
Implementation end date	|13-MAY-2019
Jenkins Build #	|Min Version : 1732  [Any later version also]
Objective & Scope of Release| Sprint11 & Scope: Refer the section 3
Acceptance Criteria	| Unit Testing and Code Coverage > 85%
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
ZIP|Fat Jar + DB|Latest version|The ZIP contains the both fat jar and required the initial DB. 
kernel-core|NA|0.11.0|Basic core kernel packages.
kernel-logger-logback|NA|0.11.0|Use for the logging.
kernel-dataaccess-hibernate|NA|0.11.0|Used for the communicating to the DB.
kernel-auditmanager-api|NA|0.11.0|Used to audit the reocrds into the DB.
kernel-idgenerator-prid|NA|0.11.0|Used to generate the PRID format.
kernel-idvalidator-prid|NA|0.11.0|Used to validate the PRID format.
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

#### 1.5 Open Issues : <br><sub>List of Open Issues, which would be resolved or fixed in another release version, but same Sprint</sub></br>
Open Items|Description
-----------------|----------------------
Demographic Service | Demographic master data validation
Audit login and logout| After login and logout system is not audit anything in audit table.  MOS-23793
langCode validation | Invalid HTTP Status code,error code,error message when user tries to send request  by passing invalid value for langCode key of Create Pre-Registration API. MOS-12023
File Upload Bug	| Same as is coming for the first applicant also. View File index is coming as 2, only 1 file is uploaded. OR is not displaying in French. MOS-24028 , When Same as is selected in document upload user should be able to view uploaded file, Names without POA displaying in same as, When name selected without POA automatically next name is selected. MOS-23912
File Preview | preview of images are displayed in document upload
Consent template	| Consent should show actual data as per configured template. MOS-23916
emailID field validation | User is not getting any error code or error message by entering the invalid value for emailID field. MOS-18849
Appointment Dates | User is able to book the Appointment for ld dates i.e., date less than today date. MOS-12161
preregistration id validation | Mismatch in the error code and error message by passing invalid value for preregistration id. MOS-23221
Alignment issue | Alignment issue in preview page. MOS-23879 


#### 1.6 Defects list
Defect JIRA ID|Status|Availabilty in the branch
---------------|-------------|------------------
MOS-23818|FIXED|YES
MOS-23758|FIXED|YES
MOS-23108|FIXED|YES
MOS-22496|FIXED|YES
MOS-21769|FIXED|YES
MOS-21644|FIXED|YES
MOS-21434|FIXED|YES
MOS-23759|FIXED|YES
MOS-23727|FIXED|YES
MOS-23720|FIXED|YES
MOS-23282|FIXED|YES
MOS-23271|FIXED|YES
MOS-23238|FIXED|YES
MOS-23237|FIXED|YES
MOS-23107|FIXED|YES
MOS-23087|FIXED|YES
MOS-23086|FIXED|YES
MOS-22762|FIXED|YES
MOS-18213|FIXED|YES
MOS-18088|FIXED|YES
MOS-17478|FIXED|YES
MOS-13672|FIXED|YES
MOS-23726|FIXED|YES
MOS-23724|FIXED|YES
MOS-23717|FIXED|YES
MOS-23244|FIXED|YES
MOS-23241|FIXED|YES
MOS-23240|FIXED|YES
MOS-23222|FIXED|YES
MOS-21367|FIXED|YES
MOS-18566|FIXED|YES
MOS-15377|FIXED|YES
MOS-14242|FIXED|YES
MOS-14127|FIXED|YES
MOS-14125|FIXED|YES
MOS-14102|FIXED|YES
MOS-14052|FIXED|YES
MOS-14051|FIXED|YES
MOS-12851|FIXED|YES
MOS-12843|FIXED|YES
MOS-10881|FIXED|YES
MOS-10876|FIXED|YES
MOS-23725|FIXED|YES
MOS-18366|FIXED|YES
MOS-23888|FIXED|YES

#### 1.7 Features Pending : <br><sub>List of Features (Requirement) which are still pending at the time of this release for current sprint only, specifying details and date/sprint in which it would be released</sub></br>
Requirement Id|Description|Future Date / Sprint when expected to release | Reason
--------------|-----------|-----------|-------------





