# MOSIP Release Notes
## 1. Registration Client

### 1.1 Introduction : <br><sub>This document highlights Sprint 12 features for Registration-Client that are released for QA phase.</sub></br>

### 1.2 Release Summary : 
|         |          |
|----------|----------|
SubmittedBy|Omsai Eswar M
Date Raised | 28-MAY-2019
Impact of Release|NA
Implementation Start Date |20-MAY-2019
Implementation end date	|31-MAY-2019
Jenkins Build #	|Min Version : 1767  [Any later version also]
Objective & Scope of Release| Sprint12 & Scope: Refer the section 3
Acceptance Criteria	| Unit Testing and Code Coverage > 85%
Role/Job Title|Technical Lead
RFC(s) #|	NA


### 1.3 Features Delivered : <br><sub>List of Features Delivered as part of this release should be listed here</sub></br>
Requirement ID | Requirement Type <br>(New\\Enhancement\\Defect)</br> | Description
-----|----------|-------------
MOS-22010 |New|EoD Process : Add column for Registration Date.
MOS-24382|New|Implementing BA feedback comments
MOS-23904|New|Stub--As MOSIP registration client, for New registration and UIN Update of Child, allow face photo as an acceptable biometric for the Parent/Guardian when they do not have all fingerprints and both irises
MOS-21928|New|Renamed 'Re-register' to 'Notification for Re-registration
MOS-22012|New|Upload packet 1: Add column for Registration Date
MOS-24124|New|Signing the request and sync using TPM public key
MOS-23766|New|Bundling of JRE to the initial setup, and run the application (run.bat) by using of bundled JRE





### 1.4 Prerequisites : <br><sub>Dependent module/component with their respective versions should be mentioned here</sub></br>
Module/Files|Component|Version|Description (If any)
-----|-------------|----------------|--------------
ZIP|jar,DB, MDM, Props and JRE|0.12.5|The ZIP contains jar,DB, MDM, Props and JRE. 
Clam AV |NA|NA|<br>Download the windows clam av antivirus by provided link and install the s\w.</br> <br>[https://www.clamav.net/downloads#otherversions]</br>
mosip-sw-0.12.5.zip|NA|NA|<br>Please unzip the file and execute the run.bat</br><br> **“run.bat”**</br>
mdm_start.bat|NA|NA|<br>To start the MDM </br>
mdm_stop.bat|NA|NA|<br>To stop the MDM</br>
Admin Configuration|NA|Latest Version|Admin has to setup the desired configuration for the registration-client.
kernel-core|NA|0.12.5|Basic core kernel packages.
kernel-logger-logback|NA|0.12.5|Use for the logging.
kernel-dataaccess-hibernate|NA|0.12.5|Used for the communicating to the DB.
kernel-auditmanager-api|NA|0.12.5|Used to audit the reocrds into the DB
kernel-idvalidator-rid|NA|0.12.5|Used to validate the RID format.
kernel-idvalidator-uin|NA|0.12.5|Used to validate the UIN format
kernel-idvalidator-prid|NA|0.12.5|Used to validate the PRID format
kernel-idgenerator-rid|NA|0.12.5|Used to Generate the RID.
kernel-crypto-signature|NA|0.12.5|Used to validate the signature response from server.
kernel-keygenerator-bouncycastle|NA|0.12.5|Used to generate the key pair for AES -256.
kernel-templatemanager-velocity|NA|0.12.5|Used to generate the template manager using the velocity
kernel-qrcodegenerator-zxing|NA|0.12.5|Used to generate the QR code in acknowledgment page.
kernel-pdfgenerator-itext|NA|0.12.5|Used to scan the document in PDF format.
kernel-crypto-jce|NA|0.12.5|Used to encrypt the packet information
kernel-jsonvalidator|NA|0.12.5|Used to validate the JSON.
kernel-virusscanner-clamav|NA|0.12.5|Used to communicate to the Antivirus Clam AV
kernel-transliteration-icu4j|NA|0.12.5|Used to transliterate the Arabic to French and vice versa.
kernel-applicanttype-api|NA|0.12.5|Used to get the applicant types 
kernel-cbeffutil-api|NA|0.12.5|Used to generate the CBEFF file and validate against the schema also.
kernel-bioapi-provider|NA|0.12.5|Used to integrate for the user-onboarding.

### 1.5 Open Issues : <br><sub>List of Open Issues, which would be resolved or fixed in another release version, but same Sprint</sub></br>
Open Items|Description
-----------------|----------------------
Transliteration|English-Arabic Transliteration  won’t work because of non-availability of kernel library
Bio-API|Integration with Bio-API for user-onboarding still in-progress.

### 1.6 Defects list
Defect JIRA ID|Status|Availabilty in the branch
---------------|-------------|------------------
MOS-24402|FIXED|YES
MOS-24422|FIXED|YES
MOS-24429|FIXED|YES
MOS-24543|FIXED|YES
MOS-24443|FIXED|YES
MOS-24403|FIXED|YES
MOS-24234|FIXED|YES
MOS-24377|FIXED|YES
MOS-24373|FIXED|YES
MOS-24442|FIXED|YES
MOS-14575|FIXED|YES
MOS-24775|FIXED|YES
MOS-24788|FIXED|YES
MOS-24448|FIXED|YES
MOS-25514|FIXED|YES
MOS-25517|FIXED|YES
MOS-25474|FIXED|YES
MOS-25473|FIXED|YES
MOS-25472|FIXED|YES
MOS-25471|FIXED|YES
MOS-24772|FIXED|YES
MOS-25491|FIXED|YES
MOS-25492|FIXED|YES
MOS-25490|FIXED|YES
MOS-25504|FIXED|YES
MOS-25478|FIXED|YES
MOS-24727|FIXED|YES
MOS-25469|FIXED|YES
MOS-25470|FIXED|YES
MOS-25493|FIXED|YES
MOS-25511|FIXED|YES
MOS-25495|FIXED|YES
MOS-25494|FIXED|YES

### 1.7 Features Pending : <br><sub>List of Features (Requirement) which are still pending at the time of this release for current sprint only, specifying details and date/sprint in which it would be released</sub></br>
Requirement Id|Description|Future Date / Sprint when expected to release | Reason
--------------|-----------|-----------|-------------






