package io.mosip.kernel.security.cipher.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.spec.SecretKeySpec;

import org.junit.Before;
import org.junit.Test;

import io.mosip.kernel.security.cipher.constant.MosipSecurityMethod;
import io.mosip.kernel.security.cipher.encryption.EncryptorImpl;
import io.mosip.kernel.security.cipher.exception.MosipInvalidDataException;
import io.mosip.kernel.security.cipher.exception.MosipInvalidKeyException;
import io.mosip.kernel.security.cipher.exception.MosipNoSuchAlgorithmException;
import io.mosip.kernel.security.cipher.exception.MosipNullDataException;
import io.mosip.kernel.security.cipher.exception.MosipNullMethodException;

public class MosipEncryptorTest {

	private EncryptorImpl MOSIPENCRYPTOR;

	private KeyPair rsaPair;

	private byte[] data;

	@Before
	public void setRSAUp() throws NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		generator.initialize(2048, random);
		rsaPair = generator.generateKeyPair();
		data = "a".getBytes();
		MOSIPENCRYPTOR = new EncryptorImpl();
	}

	public SecretKeySpec setSymmetricUp(int length, String algo)
			throws NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		return new SecretKeySpec(keyBytes, algo);
	}

	@Test
	public void testRSAPKS1AsymmetricPrivateEncrypt() {
		assertThat(
				MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate(),
						data, MosipSecurityMethod.RSA_WITH_PKCS1PADDING),
				isA(byte[].class));
	}

	@Test
	public void testRSAPKS1AsymmetricPublicEncrypt() {
		assertThat(
				MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic(),
						data, MosipSecurityMethod.RSA_WITH_PKCS1PADDING),
				isA(byte[].class));
	}

	@Test
	public void testAESSymmetricEncrypt() throws NoSuchAlgorithmException {
		assertThat(
				MOSIPENCRYPTOR.symmetricEncrypt(setSymmetricUp(32, "AES"), data,
						MosipSecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING),
				isA(byte[].class));
	}

	@Test(expected = MosipInvalidKeyException.class)
	public void testRSAPKS1AsymmetricInvalidKey() {
		MOSIPENCRYPTOR.asymmetricPrivateEncrypt(null, data,
				MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
	}

	@Test(expected = MosipNullMethodException.class)
	public void testRSAPKS1AsymmetricNullMethod() {
		MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate(), data,
				null);
	}

	@Test(expected = MosipNullDataException.class)
	public void testRSAPKS1AsymmetricNullData() {
		MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate(), null,
				MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
	}

	@Test(expected = MosipInvalidDataException.class)
	public void testRSAPKS1AsymmetricInvalidData() {
		MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate(),
				"".getBytes(), MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
	}

	@Test(expected = MosipNoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptNoSuchMethod() {
		MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate(), data,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}

	@Test(expected = MosipNoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPublicEncryptNoSuchMethod() {
		MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic(), data,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}

	@Test(expected = MosipNoSuchAlgorithmException.class)
	public void testAESSymmetricEncryptNoSuchMethod()
			throws NoSuchAlgorithmException {
		MOSIPENCRYPTOR.symmetricEncrypt(setSymmetricUp(32, "AES"), data,
				MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
	}
	
	@Test(expected=MosipInvalidKeyException.class)
	public void testAESSymmetricEncryptInvalidKey() throws NoSuchAlgorithmException {
		assertThat(
				MOSIPENCRYPTOR.symmetricEncrypt(null, data,
						MosipSecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING),
				isA(byte[].class));
	}

}
