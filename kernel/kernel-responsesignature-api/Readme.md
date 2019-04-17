## kernel-responsesignature-api
This api can be used to digitally sign the response of a microservice.


[Background & Design](../../docs/design/kernel/kernel-responsesignature.md)


**Api Documentation**

```
mvn javadoc:javadoc
```

**Maven dependency**

 ```
    <dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-responsesignature-api</artifactId>
			<version>${project.version}</version>
		</dependency>
 ```
 
 **Prerequisite**
 
 ```
 For successful digital signature, 
 cryptomanagerservice and keymanagerservice should be running.
 
```
 
 **Application Properties**
 
 ```
 # Cryptomanager url
 mosip.kernel.signature.cryptomanager-encrypt-url=https://{environment}/cryptomanager/private/encrypt
 ```
 
 **Sample Usage**
 
  ```
   #Instance of signatureUtil
   @Autowired
   SignatureUtil signatureUtil
   
   #Call method below
   signatureUtil.signResponseData(response);
   ```
 **Sample response**
 
 ```
 #The encrypted response is added to the header.
 Response headers
 
 response-signature=response-signature: EoYMjwMJadE-FOYupAuSwQFnUkE87_5jqqDBf9FRK75O4XBcvZws3dd
 ```
   