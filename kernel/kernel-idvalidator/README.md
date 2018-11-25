## kernel-idvalidator

 
 [API Documentation ]
 
 ```
 mvn javadoc:javadoc

 ```
 
**Properties to be added in Spring application environment using this component**

[kernel-idvalidator-dev.properties](../../config/kernel-idvalidator-dev.properties)

 
 
 **Maven Dependency**
 
 ```
 	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idvalidator</artifactId>
			<version>${project.version}</version>
		</dependency>

 ```
 

**UIN, VID and PRID validation**

1. Input String should be numeric string

2. Length of the given Input is as mentioned in property file for the respective IDs

3. Input Numeric String should not contain '0' or '1' as the first digit

4. Input Numeric String should not contain any sequential number for 2 or more than two digits

5. Input Numeric String should not contain any repeating numbers for 2 or more than two digits

6. and Input Numeric String should not have repeated block of numbers for more than 2 digits

7. The last digit of the generated token id should have checksum.


**RID validation**

1. Input String should be Numeric String

2. Length of the given RID is as mentioned in property file

3. Input RID first 5 digit from 1 to 5 digit should contain Center ID

4. and from 6 to 10 digit should contain Dongle ID

5. and next 5 digit should be sequential number

6. and next 14 digit should be timestamp in this format yyyymmddhhmmss





**The response will be true is case if it pass the all validation condition otherwise it will throw respective error message**

 

**Usage Sample:**

Autowired interface IdValidator and call the method validateId(Id)

 
Valid UIN  Example:
 
 ```
	@Autowired
	private IdValidator<String> uinValidatorImpl;
	
	boolean isValid = uinValidatorImpl.validateId("426789089018"); //return true
	
```
	
Invalid UIN Example

```
	@Autowired
	private IdValidator<String> uinValidatorImpl;
	
	boolean isValid = uinValidatorImpl.validateId("026789089018"); //throw Exception "UIN should not contain Zero or One as first digit."

 
 ```

Valid VID Example:
 
 ```
	@Autowired
	private IdValidator<String> vinValidatorImpl;
	
	boolean return = vinValidatorImpl.validateId("537184361359820"); //return true

```
	
Invalid VID Example:
	
```
	@Autowired
	private IdValidator<String> vinValidatorImpl;
	
	boolean isValid = vinValidatorImpl.validateId("037184361359820"); //Throws Exception "VID should not contain Zero or One as first digit."
 
```

Valid RID Example:

```
	@Autowired
	private RidValidator<String> rinValidatorImpl;
	
	boolean return = ridValidatorImpl.validateId("27847657360002520181208183050"); //return true

 
 ```
 Invalid RID Example:
 
 ```
	@Autowired
	private RidValidator<String> rinValidatorImpl;
	
	boolean return = ridValidatorImpl.validateId("27847657360002520181208183070"); //Throws Exception "Invalid Time Stamp Found"
	
 ```
 
 Valid PRID Example:
 
```
	@Autowired
	private IdValidator<String> pridValidatorImpl;
	
	boolean return = pridValidatorImpl.validateId("537184361359820"); //return true

```
 
  Invalid PRID Example:
 
```
 	@Autowired
	private IdValidator<String> pridValidatorImpl;
	
 	boolean isValid = pridValidatorImpl.validateId("037184361359820"); //Throws Exception "PRID should not contain Zero or One as first digit."
 	
```









