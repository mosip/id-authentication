
### Contains
* This folder contains performance Test script of below API endpoint categories.
    01. Auth Token Generation (Setup)
    02. Create Identities in MOSIP Identity System (Setup)
    03. Third Party Certificates (Setup)
	04. S01 Authentication with OTP (Execution)
	05. S02 Authentication with Biometrics (Execution)
	06. S03 Authentication with Demo (Execution)
	07. S04 EKYC with Biometrics (Execution)

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

**Note - Before creating certificates we need to assign default role as AUTH_PARTNER in the keycloak for the environment we are using so that all new users with the same partner id which we will create will automatically have the desired role which it needs**

### Execution points for eSignet Authentication API's

*IDA_Test_Script.jmx
	
	*Auth Token Generation (Setup): This thread conatins Auth manager authentication API which will generate auth token value for Registration client. 
	
	* Create Identities in MOSIP Identity System (Setup) : This thread contains the authorization api's for regproc and idrepo from which the auth token will be generated. There is set of 4 api's generate RID, generate UIN, add identity and add VID. From here we will get the VID which can be further used as individual id. These 4 api's are present in the loop controller where we can define the number of samples for creating identities in which "addIdentitySetup" is used as a variable. 
	
	* Third Party Certificates (Setup) : This threadgroup generates certificates to support the IDA execution.
	  			
	* S01 Authentication with OTP (Execution) :
		* S01 T01 Create Auth OTP Request Body : This thread creates OTP request.
		* S01 T02 Auth Send OTP : This thread sends OTP request.
		* S01 T03 Create Auth Request UIN : This thread accepts OTP number.
		* S01 T04 Authentication with OTP : This thread performs OTP authentication.
		
	* S02 Authentication with Biometrics (Execution):
		* S02 T01 Get Record From IDRepo : This thread fetches record from the IDrepo.
		* S02 T02 Download CBEFFfile : This thread downloads cbeff file.
		* S02 T03 Create Auth Request UIN : This thread generates auth request with UIN number.
		* S02 T04 Authentication with Biometrics: This thread performs biometric authentication
	
	* S03 Authentication with Demo (Execution):
		* S03 T01 Get Record From ID Repo : This thread fetches record from the IDrepo.
		* S03 T02 Create Auth with Demo Request UIN : This thread creates authentication demo request with UIN number.
		* S03 T03 Authentication with Demo : This thread verifies the authentication.

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
