## kernel-idvalidator-vid

 [Background & Design](../../docs/design/kernel/kernel-idvalidator.md)
 

 [API Documentation ]
 
 ```
 mvn javadoc:javadoc

 ```
 
**Properties to be added in Spring application environment using this component**

[application-dev.properties](../../config/application-dev.properties)

 ```
 #-----------------------------VID Properties--------------------------------------
# length of the vid
mosip.kernel.vid.length=16


# Upper bound of number of digits in sequence allowed in id. For example if
# limit is 3, then 12 is allowed but 123 is not allowed in id (in both
# ascending and descending order)
mosip.kernel.vid.length.sequence-limit=3

# Number of digits in repeating block allowed in id. For example if limit is 2,
# then 4xxx4 is allowed but 48xxx48 is not allowed in id (x is any digit)
mosip.kernel.vid.length.repeating-block-limit=2


# Lower bound of number of digits allowed in between two repeating digits in
# id. For example if limit is 2, then 11 and 1x1 is not allowed in id (x is any
# digit)
mosip.kernel.vid.length.repeating-limit=2
 ```
 
 **Maven Dependency**
 
 ```
 	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idvalidator-vid</artifactId>
			<version>${project.version}</version>
		</dependency>

 ```
 

**VID validation**

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
	private VidValidator<String> vidValidatorImpl;
```
Call the method to validate Id

Valid VID Example:
 
 ```
	boolean return = vidValidatorImpl.validateId("537184361359820"); //return true

```
	
Invalid VID Example:
	
```
	boolean isValid = vidValidatorImpl.validateId("037184361359820"); //Throws Exception "VID should not contain Zero or One as first digit."
 
```


 
