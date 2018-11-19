package io.mosip.kernel.crypto.jce.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import java.security.SecureRandom;

import javax.crypto.spec.SecretKeySpec;

import org.junit.Before;
import org.junit.Test;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.crypto.jce.constant.SecurityMethod;
import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;
import io.mosip.kernel.crypto.jce.impl.EncryptorImpl;



public class DecryptorTest {

	private KeyPair rsaPair;

	private byte[] data;

	private EncryptorImpl MOSIPENCRYPTOR;

	private DecryptorImpl MOSIPDECRYPTOR;

	@Before
	public void setRSAUp() throws java.security.NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		generator.initialize(2048, random);
		rsaPair = generator.generateKeyPair();
		data = "abc".getBytes();
		MOSIPENCRYPTOR = new EncryptorImpl();
		MOSIPDECRYPTOR = new DecryptorImpl();
	}

	public SecretKeySpec setSymmetricUp(int length, String algo) throws java.security.NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		return new SecretKeySpec(keyBytes, algo);
	}

	@Test
	public void testRSAPKS1AsymmetricPrivateDecrypt(){
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic(), data,
				SecurityMethod.RSA_WITH_PKCS1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate(), encryptedData,
				SecurityMethod.RSA_WITH_PKCS1PADDING), isA(byte[].class));
	}

	

	@Test
	public void testRSAPKS1AsymmetricPublicDecrypt()
			{
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate(), data,
				SecurityMethod.RSA_WITH_PKCS1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPublicDecrypt(rsaPair.getPublic(), encryptedData,
				SecurityMethod.RSA_WITH_PKCS1PADDING), isA(byte[].class));
	}
 
	@Test
	public void testAESSymmetricDecrypt() throws java.security.NoSuchAlgorithmException
			{
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		byte[] encryptedData = MOSIPENCRYPTOR.symmetricEncrypt(secretKeySpec, data,
				SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
		assertThat(MOSIPDECRYPTOR.symmetricDecrypt(secretKeySpec, encryptedData,
				SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING), isA(byte[].class));
	}
	
	@Test(expected=NoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptNoSuchMethod(){
	  MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate(), "aa".getBytes(),
				SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}

	

	@Test(expected=NoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPublicDecryptNoSuchMethod()
			{
		MOSIPDECRYPTOR.asymmetricPublicDecrypt(rsaPair.getPublic(), "aa".getBytes(),
				SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}
	
	@Test(expected=NoSuchAlgorithmException.class)
	public void testAESSymmetricDecryptNoSuchMethod() throws java.security.NoSuchAlgorithmException
			{
		MOSIPDECRYPTOR.symmetricDecrypt(setSymmetricUp(32, "AES"), "aa".getBytes(),
				SecurityMethod.RSA_WITH_PKCS1PADDING);
	}
 
	@Test(expected=InvalidKeyException.class)
	public void testAESSymmetricDecryptInvalidKey() throws java.security.NoSuchAlgorithmException
			{
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		byte[] encryptedData = MOSIPENCRYPTOR.symmetricEncrypt(secretKeySpec, data,
				SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
		MOSIPDECRYPTOR.symmetricDecrypt(null, encryptedData,
				SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}
	
	@Test(expected=InvalidDataException.class)
	public void testAESSymmetricDecryptInvalidDataArrayIndexOutOfBounds() throws java.security.NoSuchAlgorithmException
			{
		MOSIPDECRYPTOR.symmetricDecrypt(setSymmetricUp(32, "AES"), "aa".getBytes(),
				SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}

	@Test(expected=InvalidDataException.class)
	public void testAESSymmetricDecryptInvalidDataIllegalBlockSize() throws java.security.NoSuchAlgorithmException
			{
		MOSIPDECRYPTOR.symmetricDecrypt(setSymmetricUp(32, "AES"), new byte[121],
				SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}
	
	@Test(expected=InvalidDataException.class)
	public void testAESSymmetricDecryptInvalidDataBadPadding() throws java.security.NoSuchAlgorithmException
			{
		MOSIPDECRYPTOR.symmetricDecrypt(setSymmetricUp(32, "AES"), new byte[32],
				SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}
	
	@Test(expected=InvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptInvalidDataIllegalBlockSize(){
		MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate(), new byte[121],
				SecurityMethod.RSA_WITH_PKCS1PADDING);
	}
	
	@Test(expected=InvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptInvalidDataBadPadding(){
		MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate(), new byte[32],
				SecurityMethod.RSA_WITH_PKCS1PADDING);
	}
}
