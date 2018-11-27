## Module kernel-idgenerator-tokenid

[API Documentation](doc/index.html)


```
 mvn javadoc:javadoc

 ```
 
 **Maven Dependency**

```
	<dependencies>
		<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-idgenerator-tokenid</artifactId>
			<version>${project.version}</version>
	</dependency>

```

** Properties to be added in parent Spring Application environment **

[kernel-idgenerator-tokenid-dev.properties](../../config/kernel-idgenerator-tokenid-dev.properties)


**Database properties**
schema:ids

table:tokenId


** Description **

1.**ADMIN** _can only configure the length_ 

2._The Token Id should not be generated sequentially,cannot not have repeated numbers and cannot contain alphanumeric values_

3._The last digit of the generated token id should be reserved checksum_ .



**Sample**
 
  ```
//Autowire the interface class tokenIdGenerator
  @Autowired
  private TokenIdGenerator<String> tokenIdGeneratorImpl;

 //Call generateId from autowired tokenIdGenerator instance to generateId.
  String tokenId = tokenIdGeneratorImpl.generateId());
  
```
**Sample TokenID**

_Generated tokenId_ : 526900409300563849276960763148952762









