package io.mosip.kernel.crypto.jce.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.spec.SecretKeySpec;

import org.junit.Before;
import org.junit.Test;

import io.mosip.kernel.crypto.jce.constant.MosipSecurityMethod;
import io.mosip.kernel.crypto.jce.decryption.DecryptorImpl;
import io.mosip.kernel.crypto.jce.encryption.EncryptorImpl;
import io.mosip.kernel.crypto.jce.exception.MosipInvalidDataException;
import io.mosip.kernel.crypto.jce.exception.MosipInvalidKeyException;
import io.mosip.kernel.crypto.jce.exception.MosipNoSuchAlgorithmException;



public class MosipDecryptorTest {

	private KeyPair rsaPair;

	private byte[] data;

	private EncryptorImpl MOSIPENCRYPTOR;

	private DecryptorImpl MOSIPDECRYPTOR;

	@Before
	public void setRSAUp() throws NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		generator.initialize(2048, random);
		rsaPair = generator.generateKeyPair();
		data = "abc".getBytes();
		MOSIPENCRYPTOR = new EncryptorImpl();
		MOSIPDECRYPTOR = new DecryptorImpl();
	}

	public SecretKeySpec setSymmetricUp(int length, String algo) throws NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		return new SecretKeySpec(keyBytes, algo);
	}

	@Test
	public void testRSAPKS1AsymmetricPrivateDecrypt(){
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic(), data,
				MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate(), encryptedData,
				MosipSecurityMethod.RSA_WITH_PKCS1PADDING), isA(byte[].class));
	}

	

	@Test
	public void testRSAPKS1AsymmetricPublicDecrypt()
			{
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate(), data,
				MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPublicDecrypt(rsaPair.getPublic(), encryptedData,
				MosipSecurityMethod.RSA_WITH_PKCS1PADDING), isA(byte[].class));
	}
 
	@Test
	public void testAESSymmetricDecrypt() throws NoSuchAlgorithmException
			{
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		byte[] encryptedData = MOSIPENCRYPTOR.symmetricEncrypt(secretKeySpec, data,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
		assertThat(MOSIPDECRYPTOR.symmetricDecrypt(secretKeySpec, encryptedData,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING), isA(byte[].class));
	}
	
	@Test(expected=MosipNoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptNoSuchMethod(){
	  MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate(), "aa".getBytes(),
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}

	

	@Test(expected=MosipNoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPublicDecryptNoSuchMethod()
			{
		MOSIPDECRYPTOR.asymmetricPublicDecrypt(rsaPair.getPublic(), "aa".getBytes(),
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}
	
	@Test(expected=MosipNoSuchAlgorithmException.class)
	public void testAESSymmetricDecryptNoSuchMethod() throws NoSuchAlgorithmException
			{
		MOSIPDECRYPTOR.symmetricDecrypt(setSymmetricUp(32, "AES"), "aa".getBytes(),
				MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
	}
 
	@Test(expected=MosipInvalidKeyException.class)
	public void testAESSymmetricDecryptInvalidKey() throws NoSuchAlgorithmException
			{
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		byte[] encryptedData = MOSIPENCRYPTOR.symmetricEncrypt(secretKeySpec, data,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
		MOSIPDECRYPTOR.symmetricDecrypt(null, encryptedData,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}
	
	@Test(expected=MosipInvalidDataException.class)
	public void testAESSymmetricDecryptInvalidDataArrayIndexOutOfBounds() throws NoSuchAlgorithmException
			{
		MOSIPDECRYPTOR.symmetricDecrypt(setSymmetricUp(32, "AES"), "aa".getBytes(),
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}

	@Test(expected=MosipInvalidDataException.class)
	public void testAESSymmetricDecryptInvalidDataIllegalBlockSize() throws NoSuchAlgorithmException
			{
		MOSIPDECRYPTOR.symmetricDecrypt(setSymmetricUp(32, "AES"), new byte[121],
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}
	
	@Test(expected=MosipInvalidDataException.class)
	public void testAESSymmetricDecryptInvalidDataBadPadding() throws NoSuchAlgorithmException
			{
		MOSIPDECRYPTOR.symmetricDecrypt(setSymmetricUp(32, "AES"), new byte[32],
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}
	
	@Test(expected=MosipInvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptInvalidDataIllegalBlockSize(){
		MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate(), new byte[121],
				MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
	}
	
	@Test(expected=MosipInvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptInvalidDataBadPadding(){
		MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate(), new byte[32],
				MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
	}
}
