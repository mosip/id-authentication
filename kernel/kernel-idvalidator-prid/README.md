## kernel-idvalidator-prid

[Background & Design](../../docs/design/kernel/kernel-idvalidator.md)
 

 
[API Documentation]
 
 ```
 mvn javadoc:javadoc

 ```
 
**Properties to be added in Spring application environment using this component**

[application-dev.properties](../../config/application-dev.properties)

 ```
 #-----------------------------PRID Properties------------------------------------
# length of the prid
mosip.kernel.prid.length=14

# Upper bound of number of digits in sequence allowed in id. For example if
# limit is 3, then 12 is allowed but 123 is not allowed in id (in both
# ascending and descending order)
mosip.kernel.prid.sequence-limit=3

# Number of digits in repeating block allowed in id. For example if limit is 2,
# then 4xxx4 is allowed but 48xxx48 is not allowed in id (x is any digit)
mosip.kernel.prid.repeating-block-limit=3


# Lower bound of number of digits allowed in between two repeating digits in
# id. For example if limit is 2, then 11 and 1x1 is not allowed in id (x is any
# digit)
mosip.kernel.prid.repeating-limit=2
 
 ```
 
 **Maven Dependency**
 
 ```
 	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idvalidator-prid</artifactId>
			<version>${project.version}</version>
		</dependency>

 ```
 

**PRID validation**

1. Input String should be numeric string

2. Length of the given Input is as mentioned in property file for the respective IDs

3. Input Numeric String should not contain '0' or '1' as the first digit

4. Input Numeric String should not contain any sequential number for 2 or more than two digits

5. Input Numeric String should not contain any repeating numbers for 2 or more than two digits

6. and Input Numeric String should not have repeated block of numbers for more than 2 digits

7. The last digit of the generated token id should have checksum.


**The response will be true is case if it pass the all validation condition otherwise it will throw respective error message**

 

**Usage Sample:**

Autowired interface PridValidator 

```
   @Autowired
	private PridValidator<String> pridValidatorImpl;

```
  Call the method to validate Id

  Valid PRID Example:
 
```
	boolean return = pridValidatorImpl.validateId("537184361359820"); //return true
	
	String id="537184361359820";
	
	int pridLength=14;
	
	int sequenceLimit=3;
	
	int repeatingLimit=3;
	
	int blockLimit=2;
	
	boolean return = pridValidatorImpl.validateId(id,pridLength,sequenceLimit,repeatingLimit,blockLimit)//return true

```
 
  Invalid PRID Example:
 
```
 	boolean isValid = pridValidatorImpl.validateId("037184361359820"); //Throws Exception "PRID should not contain Zero or One as first digit."
 	
```
