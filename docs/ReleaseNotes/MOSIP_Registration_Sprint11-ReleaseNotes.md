# MOSIP Release Notes
## 1. Registration Client

### 1.1 Introduction : <br><sub>This document highlights Sprint 11 features for Registration-Client that are released for QA phase.</sub></br>

### 1.2 Release Summary : 
|         |          |
|----------|----------|
SubmittedBy|Omsai Eswar M
Date Raised | 15-MAY-2019
Impact of Release|NA
Implementation Start Date |26-APR-2019
Implementation end date	|13-MAY-2019
Jenkins Build #	|Min Version : 1435  [Any later version also]
Objective & Scope of Release| Sprint11 & Scope: Refer the section 3
Acceptance Criteria	| Unit Testing and Code Coverage > 85%
Role/Job Title|Technical Lead
RFC(s) #|	NA


### 1.3 Features Delivered : <br><sub>List of Features Delivered as part of this release should be listed here</sub></br>
Requirement ID | Requirement Type <br>(New\\Enhancement\\Defect)</br> | Description
-----|----------|-------------
MOS-67 |New|As the MOSIP registration client, I should be able to update the client software from the server
MOS-21461|New|Tech story - Send extra parameters related to the packet while Sync to registration- processor
MOS-22009 |New|As a Registration Officer, I should be able to search for packets on the EoD process page
MOS-21929|New|As a Registration Officer, I should be able to search for packets on the Upload page
MOS-21573|New|Tech story--Generate packet using center id and unique machine id
MOS-21470|New|Stub--As MOSIP registration client, for UIN Update of Child, capture the UIN, Name and one biometric of the Parent/Guardian
MOS-22405|New|Tech Story - Secure the keys at Reg. client using TPM part 2
MOS-16121|New|Stub - MOSIP Device Manager implementation - Part 2
MOS-23288|New|Capture exception photo of Parent if exception is marked for a child's new registration/UIN update



