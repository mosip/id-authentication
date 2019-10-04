# ID Object Validator:

## 1. Background

This library can be used to validate ID Object. It performs below 4 validations.
1. Validate input ID JSON String against ID Schema configured for the country
2. Validate ID attributes against configured regex patterns
3. Validate ID attributes like Gender, Language, DocumentType, Location, etc. against Master Data APIs
4. Validate Country-specific ID attributes

**Note -**
1. This IDObject Validator needs to be modified if there is any change made to the ID Object for any country
2. The ID Attributes added in ID Schema are for reference purpose. Additional ID attributes for a country can be added
3. If any additional attributes added or existing one removed from ID Schema, corresponding validations needs to be modified in the IDObject Validator


***1.1.Target Users -***  
- Pre-Registration and Registration Processor can use ID Object Validator to validate ID Object against ID Schema configured for the country, and data of few ID attributes against Master Data stored for the country
- Registration Client can use ID Object Validator to validate created ID Object against ID Schema for the country, verification for ID Attributes for Registration Processor will happen against their local database


***1.2. Key Non-Functional Requirements -***   
-	Logging :
	-	Log all exceptions along with error code and short error message
	-	As a security measure, Individualâ€™s UIN should not be logged
-	Audit :
	-	Audit all transaction details in database
	-	Individualâ€™s UIN should not be audited     
-	Exception :
	-	Any error in storing or retrieval of Identity details should be handled with appropriate error code and message in the response  
-	Security :  

## 2. Solution:    

The key solution considerations are - 
- Create an interface IDObjectValidator with a method to validate input ID JSON
- Create a project which implements IDObjectValidator based on any opensource json-schema-validator. Current implementation uses Draft v7 specifications of [JSON Schema](http://json-schema.org/)
- IDObjectValidator can be used in any MOSIP module to validate ID Object, after adding its jar in their class path


**2.1. Class Diagram:**    
![kernel_idobjectvalidator_classdiagram](_images/kernel-idobjectvalidator-cd.PNG)

Below are the functional details of each of the above validation classes -
1. **IdObjectSchemaValidator** -  This *generic* validator class performs structural validation of input ID JSON against ID Schema configured for the country. It also validates mandatory ID Attributes configured for each module using ValidationCase as an input scenario. These mandatory ID attributes per module is configured in [global](https://github.com/mosip/mosip-config/blob/master/config-templates/application-env.properties) properties.

***Usage of IdObjectSchemaValidator-*** 
- Autowire IdObjectSchemaValidator in your class
- Configure mandatory attribute to be validated for a scenario in a module in global properties as below -
*mosip.kernel.idobjectvalidator.mandatory-attributes.[module-name].[scenario-name]=[comma-separated-ID-attributes]*    
Sample configuration of mandatory ID Attributes for New Registration in ID Repository module is - 
*mosip.kernel.idobjectvalidator.mandatory-attributes.id-repository.new-registration=fullName,dateOfBirth,gender*

Below are the list of scenarios supported for mandatory ID Attribute validation - 
- New Registration
- Child Registration
- UIN Update
- Lost UIN

2. **IdObjectPatternValidator** - This *generic* validator class performs pattern validation of ID attributes configured in [global](https://github.com/mosip/mosip-config/blob/master/config-templates/application-env.properties) properties.

***Usage of IdObjectPatternValidator-*** 
- Autowire IdObjectPatternValidator in your class
- Add pattern to be validated in global properties as below -
*mosip.id.validation.identity.[id-attribute]=[validation-pattern]*
Sample configuration of pattern for an ID Attribute is - 
*mosip.id.validation.identity.phone=^([6-9]{1})([0-9]{9})$*

3. **IdObjectReferenceValidator** - This validator class contains specific validations for ID Attribute like validating gender attribute against master-data. This customizable validation class will part of kernel-ref-idobjectvalidator in [mosip-ref-impl](https://github.com/mosip/mosip-ref-impl) repository. Any country can chose to customize these validations based on their ID Attributes.

***Usage of IdObjectReferenceValidator -*** 
- Add *mosip.kernel.idobjectvalidator.referenceValidator* in module-specific properties file with fully qualified classname of this class as below - 
*mosip.kernel.idobjectvalidator.referenceValidator=io.mosip.kernel.idobjectvalidator.impl.IdObjectReferenceValidator*   
**Note** - We support only **one** Reference Validator from classpath.
- Run module jar with absolute path of the reference validator jar as below - 
*java –Dloader.path=<absolute-path-of-kernel-ref-IdObjectValidator.jar> -jar id-repository-identity-service.jar*
- In order to run module jar in a docker container, either physically add the jar in docker container or fetch it from a remote repository like below - 
ADD http://13.71.87.138:8040/artifactory/libs-release-local/io/mosip/kernel-ref-IdObjectValidator.jar kernel-ref-IdObjectValidator.jar

4. **IdObjectCompositeValidator** - This is a composite validator which can be autowired when all the above 3 validators are required.

**2.2. Sequence Diagram:**    
![kernel_idobjectvalidator_classdiagram](_images/kernel-idobjectvalidator-sd.PNG)

