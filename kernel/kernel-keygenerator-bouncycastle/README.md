## kernel-keygenerator-bouncycastle
[Background & Design](../../design/kernel/kernel-keygenerator.md)

**Api Documentation**

[API Documentation <TBA>](TBA)

```
mvn javadoc:javadoc
```


**Maven dependency**
  
 ```
    <dependency>
		<groupId>io.mosip.kernel</groupId>
		<artifactId>kernel-keygenerator-bouncycastle</artifactId>
		<version>${project.version}</</version>
	</dependency>
 ```

**Properties to be added in parent Spring Application environment** 

[kernel-keygenerator-bouncycastle-dev.properties](../../config/kernel-keygenerator-bouncycastle-dev.properties)

**The response will be [SecretKey](https://docs.oracle.com/javase/8/docs/api/javax/crypto/SecretKey.html) and [KeyPair](https://docs.oracle.com/javase/8/docs/api/index.html?java/security/KeyPair.html) for symmetric key generation and asymmetric key generation respectively.**


**Usage Sample**



  *Usage Symmetric Key Generation:*
 
 ```
@Autowired
	KeyGenerator keyGenerator;
	
	SecretKey secretKey=keyGenerator.getSymmetricKey();
		
 ```
 
 *Output:*
 
 ```
 secretKey = o6cE0pf5eD3/hhflu4KRfz9VoSBgmZ1IJ4hma6/BVEk=
 ```

 *Usage Asymmetric Key Generation:*
 
 ```
@Autowired
	KeyGenerator keyGenerator;
	
	KeyPair keyPair=keyGenerator.getAsymmetricKey();
	
 
 ```
 
*Output:*

```
RSA Private CRT Key [41:17:f9:dc:04:5f:d1:58:d1:e5:c9:4e:91:17:39:f5:a7:ca:18:e0]
            modulus: b67a968b46b4baeea2d857d0abbae36af48ecaef27b1aa18e01a3b4dae6771eee4ea7f7f05f10bfb63b108bd7b26a2a46fe4bf71547809f945961c4cccfde080085896a1403fe10c33b881cb3b61bf38172341df1248417be0b05926b8b0d962ec98bdbb30a24f9a94cdcfaae7749e202fde3683eee57fba9d914bb34d1264e07f302d63d4c08fe2351dea212ab2edf3621dfabf5e1408e394064dc9f205372fc2a0c28f5c65cf6a3f498c804a9aeef9477e6bb7a9466c5f11d9d00ce24696c9f4e99644a2a3ddbb13c8e7426aceb5caa274f826c0e1b8e2607d7943b2e158c794c45b58c6844f5741fb3a25aef6b70bb78f238f42f730dab409ad5e2e8966e5
    public exponent: 10001

RSA Public Key [41:17:f9:dc:04:5f:d1:58:d1:e5:c9:4e:91:17:39:f5:a7:ca:18:e0]
            modulus: b67a968b46b4baeea2d857d0abbae36af48ecaef27b1aa18e01a3b4dae6771eee4ea7f7f05f10bfb63b108bd7b26a2a46fe4bf71547809f945961c4cccfde080085896a1403fe10c33b881cb3b61bf38172341df1248417be0b05926b8b0d962ec98bdbb30a24f9a94cdcfaae7749e202fde3683eee57fba9d914bb34d1264e07f302d63d4c08fe2351dea212ab2edf3621dfabf5e1408e394064dc9f205372fc2a0c28f5c65cf6a3f498c804a9aeef9477e6bb7a9466c5f11d9d00ce24696c9f4e99644a2a3ddbb13c8e7426aceb5caa274f826c0e1b8e2607d7943b2e158c794c45b58c6844f5741fb3a25aef6b70bb78f238f42f730dab409ad5e2e8966e5
    public exponent: 10001

``` 