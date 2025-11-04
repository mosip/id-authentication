
### Contains
* This folder contains performance Test script of below API endpoint categories.
    01. Create Identities in MOSIP Identity System (Setup)
    02. Third Party Certificates (Setup)
	03. S01 Authentication with OTP (Preparation)
	04. S02 Authentication with Biometrics (Preparation)
	05. S03 Authentication with Demo (Preparation)
	06. S01 Authentication with OTP (Execution)
	07. S02 Authentication with Biometrics (Execution)
	08. S03 Authentication with Demo (Execution)
	09. S04 EKYC with Biometrics (Execution)


* Open source Tools used,
    1. [Apache JMeter](https://jmeter.apache.org/)

### How to run performance scripts using Apache JMeter tool
* Download Apache JMeter from https://jmeter.apache.org/download_jmeter.cgi
* Download scripts for the required module.
* Start JMeter by running the jmeter.bat file for Windows or jmeter file for Unix. 
* Validate the scripts for one user.
* Execute a dry run for 10 min.
* Use MOSIP_TPS_Thread_setting_calculator.xlsx to calculate the thread settings required for your target load.
* Execute performance run with various loads in order to achieve targeted NFR's.

### Setup points for Execution

* We need some jar files which needs to be added in lib folder of jmeter, PFA dependency links for your reference : 

   * jmeter-plugins-manager-1.10.jar
      *<!-- https://mvnrepository.com/artifact/kg.apc/jmeter-plugins-manager -->
<dependency>
    <groupId>kg.apc</groupId>
    <artifactId>jmeter-plugins-manager</artifactId>
    <version>1.10</version>
</dependency>

   * jmeter-plugins-synthesis-2.2.jar
      * <!-- https://mvnrepository.com/artifact/kg.apc/jmeter-plugins-synthesis -->
<dependency>
    <groupId>kg.apc</groupId>
    <artifactId>jmeter-plugins-synthesis</artifactId>
    <version>2.2</version>
</dependency>

* id-authentication-default.properties: Update the value for the properties according to the execution setup. 	
		*authrequest.received-time-allowed.seconds=86400

**Note - Before creating certificates we need to assign default role as AUTH_PARTNER in the keycloak for the environment we are using so that all new users with the same partner id which we will create will automatically have the desired role which it needs**

**Note - Please make sure to create new keycloak user manually and assigning it to Partner Admin Access role**

### Procedure to install and execute auth-demo-service in the local machine

* The following link provides installation of auth-demo-service 
	* https://github.com/mosip/mosip-functional-tests/blob/master/README.md
	
* Navigate to the path where auth-demo-service has been installed and run following query for cellbox1 env.
	*java -jar -Dmosip.base.url=https://api-internal.cellbox1.mosip.net -Dserver.port=8082 -Dauth-token-generator.rest.clientId=mosip-resident-client -Dauth-token-generator.rest.secretKey=abc@123 -Dauth-token-generator.rest.appId=resident authentication-demo-service-1.2.1-develop-SNAPSHOT.jar

### Execution points for eSignet Authentication API's

*IDA_Test_Script.jmx
		
	
	* Create Identities in MOSIP Identity System (Setup) : This threadgroup contains the authorization api's for regproc and idrepo from which the auth token will be generated. There is set of 4 api's generate RID, generate UIN, add identity and add VID. From here we will get the VID which can be further used as individual id. These 4 api's are present in the loop controller where we can define the number of samples for creating identities in which "freshIdentityCreationCount" is used as a variable. 
	
	* Third Party Certificates (Setup) : This threadgroup contains series of certificates upload to support the IDA execution.
			* Setup Ida Certificates to Utility: This transaction controller generates IDA certificates.
			* Creating Policy And Policy Group : This transaction controller creates and publish policy and policy group. The policy ID is the random number provided by user. The ID is not fetched from environment database.
			* Registering The Relying Partner : This transaction controller generates partner id for the relying partner.
			* Relying Party Keycloak User And Api Key Generation : This transaction controller generates keycloak user and API key for relying partner.
			* Create Misp Partner And Misp License Key :  This transaction controller generates partner ID and license key for MISP partner.
			* Setup Device Partner : This transaction controller generates certificate for device partner.
			* Setup FTM Partner : This transaction controller generates certificate for FTM partner.
				
	* S01 Authentication with OTP (Preparation): This threadgroup creates testdata like signature and request body for the Auth Send OTP and Authentication with OTP request which expires after 24 hours.
	
	* S02 Authentication with Biometrics (Preparation): This threadgroup creates testdata like signature and request body for the Authentication with Biometric request which expires after 24 hours.
	
	* S03 Authentication with Demographics (Preparation): This threadgroup creates testdata like signature and request body for the Authentication with Demographics request which expires after 24 hours.
	  			
	* S01 Authentication with OTP (Execution) :
		* S01 T01 Auth Send OTP : This API endpoint sends OTP request.
		* S01 T02 Authentication with OTP : This API endpoint performs OTP authentication.
		
	* S02 Authentication with Biometrics (Execution):
		* S02 T01 Authentication with Biometrics: This API endpoint performs biometric authentication
	
	* S03 Authentication with Demographics (Execution):
		* S03 T01 Authentication with Demographics : This API endpoint performs the authentication with Demographics.

	* S04 EKYC with Biometrics (Execution):
		* S04 T01 EKYC with Biometrics: This API endpoint performs EKYC with biometric data.
 	
### Downloading Plugin manager jar file for the purpose installing other JMeter specific plugins

* Download JMeter plugin manager from below url links.
	*https://jmeter-plugins.org/get/

* After downloading the jar file place it in below folder path.
	*lib/ext

* Please refer to following link to download JMeter jars.
	https://mosip.atlassian.net/wiki/spaces/PT/pages/1227751491/Steps+to+set+up+the+local+system#PluginManager
		
### Designing the workload model for performance test execution

* The script is preconfigured for 100 tps within our test environement. Performance may vary based on hardware and infreastructure settings.

* If you are testing for different tps or with different hardware settings, adjustment needs to made to thread group settings within the script.

* `MOSIP_TPS_Thread_setting_calculator-packet_credential_processing.xlsx` applies Little's law to recommend required thread settings inputs.

### Support files required for this test execution:

1. add_identity_request_details.csv - This support file contains sample list of data that is used to create new identities. 
2. biometrics_data.txt - This support file contains sample excrypted bio data
3. center_machine_id_values.csv - This support file contains center and machine ids available in master database.
4. face_data.txt - This support file contains sample excrypted face data that is used prepare and verify bio-authentication.