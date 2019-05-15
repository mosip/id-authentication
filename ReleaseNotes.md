**MOSIP Release Notes**
#### 2. Registration Client

#### 2.1 Introduction : <br><sub>This document highlights Sprint 11 features for Registration-Client that are released for QA phase.</sub></br>

#### 2.2 Release Summary : 
|         |          |
|----------|----------|
SubmittedBy|Omsai Eswar M
Date Raised | 15-MAY-2019
Impact of Release|NA
Implementation Start Date |26-APR-2019
Implementation end date	|13-MAY-2019
Jenkins Build #	|Min Version : 1407  [Any later version also]
Objective & Scope of Release| Sprint11 & Scope: Refer the section 3
Acceptance Criteria	| Unit Testing and Code Coverage > 85%
Role/Job Title|Technical Lead
RFC(s) #|	NA


#### 2.3 Features Delivered : <br><sub>List of Features Delivered as part of this release should be listed here</sub></br>
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



#### 2.4 Prerequisites : <br><sub>Dependent module/component with their respective versions should be mentioned here</sub></br>
Module/Files|Component|Version|Description (If any)
-----|-------------|----------------|--------------
Java|JDK + JRE [Installed]|Main Version: Java 8 Min Version: 181|The Java Version is to be installed in the machine.
ZIP|Fat Jar + DB|Latest version|The ZIP contains the both fat jar and required the initial DB. 
RXTXcomm.jar|NA|NA|Path: <br>C:\Program Files\Java\jre1.8.0_191\lib\ext </br> <br>Please copy the jar to the ‘JAVA_HOME\jre\lib\ext’ folder</br>
rxtxcomm-2.2.jar|NA|NA|Path: <br>C:\Program Files\Java\jre1.8.0_191\lib\ext</br> <br>Please copy the jar to the ‘JAVA_HOME\jre\lib\ext’ folder</br>
java.security|NA|NA|Path: <br>C:\Program Files\Java\jre1.8.0_191\lib\security</br> <br>security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider</br> <br>Please add the above property to the ‘JAVA_HOME\jre\lib\security\java.security’ file</br>
rxtxParallel.dll|NA|NA|Path: <br>C:\Program Files\Java\jre1.8.0_191\bin </br> <br>Please add the above property to the ‘JAVA_HOME\jre\lib\security\java.security’ file</br>
rxtxSerial.dll|NA|NA|Path: <br>C:\Program Files\Java\jre1.8.0_191\bin</br> <br> Please copy the dll to the ‘JAVA_HOME\jre\bin’ file</br>
Clam AV |NA|NA|<br>Download the windows clam av antivirus by provided link and install the s\w.</br> <br>[https://www.clamav.net/downloads#otherversions]</br>
run.jar|NA|NA|<br>Please execute the command to run the shaded jar</br><br> **“java -Dfile.encoding=UTF-8 -Dspring.profiles.active=qa -jar <Fat_Jar_Name>.jar”**</br>
Admin Configuration|NA|Latest Version|Admin has to setup the desired configuration for the registration-client.

#### 2.5 Open Issues : <br><sub>List of Open Issues, which would be resolved or fixed in another release version, but same Sprint</sub></br>
Open Items|Description
-----------------|----------------------
Transliteration|English-Arabic Transliteration  won’t work because of non-availability of kernel library
Sonar Issues|Vulnerability – 10 : Due to sonar rule set upgradation.<br>java/io/File.<init>(Ljava/lang/String;)V reads a file whose location might be specified by user input Needs to be Addressed.</br>
All new user passwords were point to default password ‘mosip’| Still waiting for the URLs from Kernel, we are unable to integrate with SSHA.
User On-boarding|OTP communication is there. We will release this change by using the hot fix.
Reg-client S/W download and Update|Partially was done. Initially provided with the ZIP of fat jar and DB. It span across the multiple sprints.
MDM|Mosip Device Management partially implemented. It Span across the multiple sprints.
Signature changes|At present we implemented for the Sync services. RegProc/Pre-Reg services yet to implement this.
Child Registration & UIN Update|Due to packet meta info and ID json changes, <br>in this fit 4 partially completed. It might be spill over next.</br><br> [So the code will be merged separately will send later for this story]</br>

#### 2.6 Defects list
Defect JIRA ID|Status|Availabilty in the branch
---------------|-------------|------------------
MOS-22966|DONE|YES

#### 2.7 Features Pending : <br><sub>List of Features (Requirement) which are still pending at the time of this release for current sprint only, specifying details and date/sprint in which it would be released</sub></br>
Requirement Id|Description|Future Date / Sprint when expected to release | Reason
--------------|-----------|-----------|-------------





