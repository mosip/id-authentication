## kernel-idgenerator-vid

[Background & Design](../../docs/design/kernel/Kernel-idgenerator-vid.md)

[API Documentation](doc/index.html)

```
 mvn javadoc:javadoc

 ```
**Maven Dependency**

```
	<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idgenerator-vid</artifactId>
			<version>${project.version}</version>
	</dependency>

```



** Properties to be added in parent Spring Application environment **

[application-dev.properties](../../config/application-dev.properties)


```

# length of the vid
mosip.kernel.vid.length=16

# Upper bound of number of digits in sequence allowed in id. For example if
# limit is 3, then 12 is allowed but 123 is not allowed in id (in both
# ascending and descending order)
# to disable sequence limit validation assign 0 or negative value
mosip.kernel.vid.length.sequence-limit=3

# Number of digits in repeating block allowed in id. For example if limit is 2,
# then 4xxx4 is allowed but 48xxx48 is not allowed in id (x is any digit)
# to disable repeating block validation assign 0 or negative value
mosip.kernel.vid.length.repeating-block-limit=2


# Lower bound of number of digits allowed in between two repeating digits in
# id. For example if limit is 2, then 11 and 1x1 is not allowed in id (x is any digit)
# to disable repeating limit validation, assign 0  or negative value
mosip.kernel.vid.length.repeating-limit=2

# list of number that id should not be start with
# to disable null
mosip.kernel.vid.not-start-with=0,1

#restricted numbers for vid
mosip.kernel.vid.restricted-numbers=786,666

# Crypto asymmetric algorithm name
mosip.kernel.crypto.asymmetric-algorithm-name=RSA
#Crypto symmetric algorithm name
mosip.kernel.crypto.symmetric-algorithm-name=AES


```



```
Schema : ida

Tables : vid_seed , vid_seq

```


**Description**

1.**ADMIN** _can only configure the length_ 

2.Logic behind generating vid
  1. _The  Id should not be generated sequentially._
  2. _cannot not have repeated numbers,cannot contain any repeating numbers for configured number of digit or more than configured number of digits in property file._
  3. _cannot have repeated block of numbers for configured number of digits in property file._ 
  4. _cannot contain any sequential number for configured number of digits or more than configured number of  digits in property file and cannot contain alphanumeric values._
  5. _The last digit of the generated id should be reserved for checksum_  
  6. _The number should not contain '0' or '1' as the first digit._
  
  
**Sample Usage**
  
    
      //Autowire the interface class vidGenerator
	  @Autowired
	  private VidGenerator<String> vidGeneratorImpl;
	
     //Call generateId from autowired vidGenerator instance to generateId.
     
     
	  String generatedVid = vidGeneratorImpl.generateId());
	  
	  
	 
**Sample VID**
	  
_Generated vid_: 5916983045841801  
  






