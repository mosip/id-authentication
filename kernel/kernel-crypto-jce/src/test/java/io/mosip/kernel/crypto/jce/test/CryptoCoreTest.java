package io.mosip.kernel.crypto.jce.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.crypto.exception.SignatureException;
import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CryptoCoreTest {

	private static final String MOCKAAD = "MOCKAAD";

	@Autowired
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;

	private KeyPair rsaPair;

	private byte[] data;
	
	private byte[] keyBytes;
	
	private final SecureRandom random = new SecureRandom();

	@Before
	public void init() throws java.security.NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048, random);
		rsaPair = generator.generateKeyPair();
		data = "test".getBytes();
	    keyBytes = new byte[16];
		random.nextBytes(keyBytes);

	}

	private SecretKeySpec setSymmetricUp(int length, String algo) throws java.security.NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		return new SecretKeySpec(keyBytes, algo);
	}

	@Test
	public void testAsymmetricPublicEncrypt() {
		assertThat(cryptoCore.asymmetricEncrypt(rsaPair.getPublic(), data), isA(byte[].class));
	}

	@Test
	public void testAESSymmetricEncrypt() throws java.security.NoSuchAlgorithmException {
		assertThat(cryptoCore.symmetricEncrypt(setSymmetricUp(32, "AES"), data,null, MOCKAAD.getBytes()), isA(byte[].class));
	}

	@Test
	public void testAESSymmetricSaltEncrypt() throws java.security.NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[16];
		random.nextBytes(keyBytes);
		assertThat(cryptoCore.symmetricEncrypt(setSymmetricUp(32, "AES"), data, keyBytes, MOCKAAD.getBytes()),
				isA(byte[].class));
	}

	@Test(expected = NullPointerException.class)
	public void testAESSymmetricEncryptNullKey() throws java.security.NoSuchAlgorithmException {
		cryptoCore.symmetricEncrypt(null, data, MOCKAAD.getBytes());
	}

	@Test(expected = InvalidKeyException.class)
	public void testAESSymmetricEncryptInvalidKey() throws java.security.NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[15];
		random.nextBytes(keyBytes);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
		cryptoCore.symmetricEncrypt(secretKeySpec, data, MOCKAAD.getBytes());
	}

	@Test(expected = InvalidKeyException.class)
	public void testAESSymmetricEncryptSaltInvalidKey() throws java.security.NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(15, "AES");
		cryptoCore.symmetricEncrypt(secretKeySpec, data, keyBytes, MOCKAAD.getBytes());
	}
	
	
	@Test(expected = InvalidKeyException.class)
	public void testAsymmetricPublicInvalidKeyEncrypt() throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA");
		generator.initialize(2048, random);
		KeyPair invalidKeyPair=generator.generateKeyPair();
		assertThat(cryptoCore.asymmetricEncrypt(invalidKeyPair.getPublic(), data), isA(byte[].class));
	}
	
	@Test
	public void testHash() throws NoSuchAlgorithmException, InvalidKeySpecException {
		assertThat(cryptoCore.hash(data, keyBytes), isA(String.class));
	}
	
	@Test
	public void testSign() throws NoSuchAlgorithmException, InvalidKeySpecException {
		assertThat(cryptoCore.sign(data,rsaPair.getPrivate()), isA(String.class));
	}
	
	@Test(expected = InvalidKeyException.class)
	public void testSignInvalidKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA");
		generator.initialize(2048, random);
		KeyPair invalidKeyPair=generator.generateKeyPair();
		assertThat(cryptoCore.sign(data,invalidKeyPair.getPrivate()), isA(String.class));
	}
	
	@Test
	public void testVerify() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String signature= cryptoCore.sign(data,rsaPair.getPrivate());
		assertThat(cryptoCore.verifySignature(data, signature, rsaPair.getPublic()), is(true));
	}
	
	@Test(expected = SignatureException.class)
	public void testVerifySignatureException() throws NoSuchAlgorithmException, InvalidKeySpecException {
		assertThat(cryptoCore.verifySignature(data, "Invaliddata", rsaPair.getPublic()), is(true));
	}
	
	@Test(expected = SignatureException.class)
	public void testVerifySignatureNullException() throws NoSuchAlgorithmException, InvalidKeySpecException {
		assertThat(cryptoCore.verifySignature(data, null, rsaPair.getPublic()), is(true));
	}
	
	@Test(expected = InvalidKeyException.class)
	public void testVerifyInvalidKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA");
		generator.initialize(2048, random);
		KeyPair invalidKeyPair=generator.generateKeyPair();
		String signature= cryptoCore.sign(data,rsaPair.getPrivate());
		assertThat(cryptoCore.verifySignature(data, signature, invalidKeyPair.getPublic()), is(true));
	}
	
	@Test
	public void testRandom() throws NoSuchAlgorithmException, InvalidKeySpecException {
		assertThat(cryptoCore.random(), isA(SecureRandom.class));
	}
	
	@Test
	public void testAsymmetricDecrypt() {
		byte[] encryptedData = cryptoCore.asymmetricEncrypt(rsaPair.getPublic(), data);
		assertThat(cryptoCore.asymmetricDecrypt(rsaPair.getPrivate(), encryptedData), isA(byte[].class));
	}



	@Test
	public void testAESSymmetricDecrypt() throws java.security.NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		byte[] encryptedData = cryptoCore.symmetricEncrypt(secretKeySpec, data,MOCKAAD.getBytes());
		assertThat(cryptoCore.symmetricDecrypt(secretKeySpec, encryptedData,null,MOCKAAD.getBytes()), isA(byte[].class));
	}
	
	@Test
	public void testAESSymmetricSaltDecrypt() throws java.security.NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		byte[] encryptedData = cryptoCore.symmetricEncrypt(secretKeySpec, data,MOCKAAD.getBytes(),keyBytes);
		assertThat(cryptoCore.symmetricDecrypt(secretKeySpec, encryptedData,MOCKAAD.getBytes(),keyBytes), isA(byte[].class));
	}

	@Test(expected = NullPointerException.class)
	public void testAESSymmetricDecryptInvalidKey() throws java.security.NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		byte[] encryptedData = cryptoCore.symmetricEncrypt(secretKeySpec, data,MOCKAAD.getBytes());
		cryptoCore.symmetricDecrypt(null, encryptedData,MOCKAAD.getBytes());
	}

	@Test(expected = InvalidDataException.class)
	public void testAESSymmetricDecryptInvalidDataArrayIndexOutOfBounds()
			throws java.security.NoSuchAlgorithmException {
		cryptoCore.symmetricDecrypt(setSymmetricUp(32, "AES"), "aa".getBytes(),MOCKAAD.getBytes());
	}

	@Test(expected = InvalidDataException.class)
	public void testAESSymmetricDecryptInvalidDataIllegalBlockSize() throws java.security.NoSuchAlgorithmException {
		cryptoCore.symmetricDecrypt(setSymmetricUp(32, "AES"), new byte[121],MOCKAAD.getBytes());
	}

	 @Test(expected=InvalidKeyException.class)
	public void testAESSymmetricDecryptInvalidKeyLength() throws java.security.NoSuchAlgorithmException {
		 SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		 byte[] encryptedData = cryptoCore.symmetricEncrypt(secretKeySpec, data,MOCKAAD.getBytes());
		 cryptoCore.symmetricDecrypt(setSymmetricUp(15, "AES"), encryptedData,null,MOCKAAD.getBytes());
	}
	 
	 @Test(expected=InvalidKeyException.class)
		public void testAESSymmetricDecryptSaltInvalidKeyLength() throws java.security.NoSuchAlgorithmException {
			 SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
			 byte[] encryptedData = cryptoCore.symmetricEncrypt(secretKeySpec, data,MOCKAAD.getBytes());
			 cryptoCore.symmetricDecrypt(setSymmetricUp(15, "AES"), encryptedData,keyBytes,MOCKAAD.getBytes());
		}

	@Test(expected = InvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptInvalidDataIllegalBlockSize() {
		cryptoCore.asymmetricDecrypt(rsaPair.getPrivate(), new byte[121]);
	}

	
	@Test(expected = InvalidKeyException.class)
	public void testAsymmetricPublicInvalidKeyDecrypt() throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA");
		generator.initialize(2048, random);
		KeyPair invalidKeyPair=generator.generateKeyPair();
		byte[] encryptedData = cryptoCore.asymmetricEncrypt(rsaPair.getPublic(), data);
		assertThat(cryptoCore.asymmetricDecrypt(invalidKeyPair.getPrivate(), encryptedData), isA(byte[].class));
	}

}