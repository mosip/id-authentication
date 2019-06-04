**MOSIP Release Notes**
#### 1. Pre Registration

#### 1.1 Introduction : <br><sub>This document highlights features for Pre-Registration that are released for Technoforte.</sub></br>

#### 1.2 Release Summary : 
|         |          |
|----------|----------|
SubmittedBy|Ravi C Balaji
Date Raised | 04-Jun-2019
Jenkins Build #	|Min Version : 2016  [Any later version also]
Objective & Scope of Release| Pre-registration module
Acceptance Criteria	| Unit Testing and Code Coverage > 85%
Role/Job Title|Technical Lead
RFC(s) #|	NA


#### 1.3 Features Delivered : <br><sub>List of Features Delivered as part of this release should be listed here</sub></br>
Requirement ID | Requirement Type <br>(New\\Enhancement\\Defect)</br> | Description
-----|----------|-------------
MOS-22497 |New|Response signature for all services registration client
MOS-19821 |Enhancement|Demographic Service Pagination implementation
MOS-22495 |Enhancement|Auth service integration of Kernel with ApaacheDS ldap. Please see the prerequisites for Ldap setup.
MOS-23506 |New| Id object validation 


#### 1.4 Prerequisites 
1. MOSIP configuration has been separated from source code, and moved to a different repository. The same has been updated in Getting started here :

    https://github.com/mosip/mosip/wiki/Getting-Started#1-getting-the-source-code-

    To use this release, please make sure to mirror this new repository and update all configurations in this newly mirrored repository. And also make sure to use the same tag of mosip-configuration repository(i.e. 0.12.6)

2. Please make sure all required kernel service and external depency setup is done.
https://github.com/mosip/mosip/wiki/Kernel-Dependencies


#### 1.5 Deployment

1. The existing schemas must be dropped and recreated. Scripts are there in Getting started page. </br>
2. Deploy the kernel module. 
https://github.com/mosip/mosip/blob/0.12.0/docs/ReleaseNotes/MOSIP_Kernel_Sprint12-ReleaseNotes.md </br>
3. In Kernel config server kubernetes yaml (deployment - confi-server-deployment-and-service.yml inside scripts/configuration), change git_url_env environment variable to the ssh URL of mosip-configuration repository that you have mirrored. <br/>
4. Deploy the Pre-registration module. 


#### 1.6 Prerequisites : <br><sub>Dependent module/component with their respective versions should be mentioned here</sub></br>
Module/Files|Component|Version|Description (If any)
-----|-------------|----------------|--------------
Java|JDK + JRE [Installed]|Main Version: Java 8 Min Version: 181|The Java Version is to be installed in the machine.
kernel-core|NA|0.12.6|Basic core kernel packages.
kernel-logger-logback|NA|0.12.6|Use for the logging.
kernel-dataaccess-hibernate|NA|0.12.6|Used for the communicating to the DB.
kernel-idgenerator-prid|NA|0.12.6|Used to generate the PRID format.
kernel-idvalidator-prid|NA|0.12.6|Used to validate the PRID format.
kernel-crypto-signature|NA|0.12.6|Used to validate the signature response from server.
kernel-templatemanager|NA|0.12.6|Used to generate the template manager.
kernel-qrcodegenerator-zxing|NA|0.12.6|Used to generate the QR code in acknowledgment page.
kernel-pdfgenerator-itext|NA|0.12.6|Used to scan the document in PDF format.
kernel-idobjectvalidator|NA|0.12.6|Used to validate the JSON.
kernel-fsadapter-hdfs|NA|0.12.6|DFS adaptor for HDFS.
kernel-virusscanner-clamav|NA|0.12.6|Used to communicate to the Antivirus Clam AV.
kernel-applicanttype-service|NA|0.12.6|Used to get the applicant types. 
kernel-masterdata-service|NA|0.12.6|Used to get the master data. 
kernel-emailnotification-service|NA|0.12.6|Used to notify the user through email. 
kernel-smsnotification-service|NA|0.12.6|Used to notify the user through sms. 
kernel-auditmanager-service|NA|0.12.6|Used to audit the reocrds into the DB.
kernel-cryptomanager-service|NA|0.12.6|Used to encrypt the pre-redistaion data information.


### 1.7 Prerequisites : <br><sub>Dependent DB/External applications and services</sub></br>
Dependency|Component|Tag/Version|Description
-----|--------------|----------------|----------------
DB|mosip_prereg|0.12.6|Required for Pre-Registration module. <br>DB script link:- https://github.com/mosip/mosip/tree/0.12.6/scripts/database/mosip_prereg

#### Note:
If you want to access rest apis, access token is required from authmanager.
1.Authenticate through clientId/Secret or UserId/Password haveing respective roles assigned.
2.After successful authentication access token will set as Autherization cookies.
3.Access API through postman by passing the access token in cookies.
NOTE: Swagger UI sends cookies automatically through browser.

1. Admin Token

    url- https://dev.mosip.io/v1/authmanager/authenticate/useridPwd

    ```
    {
    "id": "string",
    "metadata": {},
    "request": {
        "appId": "admin",
        "password": "preregadmin",
        "userName": "preregadmin"
    },
    "requesttime": "2018-12-10T06:12:52.994Z",
    "version": "string"
    }
    ```


2. Individual Token

    i. Send otp 
    url -   https://dev.mosip.io/preregistration/v1/login/sendOtp

    ### Request-
    ```
    {
    "id": "mosip.pre-registration.login.sendotp",
    "version": "1.0",
    "requesttime": "2019-05-14T07:24:47.605Z",
    "request": {
        "userId": "8907654778"
    }
    }
    ```

    ii. Validate Otp 

    url - https://dev.mosip.io/preregistration/v1/login/validateOtp

    ```
    Request-
    {
    "id": "mosip.pre-registration.login.useridotp",
    "version": "1.0",
    "requesttime": "2019-06-03T08:28:04.783Z",
    "request": {
        "otp": "345674",
        "userId": "8907654778"
    }
    }
    ```
    After hitting validateOtp() api with proper request having otp and userid you will get the token in the header with the name Cookie.

### 1.8 Technoforte Defects available for re-testing in the current build :

Issue key   |  Summary|Severity
--------------|--------------|----------------
MOS-24602     |"All request body parameters are marked as required in pre-reg wiki (i.e. including all pre-reg services) even some parms are optional."|Major
MOS-24095|Pre-reg UI web is not working in IE browser (Version 11.43).|Major
MOS-24026|On downloading acknowledgement additional blank page is displayed	|Minor
MOS-23989|Incorrect error message on executing batch job for expired status when there are no applications to be updated.|Minor
MOS-23939|View Acknowledgement in the dashboard is not immediately disabled after cancelling the appointment|Minor
MOS-23917|Inconsistency in 'RETOUR' button (RETOUR button is not highlighted in Preview, Booking page).|Minor
MOS-23880|UI issue on center search page (all centers are not displaying).|Minor
MOS-23877|Error on running datasync API|Major
MOS-23852|On cancelling an appointment which is not booked, incorrect error message is displayed|Minor
MOS-23851|On providing incorrect destination pre-reg id during copy documents, incorrect error message is displayed|Minor
MOS-23221|Mismatch in the error code and error message by passing invalid value for preregistration id|Minor
MOS-22802|API parameter validations are missing|Critical




