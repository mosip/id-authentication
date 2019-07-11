## kernel-idgenerator-prid

[Background & Design](../../docs/design/kernel/kernel-idgenerator-prid.md)

[API Documentation]

```
 mvn javadoc:javadoc

 ```
 
 **Properties to be added in Spring application environment using this component**
 
[application-dev.properties](../../config/application-dev.properties)
 
```
#-----------------------------PRID Properties------------------------------------

#prid-length
mosip.kernel.prid.length=14
# Crypto asymmetric algorithm name
mosip.kernel.crypto.asymmetric-algorithm-name=RSA
#Crypto symmetric algorithm name
mosip.kernel.crypto.symmetric-algorithm-name=AES

#Secuence limit
mosip.kernel.prid.sequence-limit=3

# Number of digits in repeating block allowed in id. For example if limit is 2,
# then 4xxx4 is allowed but 48xxx48 is not allowed in id (x is any digit)
#to disable validation assign zero or negative value
mosip.kernel.prid.repeating-block-limit=3


# Lower bound of number of digits allowed in between two repeating digits in
# id. For example if limit is 2, then 11 and 1x1 is not allowed in id (x is any digit) to disable validation assign zero or negative value
mosip.kernel.prid.repeating-limit=2

# list of number that id should not be start with to disable null
mosip.kernel.prid.not-start-with=0,1

#restricted numbers for prid
mosip.kernel.prid.restricted-numbers=786,666


```
 
 
**Maven Dependency**

```
	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idgenerator-prid</artifactId>
			<version>${project.version}</version>
	</dependency>

```
** Database Properties **

```
Schema : prereg

Tables : prid_seed , prid_seq

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
	
   
   








