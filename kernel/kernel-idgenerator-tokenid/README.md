## kernel-idgenerator-tokenid

[Background & Design](../../docs/design/kernel/Kernel-idgenerator-tokenid.md)

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

_Token ID is a numeric hash for the combination of TSP and UIN_



**Sample**
 
  ```
//Autowire the interface TokenIdGenerator
//First String parameter : TSP ID
//Second String parameter : UIN ID
  @Autowired
	TokenIdGenerator<String, String> tokenIdGenerator;

 //Call generateId()
  String tokenId = tokenIdGenerator.generateId("8739","908757269171");
  
```
**Sample TokenID:**

Generated tokenId: 526900409300563849276960763148952762









