This module describes how to conduct load test for ID Authentication using provided JMeter script.

### Contains
* This directory contains performance Test script of below API endpoint categories.
    01. A01 Create Identities in MOSIP Identity System (Setup)
    02. A02 Third Party Certificates (Setup)
    03. P01 Authentication with OTP (Preparation)
    04. P02 Authentication with Biometrics (Preparation)
    05. P03 Authentication with Demo (Preparation)
    06. P04 EKYC with Biometrics (Preparation)
    07. S01 Authentication with OTP (Execution)
    08. S02 Authentication with Biometrics (Execution)
    09. S03 Authentication with Demo (Execution)
    10. S04 EKYC with Biometrics (Execution)

* Open source Tools used,
    01. [Apache JMeter 5.3](https://jmeter.apache.org/)
    02. [authentication-demo-service](https://github.com/mosip/mosip-functional-tests/blob/develop/authentication-demo-service/README.md) 
    03. [Java 11](https://jdk.java.net/archive/) (for JMeter 5.3)
    04. [Java 21](https://jdk.java.net/archive/) (for authentication-demo-service)

### How to run performance scripts using Apache JMeter tool
* Download Apache JMeter from https://jmeter.apache.org/download_jmeter.cgi
* Download JMeter Plugin Manager jar file from https://jmeter-plugins.org/get/ , and install by placing the it in "Jmeter/apache-jmeter-X.X.X/lib/ext"
* Download scripts for the required module from the [script](script/) folder of this repo.
* Start JMeter 5.3 (while running java 11) by running jmeter.bat/jmeter.sh as per your OS. 
* Load downloaded *.jmx script onto JMeter. If prompted, install the required plugins.
* If plugins were installed, restart JMeter.
* Update "User Defined Variables" within the JMeter scripts. This list holds environment endpoint URL, protocols, users, secret keys, passwords, runtime file path, support file path etc.
* Validate the scripts is working functionally. 
    * Disable all "thread groups" within the test plan by clicking 'disable'. 
    * Enable and execute only one thread at one time during this step.  
    * Sequentially, execute each thread group with Number of VUser and Iteration set to 1. 
    * Go to [script execution steps](#script-execution-steps) for further detail.
* Take average scenario response time obtained from above test and update "Scenario Response time" column in [MOSIP_TPS_Thread_setting_calculator](MOSIP_TPS_Thread_setting_calculator-ID-Authentication.xlsx)
* Execute a dry run for 10 min. The execution duration is controlled by "testDuration" variable.
* Use [MOSIP_TPS_Thread_setting_calculator](MOSIP_TPS_Thread_setting_calculator-ID-Authentication.xlsx) to calculate the thread settings required for your target load.
* Execute performance run with various loads in order to achieve targeted NFR's. For a performance run, all scenarios (S01, S02, S03....) should be enabled and executed at the same time.

### Setup points for Execution

* In keycloak, 
    1. Assign default role as AUTH_PARTNER in the test environment. This way all new users with the same partner id created from the JMeter script will automatically have the desired role. This will also create certificates in step 'A02 Third Party Certificates' with the same role.
    2. Create new keycloak user manually and assign it to Partner Admin Access role. Use this new user in JMeter as 'User Defined Variables' 
    
* This script uses [authentication-demo-service](https://github.com/mosip/mosip-functional-tests/blob/develop/authentication-demo-service/README.md). Below are setup steps of `authentication-demo-service` for the purpose of this module.
    1. Follow the instruction in the documentation to install this service.
    4. Change Java version in the system to Java 21.
    5. Run below command from the installation folder to start the service.

```
*java -jar -Dmosip.base.url=https://<<test-environment-domain>> -Dserver.port=8082 -Dauth-token-generator.rest.clientId=mosip-resident-client -Dauth-token-generator.rest.secretKey=<<testSecretKey>> -Dauth-token-generator.rest.appId=resident authentication-demo-service-<<version-number>>.jar
```
* The `authentication-demo-service` should be running in the background during preparation and execution of the test.
    

### Execution points for ID Authentication API's

*IDA_Test_Script.jmx
        
    
    * A01 Create Identities in MOSIP Identity System (Setup) : This threadgroup contains the authorization api's for regproc and idrepo from which the auth token will be generated. There is set of 4 api's generate RID, generate UIN, add identity and add VID. From here we will get the VID which can be further used as individual id. These 4 api's are present in the loop controller where we can define the number of samples for creating identities in which "freshIdentityCreationCount" is used as a variable. 
    
    * A02 Third Party Certificates (Setup) : This threadgroup contains series of certificates upload to support the IDA execution. it requires an empty "authcerts" folder in `authentication-demo-service` working directory. 
            * Setup Ida Certificates to Utility: This transaction controller generates IDA certificates.
            * Creating Policy And Policy Group : This transaction controller creates and publish policy and policy group. The policy ID is the random number provided by user. The ID is not fetched from environment database.
            * Registering The Relying Partner : This transaction controller generates partner id for the relying partner.
            * Relying Party Keycloak User And Api Key Generation : This transaction controller generates keycloak user and API key for relying partner.
            * Create Misp Partner And Misp License Key :  This transaction controller generates partner ID and license key for MISP partner.
            * Setup Device Partner : This transaction controller generates certificate for device partner.
            * Setup FTM Partner : This transaction controller generates certificate for FTM partner.

    Note: Enable "Load MISP Licensekey From File", "Load UIN From File" and "Load Relying Party API Key From File" before running below thread groups.

    * P01 Authentication with OTP (Preparation): This threadgroup creates testdata like signature and request body for the Auth Send OTP and Authentication with OTP request which expires after 24 hours.
    
    * P02 Authentication with Biometrics (Preparation): This threadgroup creates testdata like signature and request body for the Authentication with Biometric request which expires after 24 hours.
    
    * P03 Authentication with Demographics (Preparation): This threadgroup creates testdata like signature and request body for the Authentication with Demographics request which expires after 24 hours.

    * P04 EKYC with Biometrics (Preparation): This threadgroup creates testdata like signature and request body for the Authentication with Biometrics request which expires after 24 hours.

    * S01 Authentication with OTP (Execution) :
        * S01 T01 Auth Send OTP : This API endpoint sends OTP request.
        * S01 T02 Authentication with OTP : This API endpoint performs OTP authentication.
        
    * S02 Authentication with Biometrics (Execution):
        * S02 T01 Authentication with Biometrics: This API endpoint performs biometric authentication
    
    * S03 Authentication with Demographics (Execution):
        * S03 T01 Authentication with Demographics : This API endpoint performs the authentication with Demographics.

    * S04 EKYC with Biometrics (Execution):
        * S04 T01 EKYC with Biometrics: This API endpoint performs EKYC with biometric data.
 
        
### Designing the workload model for performance test execution

* The script is preconfigured for 100 tps within our test environment. Performance may vary based on hardware and infrastructure settings.

* If you are testing for different tps or with different hardware settings, adjustment needs to made to thread group settings within the script.

* [MOSIP_TPS_Thread_setting_calculator](MOSIP_TPS_Thread_setting_calculator-ID-Authentication.xlsx) applies Little's law to recommend required thread settings inputs.

### Support files required for this test execution:

1. [add_identity_request_details.csv](support-files/add_identity_request_details.csv) - This support file contains sample list of data that is used to create new identities. 
2. [biometrics_data.txt](support-files/biometrics_data.txt) - This support file contains sample encrypted bio data
3. [center_machine_id_values.csv](support-files/center_machine_id_values.csv) - This support file contains center and machine ids available in master database.
4. [face_data.txt](support-files/face_data.txt) - This support file contains sample encrypted face data that is used prepare and verify bio-authentication.
5. uin_list_ida.txt - This is a list of UINs and DoB, created by 'A01 Create Identities in MOSIP Identity System'. 
6. misp_license_key.txt - MISP partner and MISP License detail, created by 'A02 Third Party Certificates'
7. rp_api_key.txt - Relying party keycloak user detail, created by 'A02 Third Party Certificates'

