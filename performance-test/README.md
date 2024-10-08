
### Contains
* This folder contains performance Test script of below API endpoint categories.
    01. Auth Token Generation (Setup)
    02. Create Identities in MOSIP Identity System (Setup)
    03. Third Party Certificates (Setup)
	04. S01 Authentication with OTP (Preparation)
	05. S02 Authentication with Biometrics (Preparation)
	06. S03 Authentication with Demo (Preparation)
	07. S01 Authentication with OTP (Execution)
	08. S02 Authentication with Biometrics (Execution)
	09. S03 Authentication with Demo (Execution)
	10. S04 EKYC with Biometrics (Execution)


* Open source Tools used,
    1. [Apache JMeter](https://jmeter.apache.org/)

### How to run performance scripts using Apache JMeter tool
* Download Apache JMeter from https://jmeter.apache.org/download_jmeter.cgi
* Download scripts for the required module.
* Start JMeter by running the jmeter.bat file for Windows or jmeter file for Unix. 
* Validate the scripts for one user.
* Execute a dry run for 10 min.
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

### Procedure to install and execute auth-demo-service in the local machine

* The following link provides installation of auth-demo-service 
	* https://github.com/mosip/mosip-functional-tests/blob/master/README.md
	
* Navigate to the path where auth-demo-service has been installed and run following query for cellbox1 env.
	*java -jar -Dmosip.base.url=https://api-internal.cellbox1.mosip.net -Dserver.port=8082 -Dauth-token-generator.rest.clientId=mosip-resident-client -Dauth-token-generator.rest.secretKey=abc@123 -Dauth-token-generator.rest.appId=resident authentication-demo-service-1.2.1-develop-SNAPSHOT.jar

### Execution points for eSignet Authentication API's

*IDA_Test_Script.jmx
	
	*Auth Token Generation (Setup): This thread contains Auth manager authentication API which will generate auth token value for Registration client. The token expires after 24 hours.
	
	* Create Identities in MOSIP Identity System (Setup) : This thread contains the authorization api's for regproc and idrepo from which the auth token will be generated. There is set of 4 api's generate RID, generate UIN, add identity and add VID. From here we will get the VID which can be further used as individual id. These 4 api's are present in the loop controller where we can define the number of samples for creating identities in which "addIdentitySetup" is used as a variable. 
	
	* Third Party Certificates (Setup) : This threadgroup contains series of certificate upload to support the IDA execution.
	
	* S01 Authentication with OTP (Preparation): This thread creates testdata like signature and request body for the Auth Send OTP and Authentication with OTP request which expires after 24 hours.
	
	* S02 Authentication with Biometrics (Preparation): This thread creates testdata like signature and request body for the Authentication with Biometric request which expires after 24 hours.
	
	* S03 Authentication with Demo (Preparation): This thread creates testdata like signature and request body for the Authentication with Demo request which expires after 24 hours.
	  			
	* S01 Authentication with OTP (Execution) :
		* S01 T01 Auth Send OTP : This thread sends OTP request.
		* S01 T02 Authentication with OTP : This thread performs OTP authentication.
		
	* S02 Authentication with Biometrics (Execution):
		* S02 T01 Authentication with Biometrics: This thread performs biometric authentication
	
	* S03 Authentication with Demo (Execution):
		* S03 T01 Authentication with Demo : This thread verifies the authentication.

	* S04 EKYC with Biometrics (Execution):
		* S04 T01 Get Record From IDRepo : This thread fetches record from the IDrepo.
		* S04 T02 Download CBEFFfile : This thread downloads cbeff file.
		* S04 T03 Create Auth Request UIN : This thread generates auth request with UIN number.
		* S04 T04 EKYC with Biometrics: This threads performs EKYC with biometric data.
 	
### Downloading Plugin manager jar file for the purpose installing other JMeter specific plugins

* Download JMeter plugin manager from below url links.
	*https://jmeter-plugins.org/get/

* After downloading the jar file place it in below folder path.
	*lib/ext

* Please refer to following link to download JMeter jars.
	https://mosip.atlassian.net/wiki/spaces/PT/pages/1227751491/Steps+to+set+up+the+local+system#PluginManager
		
### Designing the workload model for performance test execution
* Calculation of number of users depending on Transactions per second (TPS) provided by client

* Applying little's law
	* Users = TPS * (SLA of transaction + think time + pacing)
	* TPS --> Transaction per second.
	
* For the realistic approach we can keep (Think time + Pacing) = 1 second for API testing
	* Calculating number of users for 10 TPS
		* Users= 10 X (SLA of transaction + 1)
		       = 10 X (1 + 1)
			   = 20
			   
### Usage of Constant Throughput timer to control Hits/sec from JMeter
* In order to control hits/ minute in JMeter, it is better to use Timer called Constant Throughput Timer.

* If we are performing load test with 10TPS as hits / sec in one thread group. Then we need to provide value hits / minute as in Constant Throughput Timer
	* Value = 10 X 60
			= 600

* Dropdown option in Constant Throughput Timer
	* Calculate Throughput based on as = All active threads in current thread group
		* If we are performing load test with 10TPS as hits / sec in one thread group. Then we need to provide value hits / minute as in Constant Throughput Timer
	 			Value = 10 X 60
					  = 600
		  
	* Calculate Throughput based on as = this thread
		* If we are performing scalability testing we need to calculate throughput for 10 TPS as 
          Value = (10 * 60 )/(Number of users)
