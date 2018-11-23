## kernel-idvalidator

 
 This folder has ID Validator module which can be used to validate UIN, VID, RID and PRID as numeric string based on policy.

 [API Documentation ]
 
 ```
 mvn javadoc:javadoc

 ```
### The inputs which have to be provided are:

**Input Format for UIN, VIN and PRID**

1. Input String should be numeric string

2. Length of the given Input is as mentioned in property file for the respective IDs

3. Input Numeric String should not contain '0' or '1' as the first digit

4. Input Numeric String should not contain any sequential number for 2 or more than two digits

5. Input Numeric String should not contain any repeating numbers for 2 or more than two digits

6. and Input Numeric String should not have repeated block of numbers for more than 2 digits

7. The last digit of the generated token id should have checksum.


**Input Format for RID**

1. Input String should be Numeric String

2. Length of the given RID is as mentioned in property file

3. Input RID first 5 digit from 1 to 5 digit should contain Center ID

4. and from 6 to 10 digit should contain Dongle ID

5. and next 5 digit should be sequential number

6. and next 14 digit should be timestamp in this format yyyymmddhhmmss



**Properties to be added in Spring application environment using this component**

[kernel-idvalidator-dev.properties](../../config/kernel-idvalidator-dev.properties)

**The response will be true is case if it pass the all validation condition otherwise it will throw respective error message**

 

####Usage Sample
Autowired interface IdValidator and call the method validateId(Id)

Usage Sample
 
UIN Example:
 
 ```
	@Autowired
	private IdValidator<String> uinValidatorImpl;
	
	Boolean return = uinValidatorImpl.validateId("426789089018");

	System.out.println("Validation Result for the given UIN = "+return);
 
 ```

VIN Example:
 
 ```
	@Autowired
	private IdValidator<String> vinValidatorImpl;
	
	Boolean return = vinValidatorImpl.validateId("537184361359820");

	System.out.println("Validation Result for the given VIN = "+return);
 
 ```
RID Example:

```
	@Autowired
	private RidValidator<String> rinValidatorImpl;
	
	Boolean return = ridValidatorImpl.validateId("27847657360002520181208183050");

	System.out.println("Validation Result for the given RID = "+return);
 
 ```
 PRID Example:
 
  ```
	@Autowired
	private IdValidator<String> pridValidatorImpl;
	
	Boolean return = pridValidatorImpl.validateId("537184361359820");

	System.out.println("Validation Result for the given PRID = "+return);
 
 ```
 










