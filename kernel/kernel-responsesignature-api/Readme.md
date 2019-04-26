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


 **Application Properties**
 
 ```
mosip.kernel.keygenerator.asymmetric-algorithm-name=RSA
mosip.kernel.keygenerator.asymmetric-algorithm-length=2048
mosip.kernel.keygenerator.symmetric-algorithm-name=AES
mosip.kernel.keygenerator.symmetric-algorithm-length=256
mosip.kernel.crypto.symmetric-algorithm-name=AES
mosip.kernel.crypto.asymmetric-algorithm-name=RSA

mosip.kernel.signature.signature-request-id=SIGNATURE.REQUEST
mosip.kernel.signature.signature-version-id=v1.0

mosip.signed.header=response-signature

mosip.kernel.signature.cryptomanager-encrypt-url=https://host/v1/cryptomanager/private/encrypt
mosip.kernel.keymanager-service-publickey-url=https://host/v1/keymanager/publickey/{applicationId}
auth.server.validate.url=https://host/v1.0/authorize/validateToken
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
 
 response-signature=response-signature: EoYMjwMJadE-FOYupAuSwQFnUkE87_5jqqDBf9FRK75O4XBcvZws3dd
 ```
   