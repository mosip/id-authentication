# MOSIP Release Notes
## ID Authentication

### 1.1 Introduction : <br><sub>This document highlights Sprint 11 features for ID Authentication that are released for QA phase.</sub></br>

### 1.2 Release Summary : 
|         |          |
|----------|----------|
SubmittedBy|Loganathan Sekar
Date Raised | 15-MAY-2019
Impact of Release|NA
Implementation Start Date |26-APR-2019
Implementation end date	|13-MAY-2019
Jenkins Build #	|Min Version : 1299  [Any later version also]
Objective & Scope of Release| Sprint11 & Scope: Refer the section 3
Acceptance Criteria	| Unit Testing and Code Coverage > 90%
Role/Job Title|Technical Lead
RFC(s) #|	NA


### 1.3 Features Delivered : <br><sub>List of Features Delivered as part of this release should be listed here</sub></br>
Requirement ID | Requirement Type <br>(New\\Enhancement\\Defect)</br> | Description
-----|----------|-------------
MOS-22933|New|Restructuring of ID Auth services.
MOS-23106|New|Encrypt KYC Response
MOS-17444|Enhancement|Incorporate review comments on configurations
MOS-21339|Integration|Integrate Kernel Authentication Fix for Auth token expiry error code.
MOS-18108|New|Add UserID as ID Type for Internal Auth.
MOS-21768|Integration|Integrate with Kernel Crypto Manager for decrypting HMAC and request data
MOS-17444|Integration|As the MOSIP System(IDA), I should be able to integrate with Kernel send OTP Service
MOS-21327|Integration|Integrate with VID Service.


### 1.4 Prerequisites : <br><sub>Dependent module/component with their respective versions should be mentioned here</sub></br>
Module|Component|Version|Description (If any)
-----|-------------|----------------|--------------
ID Repository|ID Repository Identity Service|0.11.1|Get Identity for UIN, Get Identity for RID
ID Repository|ID Repository VID Service|0.11.1|Get UIN for VID
Kernel|Kernel-Audit Service|0.11.1| 
Kernel|Kernel OTP Validator Service|0.11.1|
Kernel|Kernel AuthManager Service|0.11.1|Send OTP, Get RID for UserID, Authenticate with ClientId-SecretKey, Validate Token
Kernel|Mail Notification Service|0.11.1|
Kernel|SMS Notification Service|0.11.1|
Kernel|Master Data Service|0.11.1|Titles, Gender, Templates
Kernel|Kernel Crypto Manager service|0.11.1|Encrypt, Decrypt
Kernel|Kernel UIN Validator|0.11.1|Java API
Kernel|Kernel VID Validator|0.11.1|Java API
Kernel|Kernel Pin Validator|0.11.1|Java API
Kernel|Kernel ID Generator – VID|0.11.1|Java API
Kernel|Kernel ID Generator – Token ID|0.11.1|Java API
Kernel|Kernel Crypto Manager - KeyGenarator|0.11.1|Java API

### 1.5 Open Issues : <br><sub>List of Open Issues, which would be resolved or fixed in another release version, but same Sprint</sub></br>
Open Items|Description
-----------------|----------------------
MOS-24006|Kernel OTP Manager- Negative Scenarios in send OTP
MOS-24012|Kernel OTP Validator - error format is not per standard
MOS-24060|Kernel Services - 500 error code in response

### 1.6 Defects list :
Defect JIRA ID|Status|Availabilty in the branch
---------------|-------------|------------------
||

### 1.7 Features Pending : <br><sub>List of Features (Requirement) which are still pending at the time of this release for current sprint only, specifying details and date/sprint in which it would be released</sub></br>
Requirement Id|Description|Future Date / Sprint when expected to release | Reason
--------------|-----------|-----------|-------------
MOS-21757|As the MOSIP system, I should be able to implement digital signature for auth/eKYC/OTP request|Sprint12|Kernel Java API needs to be converted to REST API


### 1.8 DB Changes :
|DB Script Name|Description|
|---------------|-------------|
|         |Removed existing VID table as the VID service module is removed from IDA.|



