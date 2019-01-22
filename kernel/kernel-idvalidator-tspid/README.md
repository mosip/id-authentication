## kernel-idvalidator-tspid

 1- [Background & Design](../../design/kernel/kernel-idvalidator.md)
 

 
 2- [API Documentation ]
 
 ```
 mvn javadoc:javadoc

 ```
 
**Properties to be added in Spring application environment using this component**

mosip.kernel.tspid.length=4

[application-dev.properties](../../config/application-dev.properties)

 
 
 **Maven Dependency**
 
 ```
 	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idvalidator-tspid</artifactId>
			<version>${project.version}</version>
		</dependency>

 ```
 


**The response will be true is case if it pass the all validation condition otherwise it will throw respective error message**

 

**Usage Sample:**

Autowired interface IdValidator and call the method validateId(Id)

 Valid TSPID  Example:
 
 ```
	@Autowired
	private IdValidator<String> tspIdValidatorImpl;
	
	boolean isValid = tspIdValidatorImpl.validateId("1000"); //return true
	
```




 






