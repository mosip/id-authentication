# MOSIP Release Notes
## 1. ID Authentication

### 1.1 Introduction : <br><sub>This document highlights Sprint 12 features for ID Authentication that are released for QA phase.</sub></br>

### 1.2 Release Summary : 
|         |          |
|----------|----------|
SubmittedBy|Loganathan Sekar
Date Raised | 17-JUNE-2019
Impact of Release|NA
Implementation Start Date |28-MAY-2019
Implementation end date	|07-JUN-2019
Jenkins Build #	|Min Version : 0.12.8.1612
Objective & Scope of Release| Sprint12 & Scope: Refer the section 3
Acceptance Criteria	| Unit Testing and Code Coverage > 90%
Role/Job Title|Technical Lead
RFC(s) #|	NA


### 1.3 Features Delivered : <br><sub>List of Features Delivered as part of this release should be listed here</sub></br>
Requirement ID | Requirement Type <br>(New\\Enhancement\\Defect)</br> | Description
-----|----------|-------------
MOS-23802|New|Modify Internal API for Biometric Authentication to support 10 fingerprints in the request
MOS-23242|New|As the MOSIP system, I should be able to implement digital signature for auth and eKYc response
MOS-21757|New|Tech Story - Integrate with CBEFF to return photo in KYC Reponse
MOS-15815|Integration|Tech Story - Authentication & Authorization
MOS-24075|New|As the MOSIP system - IDA, I should be able to integrate with VID service
MOS-23068|New|Integrate with Audit changes/OTP Request/auth changes
MOS-24076|New|Integrate with BioAPI, Integrate with IdObjectValidator


### 1.4 Prerequisites : <br><sub>Dependent module/component with their respective versions should be mentioned here</sub></br>
Module|Component|Version|Description (If any)
-----|-------------|----------------|--------------
ID Repository|ID Repository Identity Service|0.12.8|Get Identity for UIN, Get Identity for RID
ID Repository|ID Repository VID Service|0.12.8|Get UIN for VID
Kernel|Kernel-Audit Service|0.12.8| 
Kernel|Kernel OTP Validator Service|0.12.8|
Kernel|Kernel AuthManager Service|0.12.8|Send OTP, Get RID for UserID, Authenticate with ClientId-SecretKey, Validate Token
Kernel|Mail Notification Service|0.12.8|
Kernel|SMS Notification Service|0.12.8|
Kernel|Master Data Service|0.12.8|Titles, Gender, Templates
Kernel|Kernel Crypto Manager service|0.12.7|Encrypt, Decrypt
Kernel|Kernel Crypto Signature|0.12.8|Sign
Kernel|Kernel UIN Validator|0.12.8|Java API
Kernel|Kernel VID Validator|0.12.8|Java API
Kernel|Kernel Pin Validator|0.12.8|Java API
Kernel|ID Object Validator|0.12.8|Java API
Kernel|Kernel ID Generator – Token ID|0.12.8|Java API
Kernel|Kernel Crypto JCE|0.12.8|Java API - Encryptor
Kernel|Kernel Bio API Provider|0.12.8|Java API - Mock BioAPI provider

### 1.5 Open Issues : <br><sub>List of Open Issues, which would be resolved or fixed in another release version, but same Sprint</sub></br>
Open Items|Description
-----------------|----------------------

### 1.6 Defects list :
Defect JIRA ID|Status|Availabilty in the branch
---------------|-------------|------------------
MOS-25388|DONE|Yes
MOS-25383|DONE|Yes
MOS-25264|DONE|Yes
MOS-24329|DONE|Yes
MOS-24325|DONE|Yes
MOS-24348|DONE|Yes
MOS-25385|DONE|Yes
MOS-25644|DONE|Yes
MOS-25623|DONE|Yes
MOS-25527|DONE|Yes
MOS-25486|DONE|Yes
MOS-25485|DONE|Yes
MOS-25477|DONE|Yes
MOS-25480|DONE|Yes
MOS-23156|DONE|Yes
MOS-25487|DONE|Yes

### 1.7 Features Pending : <br><sub>List of Features (Requirement) which are still pending at the time of this release for current sprint only, specifying details and date/sprint in which it would be released</sub></br>
Requirement Id|Description|Future Date / Sprint when expected to release | Reason
--------------|-----------|-----------|-------------
|||


### 1.8 DB Changes :
|DB Script Name|Description|
|---------------|-------------|
|ida-static_pin.sql|Removed ida.static_pin table|
|ida-static_pin_h.sql|Removed ida.static_pin_h table|




