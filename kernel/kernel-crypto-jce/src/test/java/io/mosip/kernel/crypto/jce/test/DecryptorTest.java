package io.mosip.kernel.crypto.jce.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

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
import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DecryptorTest {

	@Autowired
	private Encryptor<PrivateKey, PublicKey, SecretKey> encryptorImpl;

	@Autowired
	private Decryptor<PrivateKey, PublicKey, SecretKey> decryptorImpl;
	
	private KeyPair rsaPair;

	private byte[] data;

	

	@Before
	public void setRSAUp() throws java.security.NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		generator.initialize(2048, random);
		rsaPair = generator.generateKeyPair();
		data = "abc".getBytes();
	}

	public SecretKeySpec setSymmetricUp(int length, String algo) throws java.security.NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		return new SecretKeySpec(keyBytes, algo);
	}

	@Test
	public void testRSAPKS1AsymmetricPrivateDecrypt(){
		byte[] encryptedData = encryptorImpl.asymmetricPublicEncrypt(rsaPair.getPublic(), data);
		assertThat(decryptorImpl.asymmetricPrivateDecrypt(rsaPair.getPrivate(), encryptedData), isA(byte[].class));
	}

	

	@Test
	public void testRSAPKS1AsymmetricPublicDecrypt()
			{
		byte[] encryptedData = encryptorImpl.asymmetricPrivateEncrypt(rsaPair.getPrivate(), data);
		assertThat(decryptorImpl.asymmetricPublicDecrypt(rsaPair.getPublic(), encryptedData), isA(byte[].class));
	}
 
	@Test
	public void testAESSymmetricDecrypt() throws java.security.NoSuchAlgorithmException
			{
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		byte[] encryptedData = encryptorImpl.symmetricEncrypt(secretKeySpec, data);
		assertThat(decryptorImpl.symmetricDecrypt(secretKeySpec, encryptedData), isA(byte[].class));
	}
	
	


 
	@Test(expected=InvalidKeyException.class)
	public void testAESSymmetricDecryptInvalidKey() throws java.security.NoSuchAlgorithmException
			{
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		byte[] encryptedData = encryptorImpl.symmetricEncrypt(secretKeySpec, data);
		decryptorImpl.symmetricDecrypt(null, encryptedData);
	}
	
	@Test(expected=InvalidDataException.class)
	public void testAESSymmetricDecryptInvalidDataArrayIndexOutOfBounds() throws java.security.NoSuchAlgorithmException
			{
		decryptorImpl.symmetricDecrypt(setSymmetricUp(32, "AES"), "aa".getBytes());
	}

	@Test(expected=InvalidDataException.class)
	public void testAESSymmetricDecryptInvalidDataIllegalBlockSize() throws java.security.NoSuchAlgorithmException
			{
		decryptorImpl.symmetricDecrypt(setSymmetricUp(32, "AES"), new byte[121]);
	}
	
	@Test(expected=InvalidDataException.class)
	public void testAESSymmetricDecryptInvalidDataBadPadding() throws java.security.NoSuchAlgorithmException
			{
		decryptorImpl.symmetricDecrypt(setSymmetricUp(32, "AES"), new byte[32]);
	}
	
	@Test(expected=InvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptInvalidDataIllegalBlockSize(){
		decryptorImpl.asymmetricPrivateDecrypt(rsaPair.getPrivate(), new byte[121]);
	}
	
	@Test(expected=InvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptInvalidDataBadPadding(){
		decryptorImpl.asymmetricPrivateDecrypt(rsaPair.getPrivate(), new byte[32]);
	}
}
