## Module kernel-idgenerator-prid



[API Documentation](doc/index.html)

```
 mvn javadoc:javadoc

 ```
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
  1. _The Id should not be generated sequentially._
  2. _cannot not have repeated numbers,cannot contain any repeating numbers for 2 or more than 2 digits._
  3. _cannot have repeated block of numbers for 2 or more than 2 digits._ 
  4. _cannot contain any sequential number for 3 or more than 3 digits and cannot contain alphanumeric values._
  5. _The last digit of the generated  id should have checksum_  
  6. _The number should not contain '0' or '1' as the first digit._
  
**Sample Usage**
  
    
      //Autowire the interface class PridGenerator
	  @Autowired
	  private PridGenerator<String> pridGenerator;
	
     //Call generateId from autowired PridGenerator instance to generateId.
	  String generatedPrid = pridGenerator.generateId());
	  
	  
	 
**Sample Output**
	  
_Generated Prid_: 58361782748604
	
   
   








