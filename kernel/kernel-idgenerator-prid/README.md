## kernel-idgenerator-prid

[Background & Design](../../docs/design/kernel/Kernel-idgenerator-prid.md)

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
	<dependencies>
		<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idgenerator-prid</artifactId>
			<version>${project.version}</version>
	</dependency>

```
 
**Description**
Logic behind generating prid
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
	  
	  
**Sample PRID:"**
	  
Generated Prid: 58361782748604
	
   
   








