## kernel-idgenerator-tokenid

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

[application-dev.properties](../../config/application-dev.properties)


**Database properties**
schema:ids

table:tokenId


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
**Sample TokenID**

_Generated tokenId_ : 526900409300563849276960763148952762









