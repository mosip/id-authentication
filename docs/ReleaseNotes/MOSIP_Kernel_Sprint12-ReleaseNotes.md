
# MOSIP Release Notes
## 1. Kernel

### 1.1 Introduction : <br><sub>This document highlights Sprint 12 features for Kernel that are released for QA phase.</sub></br>

### 1.2 Release Summary : 
|         |          |
|----------|----------|
SubmittedBy|Raj Jha
Date Raised | 29-MAY-2019
Impact of Release|NA
Implementation Start Date |20-MAY-2019
Implementation end date	|7-JUNE-2019
Jenkins Build #	|Min Version : 0.12.2.2206  / KeyManager: 0.12.2.2208
Objective & Scope of Release| Sprint12
Acceptance Criteria	| Unit Testing and Code Coverage > 89%
Role/Job Title|Technical Lead
RFC(s) #|	NA


### 1.3 Features Delivered : <br><sub>List of Features Delivered as part of this release.</sub></br>
Requirement ID | Requirement Type <br>(New\\Enhancement\\Defect)</br> | Description
-----|----------|-------------
MOS-23936|New|Use mosip.io cert for digital signature and store in SoftHSM
MOS-23713|New|As the MOSIP system, I should be able to store a public key against a Machine in Masterdata
MOS-23722|New|Update Masterdata Sync Service - Add input parameter for Key Index
--|New|kernel-bioapi-provider - 
--|New|kernel-idobjectvalidator - 




### 1.5 Prerequisites : <br><sub>Dependent module/component with their respective versions</sub></br>
Module|Component|Version|Description (If any)
-----|-------------|----------------|--------------
ID Repository|ID Repository Identity Service|0.12.3|Required for UIN based login to sendOtp


### 1.6 Prerequisites : <br><sub>Dependent DB/External applications and services</sub></br>
Dependency|Component|Tag/Version|Description (If any)
-----|--------------|----------------|----------------
DB|mosip_iam|0.12.3|Required for kernel-auth-service.
DB|mosip_master|0.12.3|Required for kernel-masterdata-service.
DB|mosip_audit|0.12.3|Required for kernel-audit-service.
DB|mosip_kernel/script|0.12.3|Required for all other kernel services.
LDAP|ApaacheDS|NA|Required for kernel-auth-service.[External Dependency Setup](https://github.com/mosip/mosip/wiki/Getting-Started#6-installing-external-dependencies-)
KeyStore|SoftHsm|NA|Required for kernel-keymanager-softhsm. [External Dependency Setup](https://github.com/mosip/mosip/wiki/Getting-Started#6-installing-external-dependencies-)
DFS|HDFS|NA|Required for kernel-fsadaptor-hdfs. [External Dependency Setup](https://github.com/mosip/mosip/wiki/Getting-Started#6-installing-external-dependencies-)
SMS Gateway|msg91.com|NA|Required for kernel-smsnotification-service. [External Dependency Setup](https://github.com/mosip/mosip/wiki/Getting-Started#6-installing-external-dependencies-)


### 1.7 Open Issues : <br><sub>List of Open Issues, which would be resolved or fixed in another release version</sub></br>
Requirement ID |Component|Description
-----------------|----------------------|----------------------
Sonar Issues|Vulnerability – 21 | Due to sonar rule set upgradation. Will be addressed during security checks and cleanup.
MOS-23576|UIN Service Swagger | JS file not loading completely on Kubernetes env.



### 1.8 Features Pending : <br><sub>List of Features (Requirement) which are still pending at the time of this release for current sprint only</sub></br>
Requirement Id|Description|Future Date / Sprint when expected to release | Reason
--------------|-----------|-----------|-------------



### 1.9 DB Changes :
|DB|Description|
|---------------|-------------|
| mosip_master/template|Template data to be updated for latest sendOtp changes|
| mosip_master/template_type|Template type data to be updated for latest sendOtp changes|
| mosip_master/machine|TPM publick key and index column added|





