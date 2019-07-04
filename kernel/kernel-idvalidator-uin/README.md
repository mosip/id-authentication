## kernel-idvalidator-uin

[Background & Design](../../docs/design/kernel/kernel-idvalidator.md)
 

[API Documentation ]
 
 ```
 mvn javadoc:javadoc

 ```
 
**Properties to be added in Spring application environment using this component**

[application-dev.properties](../../config/application-dev.properties)

 ```
 #-----------------------------UIN Properties--------------------------------------
#length of the uin
mosip.kernel.uin.length=12
#minimun threshold of uin
mosip.kernel.uin.min-unused-threshold=100000
#number of uins to generate
mosip.kernel.uin.uins-to-generate=200000
#uin generation cron
mosip.kernel.uin.uin-generation-cron=0 * * * * *
#restricted numbers for uin
mosip.kernel.uin.restricted-numbers=786,666
#sequence limit for uin filter
mosip.kernel.uin.length.sequence-limit=3
#repeating block limit for uin filter
mosip.kernel.uin.length.repeating-block-limit=2
#repeating limit for uin filter
mosip.kernel.uin.length.repeating-limit=2
 ```
 
 **Maven Dependency**
 
 ```
 	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idvalidator-uin</artifactId>
			<version>${project.version}</version>
		</dependency>

 ```
 

**UIN validation**

1. Input String should be numeric string

2. Length of the given Input is as mentioned in property file for the respective IDs

3. Input Numeric String should not contain '0' or '1' as the first digit

4. Input Numeric String should not contain any sequential number for 2 or more than two digits

5. Input Numeric String should not contain any repeating numbers for 2 or more than two digits

6. and Input Numeric String should not have repeated block of numbers for more than 2 digits

7. The last digit of the generated token id should have checksum.


**The response will be true is case if it pass the all validation condition otherwise it will throw respective error message**

 

**Usage Sample:**

Autowired interface IdValidator

```
	@Autowired
	private UinValidator<String> uinValidatorImpl;
```

Call the method validate Id

 
Valid UIN  Example:
 
 ```
	boolean isValid = uinValidatorImpl.validateId("426789089018"); //return true
	
```

Invalid UIN Example

```
	boolean isValid = uinValidatorImpl.validateId("026789089018"); //throw Exception "UIN should not contain Zero or One as first digit."

 
 ```
