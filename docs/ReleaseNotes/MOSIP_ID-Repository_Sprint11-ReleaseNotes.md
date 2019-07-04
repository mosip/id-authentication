# MOSIP Release Notes
## 1. ID Repository

### 1.1 Introduction : <br><sub>This document highlights Sprint 11 features for ID Repository that are released for QA phase.</sub></br>

### 1.2 Release Summary : 
|         |          |
|----------|----------|
SubmittedBy|Manoj SP
Date Raised | 16-MAY-2019
Impact of Release|NA
Implementation Start Date |26-APR-2019
Implementation end date	|17-MAY-2019
Jenkins Build #	|Min Version : 156  [Any later version also]
Objective & Scope of Release| Sprint11 & Scope: Refer the section 3
Acceptance Criteria	| Unit Testing and Code Coverage > 90%
Role/Job Title|Module Lead
RFC(s) #|	NA


### 1.3 Features Delivered : <br><sub>List of Features Delivered as part of this release should be listed here</sub></br>
Requirement ID | Requirement Type <br>(New\\Enhancement\\Defect)</br> | Description
-----|----------|-------------
MOS-23506|Enhancement|ID Validator and IdRepo Changes
MOS-23408|New|As the MOSIP system, I should be able to maintain the appropriate status of a VID based on the attribute value of a VID
MOS-23409|New|As the MOSIP system, I should be able to retrieve the UIN corresponding to a VID
MOS-23405|New|As the MOSIP system, I should be able to create VID in the defined policy
MOS-23406|New|As the MOSIP system, I should be able to revoke a VID based on the type


### 1.4 Prerequisites : <br><sub>Dependent module/component with their respective versions should be mentioned here</sub></br>
Module|Component|Version|Description (If any)
-----|-------------|----------------|--------------
Kernel|Kernel-Audit Service|0.11.1| 
Kernel|Kernel AuthManager Service|0.11.1|Send OTP, Get RID for UserID, Authenticate with ClientId-SecretKey, Validate Token
Kernel|Kernel Crypto Manager service|0.11.1|Encrypt, Decrypt
Kernel|Kernel ID Validator - UIN|0.11.1|Java API
Kernel|Kernel ID Validator - VID|0.11.1|Java API
Kernel|Kernel ID Generator – VID|0.11.1|Java API
Kernel|Kernel ID Generator – Token ID|0.11.1|Java API
Kernel|Kernel cbeffutil api|0.11.1|Java API
Kernel|Kernel fsadapter hdfs|0.11.1|Java API
Kernel|Kernel idobjectvalidator|0.11.1|Java API
Kernel|Kernel logger logback|0.11.1|Java API

### 1.5 Open Issues : <br><sub>List of Open Issues, which would be resolved or fixed in another release version, but same Sprint</sub></br>
Open Items|Description
-----------------|----------------------
MOS-23506|kernel IdObjectValidator API validations against masterdata is not yet completed


### 1.6 Defects list :
Defect JIRA ID|Status|Availabilty in the branch
---------------|-------------|------------------
MOS-22802|Reopened|YES


### 1.7 Features Pending : <br><sub>List of Features (Requirement) which are still pending at the time of this release for current sprint only, specifying details and date/sprint in which it would be released</sub></br>
Requirement Id|Description|Future Date / Sprint when expected to release | Reason
--------------|-----------|-----------|-------------
MOS-23506|Enhancement|ID Validator and IdRepo Changes


### 1.8 DB Changes :
|DB Script Name|Description|
|---------------|-------------|
|idrepo-uin.sql|BiometricReferenceID has been added to uin table|
|idrepo-uin_h.sql|BiometricReferenceID has been added to uin history table|
|idrepo-uin_encrypt_salt.sql|uinEncryptionSalt table has been added|
|idrepo-uin_hash_salt.sql|uinHashSalt table has been added|
|mosip_idmap_db.sql|idmap db has been added|
|mosip_idmap_ddl_deploy.sql|VID Service tables has been added|

