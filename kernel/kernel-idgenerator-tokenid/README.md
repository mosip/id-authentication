## kernel-idgenerator-tokenid

[Background & Design](../../docs/design/kernel/kernel-idgenerator-tokenid.md)

[API Documentation]


```
 mvn javadoc:javadoc

 ```
 
 **Maven Dependency**

```
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

# Crypto asymmetric algorithm name
mosip.kernel.crypto.asymmetric-algorithm-name=RSA

#Crypto symmetric algorithm name
mosip.kernel.crypto.symmetric-algorithm-name=AES
```

[application-dev.properties](../../config/application-dev.properties)


** Database Properties **

```
Schema : ida

Tables : token_seed , token_seq

```


**Sample**
 
  ```
//Autowire the interface TokenIdGenerator

  @Autowired
	TokenIdGenerator<String> tokenIdGenerator;

 //Call generateId()
  String tokenId = tokenIdGenerator.generateId();
  
```
**Sample TokenID:**

Generated tokenId: 526900409300563849276960763148952762









