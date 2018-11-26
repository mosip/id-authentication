## kernel-keymanager-softhsm
This api can be used for handling keys and certificates in SoftHSM. 

1. [Background & Design](../../design/kernel/KeyManager.md)

2. Api Documentation


```
mvn javadoc:javadoc
```

To use this api, add this to dependency list:

```
		<dependency>
			<groupId>io.mosip.kernel</groupId>
			<artifactId>kernel-keymanager-softhsm</artifactId>
			<version>1.0.0-SNAPSHOT</version>
		</dependency>
```


**Exceptions to be handled while using this functionality:**

1. KeystoreProcessingException
2. NoSuchSecurityProviderException


**Usage Sample**
  
*Usage:*
 
 Get All Alias
 
 ```
		@Autowired
		private KeymanagerInterface softhsmKeystore;

		List<String> allAlias = softhsmKeystore.getAllAlias();

		allAlias.forEach(alias -> {
			Key key = softhsmKeystore.getKey(alias);
			System.out.println(alias + "," + key);
			softhsmKeystore.deleteKey(alias);
		});

 
 ```
 
 Secret Key Demo
 
 ```
 
 		@Autowired
		private KeymanagerInterface softhsmKeystore;
		
 		KeyGenerator keyGenerator = null;
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		SecureRandom secureRandom = new SecureRandom();
		int keyBitSize = 256;
		keyGenerator.init(keyBitSize, secureRandom);
		SecretKey secretKey = keyGenerator.generateKey();

		softhsmKeystore.storeSymmetricKey(secretKey, "test-alias-secret");

		SecretKey fetchedSecretKey = softhsmKeystore.getSymmetricKey("test-alias-secret");
		System.out.println(fetchedSecretKey.toString());
```

KeyPair Key Demo

```
		@Autowired
		private KeymanagerInterface softhsmKeystore;
		
		KeyPairGenerator keyPairGenerator = null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		softhsmKeystore.storeAsymmetricKey(keyPair, "test-alias-private", 365);

		PrivateKey privateKey = softhsmKeystore.getPrivateKey("test-alias-private");

		System.out.println(privateKey.toString());
		System.out.println(privateKey.getEncoded());

		PublicKey publicKey = softhsmKeystore.getPublicKey("test-alias-private");
		System.out.println(publicKey.toString());
		System.out.println(publicKey.getEncoded());
```
