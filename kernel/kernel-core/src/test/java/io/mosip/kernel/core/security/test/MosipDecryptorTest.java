package io.mosip.kernel.core.security.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.spec.SecretKeySpec;

import org.junit.Before;
import org.junit.Test;

import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.decryption.MosipDecryptor;
import io.mosip.kernel.core.security.encryption.MosipEncryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.security.exception.MosipNoSuchAlgorithmException;
import io.mosip.kernel.core.security.exception.MosipNullDataException;

public class MosipDecryptorTest {

	private KeyPair rsaPair;

	private byte[] data;

	@Before
	public void setRSAUp() throws NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		generator.initialize(2048, random);
		rsaPair = generator.generateKeyPair();
		data = "abc".getBytes();
	}

	public SecretKeySpec setSymmetricUp(int length, String algo) throws NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		return new SecretKeySpec(keyBytes, algo);
	}

	@Test
	public void testRSAPKS1AsymmetricPrivateDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
		assertThat(MosipDecryptor.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSAMD5AsymmetricPrivateDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING);
		assertThat(MosipDecryptor.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSASHA3512AsymmetricPrivateDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING);
		assertThat(MosipDecryptor.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSAPKS1AsymmetricPublicDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
		assertThat(MosipDecryptor.asymmetricPublicDecrypt(rsaPair.getPublic().getEncoded(), encryptedData,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSAMD5AsymmetricPublicDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING);
		assertThat(MosipDecryptor.asymmetricPublicDecrypt(rsaPair.getPublic().getEncoded(), encryptedData,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSASHA3512AsymmetricPublicDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING);
		assertThat(MosipDecryptor.asymmetricPublicDecrypt(rsaPair.getPublic().getEncoded(), encryptedData,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSAPKS1AsymmetricPrivateDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
		assertThat(MosipDecryptor.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				MosipSecurityMethod.RSA_WITH_PKCS1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSAMD5AsymmetricPrivateDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				MosipSecurityMethod.RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING);
		assertThat(MosipDecryptor.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				MosipSecurityMethod.RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSASHA3512AsymmetricPrivateDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				MosipSecurityMethod.RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING);
		assertThat(MosipDecryptor.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				MosipSecurityMethod.RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSAPKS1AsymmetricPublicDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
		assertThat(MosipDecryptor.asymmetricPublicDecrypt(rsaPair.getPublic().getEncoded(), encryptedData,
				MosipSecurityMethod.RSA_WITH_PKCS1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSAMD5AsymmetricPublicDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				MosipSecurityMethod.RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING);
		assertThat(MosipDecryptor.asymmetricPublicDecrypt(rsaPair.getPublic().getEncoded(), encryptedData,
				MosipSecurityMethod.RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSASHA3512AsymmetricPublicDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				MosipSecurityMethod.RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING);
		assertThat(MosipDecryptor.asymmetricPublicDecrypt(rsaPair.getPublic().getEncoded(), encryptedData,
				MosipSecurityMethod.RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testAESSymmetricDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(16, "AES");
		byte[] encryptedData = MosipEncryptor.symmetricEncrypt(secretKeySpec.getEncoded(), data,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
		assertThat(MosipDecryptor.symmetricDecrypt(secretKeySpec.getEncoded(), encryptedData,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING), isA(byte[].class));
	}

	@Test
	public void testDESSymmetricDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(8, "DES");
		byte[] encryptedData = MosipEncryptor.symmetricEncrypt(secretKeySpec.getEncoded(), data,
				MosipSecurityMethod.DES_WITH_CBC_AND_PKCS7PADDING);
		assertThat(MosipDecryptor.symmetricDecrypt(secretKeySpec.getEncoded(), encryptedData,
				MosipSecurityMethod.DES_WITH_CBC_AND_PKCS7PADDING), isA(byte[].class));
	}

	@Test
	public void testTwoFishSymmetricDecrypt()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(16, "TWOFISH");
		byte[] encryptedData = MosipEncryptor.symmetricEncrypt(secretKeySpec.getEncoded(), data,
				MosipSecurityMethod.TWOFISH_WITH_CBC_AND_PKCS7PADDING);
		assertThat(MosipDecryptor.symmetricDecrypt(secretKeySpec.getEncoded(), encryptedData,
				MosipSecurityMethod.TWOFISH_WITH_CBC_AND_PKCS7PADDING), isA(byte[].class));
	}

	@Test(expected = MosipInvalidKeyException.class)
	public void testAESSymmetricDecryptMosipIllegalArgument()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(16, "AES");
		byte[] encryptedData = MosipEncryptor.symmetricEncrypt(secretKeySpec.getEncoded(), data,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
		MosipDecryptor.symmetricDecrypt("".getBytes(), encryptedData,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	@Test(expected = MosipInvalidDataException.class)
	public void testAESSymmetricDecryptMosipDataLength()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		MosipDecryptor.symmetricDecrypt(setSymmetricUp(16, "AES").getEncoded(), "".getBytes(),
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	@Test(expected = MosipInvalidDataException.class)
	public void testAESSymmetricDecryptMosipInvalidCipherText()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		MosipDecryptor.symmetricDecrypt(setSymmetricUp(16, "AES").getEncoded(),
				"sssssssssccccccccccccccccccccccccccccccccccccccc".getBytes(),
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	@Test(expected = MosipNoSuchAlgorithmException.class)
	public void testAESSymmetricDecryptMosipNoSuchMethod()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(16, "AES");
		byte[] encryptedData = MosipEncryptor.symmetricEncrypt(secretKeySpec.getEncoded(), data,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
		MosipDecryptor.symmetricDecrypt(secretKeySpec.getEncoded(), encryptedData,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING);
	}

	@Test(expected = MosipNoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPublicDecryptMosipNoSuchMethod()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
		MosipDecryptor.asymmetricPublicDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	@Test(expected = MosipNoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptMosipNoSuchMethod()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
		MosipDecryptor.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	@Test(expected = MosipInvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptMosipDataLength()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {

		MosipDecryptor.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), "".getBytes(),
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

	@Test(expected = MosipInvalidKeyException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptMosipInvalidKey()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		byte[] encryptedData = MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
		MosipDecryptor.asymmetricPrivateDecrypt(rsaPair.getPublic().getEncoded(), encryptedData,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

	@Test(expected = MosipNullDataException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptNullData()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		MosipDecryptor.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), null,
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

	@Test(expected = MosipInvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptInvalidCipher()
			throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		MosipDecryptor.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), new byte[500],
				MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

}
