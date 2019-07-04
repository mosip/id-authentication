
**MOSIP**

|**S.No.**| **Deliverable Name**| **Supporting Information**|**Comments**|
|:------:|-----|---|---|
|1.|MOSIP - High Level Design Document|[Click to View](Deliverables---Attachments)|Refer attachment 1 in the linked page - Attachment Name: MOSIP_HLD_v2 - Delivered on 05Feb'19|
|2.|MOSIP - Platform Guide|[MOSIP Platform Documentation](Platform-Documentation)|Refer Section 10.1 in the linked page|
|3.|MOSIP - Getting Started Document|[MOSIP Getting Started Document](https://github.com/mosip/mosip/wiki/Getting-Started)|


**Date: 21 Jun 2019**

**Module: Pre Registration**

|**S.No.**|**Module**|**Deliverable Name**| **Supporting Information**|**Comments**|
|:------:|-----|---|---|---|
|1.|Pre Registration|Deployment Guide| [Click to View](https://github.com/mosip/mosip/wiki/Getting-Started#8-mosip-deployment-)|Refer Section (https://github.com/mosip/mosip/wiki/Getting-Started#611-installation-of-activemq)|
|2.|Pre Registration|Component-Feature-JIRA ID Mapping|[Click to View](https://github.com/mosip/mosip/wiki/Component-Feature-ID-JIRA-ID-Mapping#10-registration-processor-)|
|3.|Pre Registration|High Level Design Document|[Click to View](https://github.com/mosip/mosip/wiki/Deliverables---Attachments)|Refer Section 9 in the linked page|
|4.|Pre Registration|Code Drop|[Tag: 0.12.10](https://github.com/mosip/mosip/releases/tag/0.12.10)||
|5.|Pre Registration|Known Defects and Pending Items|||

**Date: 21 Jun 2019**

**Module: Pre Registration**

|**S.No.**|**Module**|**Deliverable Name**| **Supporting Information**|**Comments**|
|:------:|-----|---|---|---|
|1.|Pre Registration|Tested Code|[Tag: 0.12.10](https://github.com/mosip/mosip/releases/tag/0.12.10)|Exit Criteria: Sonar report with all quality gates cleared ([Sonar Report](http://104.215.158.154:9000/dashboard?id=io.mosip.preregistration%3Apre-registration-parent)), Zephyr report indicating: No Blocker/Critical/Major Defects, 100% test cases executed (link to Zephyr report)| 
|2.|Pre Registration|Test Cases|[Click to view](https://mosipid.atlassian.net/projects/MOS?selectedItem=com.thed.zephyr.je__project-centric-view-tests-page&testsTab=test-cycles-tab)|Test Cases Covered RegClient Reg Processor, Functional Testing and E2E testing Scenario;s|
|3.|Pre Registration|Mindmaps|[Click to View](/mosip/mosip/tree/master/docs/testing/Registration%20Client/Mindmaps)|
|4.|Pre Registration Api|Test Cases|[Click to View](https://github.com/mosip/mosip/blob/master/docs/testing/Registration%20Client/Mindmaps/Reg_Client_NonBio_Integration_TestCases.xlsx)|

**Prerequisites : <br><sub>Dependent module/component with their respective versions should be mentioned here</sub></br>**  

|**Module/Files**|**Component**|**Version**|**Description (If any)**|
|-----|-------------|----------------|--------------|
|Clam AV |NA|NA|<br>Download the windows clam av antivirus by provided link and install the s\w.</br> <br>[https://www.clamav.net/downloads#otherversions]</br>|
|Master Data Setup |NA|Latest Version|Kernel 0.12.10 version of DB scripts can be used. Refer [MOSIP Getting Started doc.] (https://github.com/mosip/mosip/wiki/Getting-Started#7-configuring-mosip-).|
|kernel-core|NA|0.12.10|Basic core kernel packages.|
|kernel-logger-logback|NA|0.12.10|Use for the logging.|
|kernel-dataaccess-hibernate|NA|0.12.10|Used for the communicating to the DB.|
|kernel-auditmanager-api|NA|0.12.10|Used to audit the records into the DB|
|kernel-idvalidator-rid|NA|0.12.10|Used to validate the RID format.|
|kernel-idvalidator-uin|NA|0.12.10|Used to validate the UIN format|
|kernel-idvalidator-prid|NA|0.12.10|Used to validate the PRID format|
|kernel-idgenerator-rid|NA|0.12.10|Used to Generate the RID.|
|kernel-crypto-signature|NA|0.12.10|Used to validate the signature response from server.|
|kernel-keygenerator-bouncycastle|NA|0.12.10|Used to generate the key pair for AES -256.|
|kernel-templatemanager-velocity|NA|0.12.10|Used to generate the template manager using the velocity|
|kernel-qrcodegenerator-zxing|NA|0.12.10|Used to generate the QR code in acknowledgment page.|
|kernel-pdfgenerator-itext|NA|0.12.10|Used to scan the document in PDF format.|
|kernel-crypto-jce|NA|0.12.10|Used to encrypt the packet information|
|kernel-jsonvalidator|NA|0.12.10|Used to validate the JSON.|
|kernel-virusscanner-clamav|NA|0.12.10|Used to communicate to the Antivirus Clam AV|
|kernel-transliteration-icu4j|NA|0.12.10|Used to transliterate the Arabic to French and vice versa.|
|kernel-applicanttype-api|NA|0.12.10|Used to get the applicant types |
|kernel-cbeffutil-api|NA|0.12.10|Used to generate the CBEFF file and validate against the schema also.|
|kernel-bioapi-provider|NA|0.12.10|Used to integrate for the user-onboarding.|

 
