## kernel-crypto-jce
This folder has kernel-crypto-jce module which can be used to encrypt and decrypt data.

[Background & Design](../../design/kernel/kernel-crypto.md)

### Api Documentation
[API Documentation <TBA>](TBA)

```
mvn javadoc:javadoc
```

### Properties to be added in parent Spring Application environment 
[kernel-crypto-jce-dev.properties](../../config/kernel-crypto-jce-dev.properties)

### The inputs which have to be provided are:
1. Key for encryption or decryption can be [SecretKey](https://docs.oracle.com/javase/8/docs/api/javax/crypto/SecretKey.html) or [PrivateKey](https://docs.oracle.com/javase/8/docs/api/java/security/PrivateKey.html) or [PublicKey](https://docs.oracle.com/javase/8/docs/api/java/security/PublicKey.html). 
2. Data for encryption in *Byte Array* format.


##### The response will be *byte array* of either encrypted data or decrypted data 

##### If there is any error which occurs while encryption and decryption, it will be thrown as Exception. 

### Exceptions to be handled while using this functionality:
1. InvalidKeyException
2. InvalidDataException

### Usage Sample
  Usage Encryption:
 
 ```
@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;
	
   byte[] encryptedData = encryptor.symmetricEncrypt(secretKey, dataToEncrypt);
   byte[] encryptedData =  encryptor.asymmetricPublicEncrypt(publicKey, dataToEncrypt);
 
 ```

 Usage Decryption:
 
 ```
 @Autowired 
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;
	
	byte[] decryptedData = decryptor.symmetricDecrypt(secretKey, encryptedData);
	byte[] decryptedData = decryptor.asymmetricPrivateDecrypt(privatekey, encryptedData);
 
 ```
