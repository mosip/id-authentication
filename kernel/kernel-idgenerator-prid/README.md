## Module kernel-idgenerator-prid



[API Documentation](doc/index.html)

```
 mvn javadoc:javadoc

 ```
 
 **Properties to be added in Spring application environment using this component**
 
[application-dev.properties](../../config/application-dev.properties)
 
 **Database properties**
 
schema:ids

table:prid 
 
 
**Maven Dependency**

```
	<dependencies>
		<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idgenerator-prid</artifactId>
			<version>${project.version}</version>
	</dependency>

```
 
**Description**

1.**ADMIN** _can only configure the length_ 

2.Logic behind generating prid
  1. _The  Id should not be generated sequentially._
  2. _cannot not have repeated numbers,cannot contain any repeating numbers for configured number of digit or more than configured number of digits in property file._
  3. _cannot have repeated block of numbers for configured number of digits in property file._ 
  4. _cannot contain any sequential number for configured number of digits or more than configured number of  digits in property file and cannot contain alphanumeric values._
  5. _The last digit of the generated id should be reserved for checksum_  
  6. _The number should not contain '0' or '1' as the first digit._
  
**Sample Usage**
  
      //Autowire the interface class PridGenerator
	  @Autowired
	  private PridGenerator<String> pridGeneratorImpl;
	
     //Call generateId from autowired PridGenerator instance to generateId.
	  String generatedPrid = pridGeneratorImpl.generateId());
	  
	  
**Sample PRId**
	  
_Generated Prid_: 58361782748604
	
   
   








