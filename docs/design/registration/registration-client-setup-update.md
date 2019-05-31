**Registration Client - Setup and Update:** 
***

This document covers the design of 'Registration client' application initial setup and Update process.  

**Setup Zip Creation:**  

   A class should be created with 'ClientJarEncryption' name inside the 'registration-lib' project to create a initial Zip folder that will contain:
   1. The required folder structures.  
   2. Encrypted version of 'mosip-client.jar', 'mosip-services.jar' and 'mosip-mdm-service.jar' files using the key.  
   3. props - should contain the JFrog repo location, environment name, db and application key.  
   4. db - Embed the initial db inside this folder, which will have all the required tables and few kernel insert scripts.  
   5. jre - Specific Java runtime environment (1.8.0_181) should be embedded.  
      - required rxtx jar and the respective dll should be loaded here.  
      - required certificate should be loaded here.  
   6 run.jar - This should have the classes to intiate the download process from JFrog repo, decrypt the application binaries and launch the application.   

**Build Process:**  
   During build process of 'registraion-lib' project the pom.xml file should be updated with inclusion 'io.mosip.registration.cipher.ClientJarEncryption' file to prepare the Initial Setup Zip file.  
  
**Initial Lib download:**  
   A class should be created with the name of 'ClientJarDecryption' and that will have the following process:
   1. Connect to TPM device and fetch public key. 
   2. Use the TPM public key to encrypt the application and db key.  
   3. Connect to JFrog repository and download the 'maven-metadata.xml' file and read the latest version.
   4. Using latest version, download the relevant 'MANIFEST.MF' file and capture the jar names provided in the file.  
   5. Initiate the supportive jar download process from the JFrog repository based on the 'MANIFEST.MF' file.  
   6. Then decrypt the 'mosip-client.jar' and 'mosip-services.jar' file using the key available in property file.  
   7. Set the 'classpath' and run the application 'mosip-client.jar' jar. 
  
**Application Update:**  
   1. During startup of an application, connect to the remote repository and check for the latest version from 'maven-metadata.xml' file. 
   2. If version mismatch between the local and remote then display the message as 'Update available, Do you want to download?'. 
   3. If yes, then connect to the remote repository and initiate the download process. (take the backup of current version of jars)  
   4. If no, then update the database with update 'yes' flag.  
   5. Once the application launched, then check for the update flag status in db. If update flag 'yes' is available for more than the number of configured days then don't allow the user to proceed until complete the 'update' process.  
   6. If there is any db update available then run the respective script from the 'resource/sql' folder.  

**Application Rollback:**  
   1. Application rollback to the previous version is not possible to through manual process. 
   2. If there is any error in application during update process, then reverse the application with previous version along with the db script.  

**Class Diagram [Initial Setup and Update Process]:**

![Initial Setup and Update Process](_images/registration/application-setup-update_class.png)  