### 1.4 Prerequisites : <br><sub>Dependent module/component with their respective versions should be mentioned here</sub></br>
Module/Files|Component|Version|Description (If any)
-----|-------------|----------------|--------------
Java|JDK + JRE [Installed]|Main Version: Java 8 Min Version: 181|The Java Version is to be installed in the machine.
ZIP|Fat Jar + DB|Latest version|The ZIP contains the both fat jar and required the initial DB. 
[RXTXcomm.jar](https://github.com/mosip/mosip/tree/0.11.0/docs/registration-additional-files/RXTXcomm.jar)|NA|NA|Path: <br>C:\Program Files\Java\jre1.8.0_191\lib\ext </br> <br>Please copy the jar to the ‘JAVA_HOME\jre\lib\ext’ folder</br>
[rxtxcomm-2.2.jar](https://github.com/mosip/mosip/tree/0.11.0/docs/registration-additional-files/rxtxcomm-2.2.jar)|NA|NA|Path: <br>C:\Program Files\Java\jre1.8.0_191\lib\ext</br> <br>Please copy the jar to the ‘JAVA_HOME\jre\lib\ext’ folder</br>
[java.security](https://github.com/mosip/mosip/tree/0.11.0/docs/registration-additional-files/java.security)|NA|NA|Path: <br>C:\Program Files\Java\jre1.8.0_191\lib\security</br> <br>security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider</br> <br>Please add the above property to the ‘JAVA_HOME\jre\lib\security\java.security’ file</br>
[rxtxParallel.dll](https://github.com/mosip/mosip/tree/0.11.0/docs/registration-additional-files/rxtxParallel.dll)|NA|NA|Path: <br>C:\Program Files\Java\jre1.8.0_191\bin </br> <br>Please add the above property to the ‘JAVA_HOME\jre\lib\security\java.security’ file</br>
[rxtxSerial.dll](https://github.com/mosip/mosip/tree/0.11.0/docs/registration-additional-files/rxtxSerial.dll)|NA|NA|Path: <br>C:\Program Files\Java\jre1.8.0_191\bin</br> <br> Please copy the dll to the ‘JAVA_HOME\jre\bin’ file</br>
[local_policy.jar](https://github.com/mosip/mosip/tree/0.11.0/docs/registration-additional-files/local_policy.jar)|NA|NA|Path:C:\Program Files\Java\jre1.8.0_191  Please copy the dll to the ‘JAVA_HOME\jre\lib\security\policy\unlimited\’ file.</br>
[US_export_policy.jar](https://github.com/mosip/mosip/tree/0.11.0/docs/registration-additional-files/US_export_policy.jar)|NA|NA|Path:C:\Program Files\Java\jre1.8.0_191  Please copy the dll to the ‘JAVA_HOME\jre\lib\security\policy\unlimited\’ file.</br>
Clam AV |NA|NA|<br>Download the windows clam av antivirus by provided link and install the s\w.</br> <br>[https://www.clamav.net/downloads#otherversions]</br>
mosip-sw-0.11.2.zip|NA|NA|<br>Please unzip the file and execute the command to run the run.jar</br><br> **“java -jar run.jar”**</br>
Admin Configuration|NA|Latest Version|Admin has to setup the desired configuration for the registration-client.
kernel-core|NA|0.11.1|Basic core kernel packages.
kernel-logger-logback|NA|0.11.1|Use for the logging.
kernel-dataaccess-hibernate|NA|0.11.1|Used for the communicating to the DB.
kernel-auditmanager-api|NA|0.11.1|Used to audit the reocrds into the DB
kernel-idvalidator-rid|NA|0.11.1|Used to validate the RID format.
kernel-idvalidator-uin|NA|0.11.1|Used to validate the UIN format
kernel-idvalidator-prid|NA|0.11.1|Used to validate the PRID format
kernel-idgenerator-rid|NA|0.11.1|Used to Generate the RID.
kernel-crypto-signature|NA|0.11.1|Used to validate the signature response from server.
kernel-keygenerator-bouncycastle|NA|0.11.1|Used to generate the key pair for AES -256.
kernel-templatemanager-velocity|NA|0.11.1|Used to generate the template manager using the velocity
kernel-qrcodegenerator-zxing|NA|0.11.1|Used to generate the QR code in acknowledgment page.
kernel-pdfgenerator-itext|NA|0.11.1|Used to scan the document in PDF format.
kernel-crypto-jce|NA|0.11.1|Used to encrypt the packet information
kernel-jsonvalidator|NA|0.11.1|Used to validate the JSON.
kernel-virusscanner-clamav|NA|0.11.1|Used to communicate to the Antivirus Clam AV
kernel-transliteration-icu4j|NA|0.11.1|Used to transliterate the Arabic to French and vice versa.
kernel-applicanttype-api|NA|0.11.1|Used to get the applicant types 
kernel-cbeffutil-api|NA|0.11.1|Used to generate the CBEFF file and validate against the schema also.

### 1.5 Open Issues : <br><sub>List of Open Issues, which would be resolved or fixed in another release version, but same Sprint</sub></br>
Open Items|Description
-----------------|----------------------
Transliteration|English-Arabic Transliteration  won’t work because of non-availability of kernel library
Sonar Issues|Vulnerability – 10 : Due to sonar rule set upgradation.<br>java/io/File.<init>(Ljava/lang/String;)V reads a file whose location might be specified by user input Needs to be Addressed.</br>
MDM|Mosip Device Management partially implemented. It Span across the multiple sprints.
TPM|The application will work for only TPM 2.0. If machine having the TPM 1.2 version then disable the TPM from properties and test the application.


### 1.6 Defects list
Defect JIRA ID|Status|Availabilty in the branch
---------------|-------------|------------------
MOS-23733|FIXED|YES
MOS-23732|FIXED|YES
MOS-23125|FIXED|YES
MOS-22444|FIXED|YES
MOS-21573|FIXED|YES
MOS-23594|FIXED|YES
MOS-23595|FIXED|YES
MOS-23780|FIXED|YES
MOS-18317|FIXED|YES
MOS-23866|FIXED|YES
MOS-23872|FIXED|YES
MOS-23779|FIXED|YES
MOS-22405|FIXED|YES
MOS-22967|FIXED|YES
MOS-22854|FIXED|YES
MOS-13075|FIXED|YES
MOS-21461|FIXED|YES
MOS-23105|FIXED|YES
MOS-23752|FIXED|YES
MOS-23700|FIXED|YES
MOS-23698|FIXED|YES
MOS-23590|FIXED|YES
MOS-23346|FIXED|YES
MOS-23345|FIXED|YES
MOS-23164|FIXED|YES
MOS-23163|FIXED|YES
MOS-22978|FIXED|YES
MOS-22796|FIXED|YES
MOS-21470|FIXED|YES
MOS-21929|FIXED|YES
MOS-23123|FIXED|YES
MOS-22979|FIXED|YES
MOS-23124|FIXED|YES
MOS-23344|FIXED|YES
MOS-22009|FIXED|YES
MOS-23288|FIXED|YES
MOS-23735|FIXED|YES
MOS-23734|FIXED|YES
MOS-23512|FIXED|YES
MOS-22976|FIXED|YES
MOS-22783|FIXED|YES
MOS-22736|FIXED|YES
MOS-22060|FIXED|YES
MOS-21981|FIXED|YES
MOS-21939|FIXED|YES
MOS-18314|FIXED|YES
MOS-14139|FIXED|YES


### 1.7 Features Pending : <br><sub>List of Features (Requirement) which are still pending at the time of this release for current sprint only, specifying details and date/sprint in which it would be released</sub></br>
Requirement Id|Description|Future Date / Sprint when expected to release | Reason
--------------|-----------|-----------|-------------





