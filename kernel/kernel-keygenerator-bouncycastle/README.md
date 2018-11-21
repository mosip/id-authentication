## KEYGENERATOR-BOUNCYCASTLE module for kernel
This folder has KEYGENERATOR-BOUNCYCASTLE module which can be used to generate [SecretKey](https://docs.oracle.com/javase/8/docs/api/javax/crypto/SecretKey.html) and [KeyPair](https://docs.oracle.com/javase/8/docs/api/index.html?java/security/KeyPair.html).

### Api Documentation
[API Documentation <TBA>](TBA)

### Properties to be added in parent Spring Application environment 
[kernel-keygenerator-bouncycastle-dev.properties](../../config/kernel-keygenerator-bouncycastle-dev.properties)

##### The response will be [SecretKey](https://docs.oracle.com/javase/8/docs/api/javax/crypto/SecretKey.html) and [KeyPair](https://docs.oracle.com/javase/8/docs/api/index.html?java/security/KeyPair.html) for symmetric key generation and asymmetric key generation respectively.


### Usage Sample
  Usage Encryption:
 
 ```
@Autowired
	KeyGenerator keyGenerator;
	
	SecretKey secretKey=keyGenerator.getSymmetricKey();
		
 ```

 Usage Decryption:
 
 ```
@Autowired
	KeyGenerator keyGenerator;
	
	KeyPair keyPair=keyGenerator.getAsymmetricKey();
 
 ```
