**MOSIP Release Notes**
#### 2. Registration Client

#### 2.1 Introduction : <br><sub>This document highlights Sprint 10 features for Registration-Client that are released for QA phase.</sub></br>

#### 2.2 Release Summary : 
|         |          |
|----------|----------|
SubmittedBy|Omsai Eswar M
Date Raised | 05-APR-2019
Impact of Release|NA
Implementation Start Date |25-MAR-2019
Implementation end date	|05-APR-2019
Jenkins Build #	|Min Version : 1128 [Any later version also]
Objective & Scope of Release| Sprint10 & Scope: Refer the section 3
Acceptance Criteria	| Unit Testing and Code Coverage > 85%
Role/Job Title|Technical Lead
RFC(s) #|	NA


#### 2.3 Features Delivered : <br><sub>List of Features Delivered as part of this release should be listed here</sub></br>
Requirement ID | Requirement Type <br>(New\\Enhancement\\Defect)</br> | Description
-----|----------|-------------
MOS-22148 |New|Tech story--Digital Signature of the Response received by Client
MOS-22397|New|As the MOSIP Registration Client, <br>I should enforce security-related rules for first time login and sync</br>
MOS-22822, MOS-22810 |New|Tech story -Upload packet changes
MOS-21517|New|Stub--As MOSIP registration client, <br>for new registration of child capture any one biometric of the Parent/Guardian</br>
MOS-22841|New|Deletion of Pre-reg packet once RID is created.
MOS-18150|New|Tech Story - Secure the keys at Reg. client using TPM
MOS-22068|New|As the MOSIP Registration Client, <br>for new registration and update, I should enable upload of applicable documents
MOS-1279|New|Sync registration centre data</br>
MOS-13527,MOS-16120|New|Download and run the registration client software[Partially].
MOS-13560|New|Update UIN – proxy implementation
MOS-13561|New|Select packets to upload
MOS-13698,MOS-18089|New|Remap a machine from one centre to another
MOS-16040|New|Technical story – Integration: Mode of login to client
MOS-16109|New|Mark low quality biometrics as exceptions – proxy implementation 
MOS-16712|New|Technical story – Push packet to server as background process
MOS-17663,MOS-20253|New|Visual design changes
MOS-18117|New|Retrieve lost UIN – proxy implementation
MOS-19886|New|Authenticate registration with low quality biometrics – proxy implementation
MOS-20066|New|Validate demographic details contain no blacklisted words
MOS-20244|New|Technical story – UI validation using global parameters

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
MOS-22967|DONE|YES
MOS-22964|DONE|YES
MOS-22804|DONE|YES
MOS-22715|DONE|YES
MOS-22725|DONE|YES
MOS-22879|DONE|YES
MOS-22979|DONE|YES
MOS-22130|DONE|YES
MOS-23105|DONE|YES
MOS-23247|DONE|YES
MOS-23119|DONE|YES
MOS-23118|DONE|YES
MOS-23100|DONE|YES
MOS-22983|DONE|YES
MOS-22838|DONE|YES
MOS-22806|DONE|YES
MOS-22805|DONE|YES
MOS-22797|DONE|YES
MOS-22783|DONE|YES
MOS-22727|DONE|YES
MOS-22712|DONE|YES
MOS-22059|DONE|YES
MOS-22756|DONE|YES
MOS-22758|DONE|YES
MOS-22783|DONE|YES
MOS-22784|DONE|YES
MOS-22788|DONE|YES
MOS-22791|DONE|YES
MOS-22794|DONE|YES
MOS-22796|DONE|YES
MOS-22712|DONE|YES
MOS-22089|DONE|YES
MOS-22448|DONE|YES
MOS-22978|DONE|YES
MOS-22977|DONE|YES

#### 2.7 Features Pending : <br><sub>List of Features (Requirement) which are still pending at the time of this release for current sprint only, specifying details and date/sprint in which it would be released</sub></br>
Requirement Id|Description|Future Date / Sprint when expected to release | Reason
--------------|-----------|-----------|-------------





