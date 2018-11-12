package io.mosip.kernel.crypto.bouncycastle.test;

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
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.crypto.bouncycastle.constant.SecurityMethod;
import io.mosip.kernel.crypto.bouncycastle.impl.DecryptorImpl;
import io.mosip.kernel.crypto.bouncycastle.impl.EncryptorImpl;

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
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSAMD5AsymmetricPrivateDecrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSASHA3512AsymmetricPrivateDecrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSAPKS1AsymmetricPublicDecrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPublicDecrypt(rsaPair.getPublic().getEncoded(), encryptedData,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSAMD5AsymmetricPublicDecrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPublicDecrypt(rsaPair.getPublic().getEncoded(), encryptedData,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSASHA3512AsymmetricPublicDecrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPublicDecrypt(rsaPair.getPublic().getEncoded(), encryptedData,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSAPKS1AsymmetricPrivateDecrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.RSA_WITH_PKCS1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				SecurityMethod.RSA_WITH_PKCS1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSAMD5AsymmetricPrivateDecrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				SecurityMethod.RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSASHA3512AsymmetricPrivateDecrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				SecurityMethod.RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSAPKS1AsymmetricPublicDecrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.RSA_WITH_PKCS1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPublicDecrypt(rsaPair.getPublic().getEncoded(), encryptedData,
				SecurityMethod.RSA_WITH_PKCS1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSAMD5AsymmetricPublicDecrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPublicDecrypt(rsaPair.getPublic().getEncoded(), encryptedData,
				SecurityMethod.RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSASHA3512AsymmetricPublicDecrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING);
		assertThat(MOSIPDECRYPTOR.asymmetricPublicDecrypt(rsaPair.getPublic().getEncoded(), encryptedData,
				SecurityMethod.RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testAESSymmetricDecrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(16, "AES");
		byte[] encryptedData = MOSIPENCRYPTOR.symmetricEncrypt(secretKeySpec.getEncoded(), data,
				SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
		assertThat(MOSIPDECRYPTOR.symmetricDecrypt(secretKeySpec.getEncoded(), encryptedData,
				SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING), isA(byte[].class));
	}

	@Test
	public void testDESSymmetricDecrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(8, "DES");
		byte[] encryptedData = MOSIPENCRYPTOR.symmetricEncrypt(secretKeySpec.getEncoded(), data,
				SecurityMethod.DES_WITH_CBC_AND_PKCS7PADDING);
		assertThat(MOSIPDECRYPTOR.symmetricDecrypt(secretKeySpec.getEncoded(), encryptedData,
				SecurityMethod.DES_WITH_CBC_AND_PKCS7PADDING), isA(byte[].class));
	}

	@Test
	public void testTwoFishSymmetricDecrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(16, "TWOFISH");
		byte[] encryptedData = MOSIPENCRYPTOR.symmetricEncrypt(secretKeySpec.getEncoded(), data,
				SecurityMethod.TWOFISH_WITH_CBC_AND_PKCS7PADDING);
		assertThat(MOSIPDECRYPTOR.symmetricDecrypt(secretKeySpec.getEncoded(), encryptedData,
				SecurityMethod.TWOFISH_WITH_CBC_AND_PKCS7PADDING), isA(byte[].class));
	}

	@Test(expected = InvalidKeyException.class)
	public void testAESSymmetricDecryptMosipIllegalArgument()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(16, "AES");
		byte[] encryptedData = MOSIPENCRYPTOR.symmetricEncrypt(secretKeySpec.getEncoded(), data,
				SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
		MOSIPDECRYPTOR.symmetricDecrypt("".getBytes(), encryptedData,
				SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	@Test(expected = InvalidDataException.class)
	public void testAESSymmetricDecryptMosipDataLength()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		MOSIPDECRYPTOR.symmetricDecrypt(setSymmetricUp(16, "AES").getEncoded(), "".getBytes(),
				SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	@Test(expected = InvalidDataException.class)
	public void testAESSymmetricDecryptMosipInvalidCipherText()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		MOSIPDECRYPTOR.symmetricDecrypt(setSymmetricUp(16, "AES").getEncoded(),
				"sssssssssccccccccccccccccccccccccccccccccccccccc".getBytes(),
				SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	@Test(expected = NoSuchAlgorithmException.class)
	public void testAESSymmetricDecryptMosipNoSuchMethod()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(16, "AES");
		byte[] encryptedData = MOSIPENCRYPTOR.symmetricEncrypt(secretKeySpec.getEncoded(), data,
				SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
		MOSIPDECRYPTOR.symmetricDecrypt(secretKeySpec.getEncoded(), encryptedData,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING);
	}

	@Test(expected = NoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPublicDecryptMosipNoSuchMethod()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
		MOSIPDECRYPTOR.asymmetricPublicDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	@Test(expected = NoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptMosipNoSuchMethod()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
		MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), encryptedData,
				SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	@Test(expected = InvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptMosipDataLength()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {

		MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), "".getBytes(),
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

	@Test(expected = InvalidKeyException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptMosipInvalidKey()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		byte[] encryptedData = MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
		MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPublic().getEncoded(), encryptedData,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

	@Test(expected = NullDataException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptNullData()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), null,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

	@Test(expected = InvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptInvalidCipher()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		MOSIPDECRYPTOR.asymmetricPrivateDecrypt(rsaPair.getPrivate().getEncoded(), new byte[500],
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

}
