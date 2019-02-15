## kernel-pinvalidator

[Background & Design](../../docs/design/kernel/kernel-pinvalidator.md)
 


[API Documentation]
 
 ```
 mvn javadoc:javadoc

 ```
 
**Properties to be added in Spring application environment using this component**

[application-dev.properties](../../config/application-dev.properties)

 
 
 **Maven Dependency**
 
 ```
 	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-pinvalidator</artifactId>
			<version>${project.version}</version>
		</dependency>

 ```
 

**StaticPIN validation**

1. Input String should be numeric string

2. Length of the given Input is as mentioned in property file for the static pin


**The response will be true is case if it pass the all validation condition otherwise it will throw respective error message**

 

**Usage Sample:**

Autowired interface PinValidator 

```
   @Autowired
	private PinValidator<String> PinValidatorImpl;

```
  Call the method to validate Id

  Valid PRID Example:
 
```
	boolean isValid = PinValidatorImpl.validatePin("537180"); //return true
	
```
 
  Invalid PRID Example:
 
```
 	boolean isValid =  PinValidatorImpl.validatePin("53C18A"); //Throws Exception "Static PIN length must be numeric."
 	boolean isValid = PinValidatorImpl.validatePin("5334"); //Throws Exception " Static PIN length Must be {length}."
 	
```
