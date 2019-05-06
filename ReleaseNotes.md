**MOSIP Release Notes**
#### 2. Registration Client

#### 2.1 Features Delivered
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

#### 2.2 Prerequisites
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




