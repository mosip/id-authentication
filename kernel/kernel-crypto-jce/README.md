## kernel-crypto-jce
[Background & Design](../../design/kernel/kernel-crypto.md)

**Api Documentation**

[API Documentation <TBA>](TBA)

```
mvn javadoc:javadoc
```

**Maven dependency**
  
 ```
    <dependency>
		<groupId>io.mosip.kernel</groupId>
		<artifactId>kernel-crypto-jce</artifactId>
		<version>${project.version}</version>
	</dependency>
 ```


**Properties to be added in parent Spring Application environment** 

[kernel-crypto-jce-dev.properties](../../config/kernel-crypto-jce-dev.properties)

**The inputs which have to be provided are:**
1. Key for encryption or decryption can be [SecretKey](https://docs.oracle.com/javase/8/docs/api/javax/crypto/SecretKey.html) or [PrivateKey](https://docs.oracle.com/javase/8/docs/api/java/security/PrivateKey.html) or [PublicKey](https://docs.oracle.com/javase/8/docs/api/java/security/PublicKey.html). 
2. Data for encryption in *Byte Array* format.


**The response will be *byte array* of either encrypted data or decrypted data** 

**If there is any error which occurs while encryption and decryption, it will be thrown as Exception.** 

**Exceptions to be handled while using this functionality:**
1. InvalidKeyException
2. InvalidDataException

**Usage Sample**



  
*Usage Symmetric Encryption:*
 
 ```
@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;
	
   byte[] encryptedData = encryptor.symmetricEncrypt(secretKey, dataToEncrypt);
```
 
 *Output*
 
 ```
8��ޡ����'��P�ր��|sN#�lY;����4(\M�M9�c�J
 ```
 
 *Usage Asymmetric Encryption:*
 
 ```
 @Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;
	
	encryptor.asymmetricPublicEncrypt(keyPair.getPublic(),dataToEncrypt));
	
 ```
 
  *Output*
 
 ```
S݄=Җ[<C&!r��˅Б�ɦ-�	�T��	�$0�P����e�T7����M���S��
�Ɯ�����>��T���a�Z3��0n�ɐ&F��7�[eܕ̺5#͉y�����l����t�f���𲞊�J�3�hk�Y�9�e�7i�k����
)&�������Ϩ8�H���=a�l�Fʷ���'d��؆x��K���0�x�ۦ.m��Պd"Q��C�����c��hvϟi�S��q�Q��
 ```

 *Usage Symmetric Decryption:*
 
 ```
 @Autowired 
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;
	
	byte[] decryptedData = decryptor.symmetricDecrypt(secretKey, encryptedData);
```


*Output*

```
This is Plain Text
```

*Usage Asymmetric Decryption:*

```
@Autowired 
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;
	
byte[] decryptedData = decryptor.asymmetricPrivateDecrypt(privatekey, encryptedData);
```

*Output*

```
This is Plain Text
```
