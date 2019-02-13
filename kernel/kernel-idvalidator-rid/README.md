## kernel-idvalidator-rid

[Background & Design](../../docs/design/kernel/kernel-idvalidator.md)
 

 
[API Documentation]
 
 ```
 mvn javadoc:javadoc

 ```
 
**Properties to be added in Spring application environment using this component**

[application-dev.properties](../../config/application-dev.properties)

```
#-----------------------------RID Properties---------------------------------------
# length of the rid
mosip.kernel.rid.length=29
# length of the center id
mosip.kernel.rid.centerid-length=5
#length of the machine id
mosip.kernel.rid.machineid-length=5
# length of the timestamp
mosip.kernel.rid.timestamp-length=14
# rid sequence length
mosip.kernel.rid.sequence-length=5
```

 
 **Maven Dependency**
 
 ```
 	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idvalidator-rid</artifactId>
			<version>${project.version}</version>
		</dependency>

 ```
 
**RID validation**

1. Input String should be Numeric String

2. Length of the given RID is as mentioned in property file

3. Input RID first 5 digit from 1 to 5 digit should contain Center ID

4. and from 6 to 10 digit should contain Machine ID

5. and next 5 digit should be sequential number

6. and next 14 digit should be timestamp in this format yyyymmddhhmmss





**The response will be true is case if it pass the all validation condition otherwise it will throw respective error message**

 

**Usage Sample:**

Autowired interface RidValidator

```
	@Autowired
	private RidValidator<String> ridValidatorImpl;
```

Call the method validate Id

Valid RID Example:

```
	String centerId = "27847";

	String machineId = "65736";
	
	String rid ="27847657360002520181208183050";
	
	int centerIdLength = 5;
	
	int machineIdLength = 5;
	
	int sequenceLength=5;
	
	int timeStampLength = 14;
	
	boolean return = ridValidatorImpl.validateId(rid,centerId,machineId); //return true
	boolean return = ridValidatorImpl.validateId(rid,centerId,machineId,centerIdLength,machineIdLength,sequenceLength,timeStampLength); //return true
	boolean return = ridValidatorImpl.validateId(rid); //return true
	boolean return = ridValidatorImpl.validateId(rid,,centerIdLength,machineIdLength,sequenceLength,timeStampLength); //return true
 
 ```
 
 
 Invalid RID Example:
 
 ```
	String centerId = "27847";

	String machineId = "65736";
	
	String rid ="27847657360002520181208183070";
	
	boolean return = ridValidatorImpl.validateId(rid,centerId,machineId); //Throws Exception "Invalid Time Stamp Found"
	
 ```







