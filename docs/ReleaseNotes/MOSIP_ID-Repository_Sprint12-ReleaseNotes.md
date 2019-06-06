# MOSIP Release Notes
## 1. ID Repository

### 1.1 Introduction : <br><sub>This document highlights Sprint 12 features for ID Repository that are released for QA phase.</sub></br>

### 1.2 Release Summary : 
|         |          |
|----------|----------|
SubmittedBy|Manoj SP
Date Raised | 06-JUN-2019
Impact of Release|NA
Implementation Start Date |20-MAY-2019
Implementation end date	|07-JUN-2019
Jenkins Build #	|Min Version : 322  [Any later version also]
Objective & Scope of Release| Sprint12 & Scope: Refer the section 3
Acceptance Criteria	| Unit Testing and Code Coverage > 90%
Role/Job Title|Module Lead
RFC(s) #|	NA


### 1.3 Features Delivered : <br><sub>List of Features Delivered as part of this release should be listed here</sub></br>
Requirement ID | Requirement Type <br>(New\\Enhancement\\Defect)</br> | Description
-----|----------|-------------
MOS-23069|Enhancement|As the MOSIP system, I should be able to store and retrieve UIN securely
MOS-23314|Enhancement|As the MOSIP system, I should be able create identity data in ID-Repo - Part II
MOS-23315|Enhancement|As the MOSIP system, I should be able update identity data in ID-Repo - Part II
MOS-22930|Enhancement|As the MOSIP system, I should be able retrieve identity data in ID-Repo - Part II


### 1.4 Prerequisites : <br><sub>Dependent module/component with their respective versions should be mentioned here</sub></br>
Module|Component|Version|Description (If any)
-----|-------------|----------------|--------------
Kernel|Kernel-Audit Service|0.12.5| 
Kernel|Kernel AuthManager Service|0.12.5|Send OTP, Get RID for UserID, Authenticate with ClientId-SecretKey, Validate Token
Kernel|Kernel Crypto Manager service|0.12.5|Encrypt, Decrypt
Kernel|Kernel ID Validator - UIN|0.12.5|Java API
Kernel|Kernel ID Validator - VID|0.12.5|Java API
Kernel|Kernel ID Generator – VID|0.12.5|Java API
Kernel|Kernel ID Generator – Token ID|0.12.5|Java API
Kernel|Kernel cbeffutil api|0.12.5|Java API
Kernel|Kernel fsadapter hdfs|0.12.5|Java API
Kernel|Kernel idobjectvalidator|0.12.5|Java API
Kernel|Kernel logger logback|0.12.5|Java API

### 1.5 Open Issues : <br><sub>List of Open Issues, which would be resolved or fixed in another release version, but same Sprint</sub></br>
Open Items|Description
-----------------|----------------------
MOS-24315|Receiving Invalid Request for vid create with invalid requestTime


### 1.6 Defects list :
Defect JIRA ID|Status|Availabilty in the branch
---------------|-------------|------------------
MOS-24350|Done|YES
MOS-24342|Done|YES


### 1.7 Features Pending : <br><sub>List of Features (Requirement) which are still pending at the time of this release for current sprint only, specifying details and date/sprint in which it would be released</sub></br>
Requirement Id|Description|Future Date / Sprint when expected to release | Reason
--------------|-----------|-----------|-------------


### 1.8 DB Changes :
|DB Script Name|Description|
|---------------|-------------|

