## Module kernel-idgenerator-tokenid

[Background & Design](../../docs/design/kernel/kernel-idgenerator-tokenid.md)

[API Documentation]


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

```
#-----------------------------TOKEN-ID Properties---------------------------------
#lenght of the token id
mosip.kernel.tokenid.length=36

# Upper bound of number of digits in sequence allowed in id. For example if
# limit is 3, then 12 is allowed but 123 is not allowed in id (in both
# ascending and descending order)
mosip.kernel.tokenid.sequence-limit=3
```

[application-dev.properties](../../config/application-dev.properties)



** Description **

1._The Token Id should not be generated sequentially,cannot not have repeated numbers and cannot contain alphanumeric values_

2._The last digit of the generated token id should be reserved checksum_ .
3._cannot contain any sequential number for configured number of digits or more than configured number of digits in property file.



**Sample**
 
  ```
//Autowire the interface class tokenIdGenerator
  @Autowired
  private TokenIdGenerator<String> tokenIdGeneratorImpl;

 //Call generateId from autowired tokenIdGenerator instance to generateId.
  String tokenId = tokenIdGeneratorImpl.generateId());
  
```
**Sample TokenID:**

Generated tokenId: 526900409300563849276960763148952762









