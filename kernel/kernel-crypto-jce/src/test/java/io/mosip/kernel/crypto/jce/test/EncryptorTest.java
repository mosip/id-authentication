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
import io.mosip.kernel.core.crypto.exception.NullDataException;
import io.mosip.kernel.core.crypto.exception.NullMethodException;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.crypto.jce.constant.SecurityMethod;
import io.mosip.kernel.crypto.jce.impl.EncryptorImpl;

public class EncryptorTest {

	private EncryptorImpl MOSIPENCRYPTOR;

	private KeyPair rsaPair;

	private byte[] data;

	@Before
	public void setRSAUp() throws java.security.NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		generator.initialize(2048, random);
		rsaPair = generator.generateKeyPair();
		data = "a".getBytes();
		MOSIPENCRYPTOR = new EncryptorImpl();
	}

	public SecretKeySpec setSymmetricUp(int length, String algo)
			throws java.security.NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		return new SecretKeySpec(keyBytes, algo);
	}

	@Test
	public void testRSAPKS1AsymmetricPrivateEncrypt() {
		assertThat(
				MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate(),
						data, SecurityMethod.RSA_WITH_PKCS1PADDING),
				isA(byte[].class));
	}

	@Test
	public void testRSAPKS1AsymmetricPublicEncrypt() {
		assertThat(
				MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic(),
						data, SecurityMethod.RSA_WITH_PKCS1PADDING),
				isA(byte[].class));
	}

	@Test
	public void testAESSymmetricEncrypt() throws java.security.NoSuchAlgorithmException {
		assertThat(
				MOSIPENCRYPTOR.symmetricEncrypt(setSymmetricUp(32, "AES"), data,
						SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING),
				isA(byte[].class));
	}

	@Test(expected = InvalidKeyException.class)
	public void testRSAPKS1AsymmetricInvalidKey() {
		MOSIPENCRYPTOR.asymmetricPrivateEncrypt(null, data,
				SecurityMethod.RSA_WITH_PKCS1PADDING);
	}

	@Test(expected = NullMethodException.class)
	public void testRSAPKS1AsymmetricNullMethod() {
		MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate(), data,
				null);
	}

	@Test(expected = NullDataException.class)
	public void testRSAPKS1AsymmetricNullData() {
		MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate(), null,
				SecurityMethod.RSA_WITH_PKCS1PADDING);
	}

	@Test(expected = InvalidDataException.class)
	public void testRSAPKS1AsymmetricInvalidData() {
		MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate(),
				"".getBytes(), SecurityMethod.RSA_WITH_PKCS1PADDING);
	}

	@Test(expected = NoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptNoSuchMethod() {
		MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate(), data,
				SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}

	@Test(expected = NoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPublicEncryptNoSuchMethod() {
		MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic(), data,
				SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING);
	}

	@Test(expected = NoSuchAlgorithmException.class)
	public void testAESSymmetricEncryptNoSuchMethod()
			throws java.security.NoSuchAlgorithmException {
		MOSIPENCRYPTOR.symmetricEncrypt(setSymmetricUp(32, "AES"), data,
				SecurityMethod.RSA_WITH_PKCS1PADDING);
	}
	
	@Test(expected=InvalidKeyException.class)
	public void testAESSymmetricEncryptInvalidKey() throws java.security.NoSuchAlgorithmException {
		assertThat(
				MOSIPENCRYPTOR.symmetricEncrypt(null, data,
						SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING),
				isA(byte[].class));
	}

}
