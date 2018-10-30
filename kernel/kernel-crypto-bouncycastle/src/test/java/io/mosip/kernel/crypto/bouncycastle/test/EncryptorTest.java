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
import io.mosip.kernel.core.crypto.exception.NullKeyException;
import io.mosip.kernel.core.crypto.exception.NullMethodException;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.crypto.bouncycastle.constant.SecurityMethod;
import io.mosip.kernel.crypto.bouncycastle.impl.EncryptorImpl;



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

	public SecretKeySpec setSymmetricUp(int length, String algo) throws java.security.NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		return new SecretKeySpec(keyBytes, algo);
	}

	@Test
	public void testRSAPKS1AsymmetricPrivateEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		generator.initialize(4096, random);
		rsaPair = generator.generateKeyPair();
		assertThat(MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSAMD5AsymmetricPrivateEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		assertThat(MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSASHA3512AsymmetricPrivateEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		assertThat(MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSAPKS1AsymmetricPrivateEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		assertThat(MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.RSA_WITH_PKCS1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSAMD5AsymmetricPrivateEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		assertThat(MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSASHA3512AsymmetricPrivateEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		assertThat(MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSAPKS1AsymmetricPublicEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		assertThat(MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSAMD5AsymmetricPublicEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		assertThat(MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testRSASHA3512AsymmetricPublicEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		assertThat(MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSAPKS1AsymmetricPublicEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		assertThat(MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.RSA_WITH_PKCS1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSAMD5AsymmetricPublicEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		assertThat(MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testPlainRSASHA3512AsymmetricPublicEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		assertThat(MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING), isA(byte[].class));
	}

	@Test
	public void testAESSymmetricEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {

		assertThat(MOSIPENCRYPTOR.symmetricEncrypt(setSymmetricUp(16, "AES").getEncoded(), data,
				SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING), isA(byte[].class));
	}

	@Test
	public void testDESSymmetricEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		assertThat(MOSIPENCRYPTOR.symmetricEncrypt(setSymmetricUp(8, "DES").getEncoded(), data,
				SecurityMethod.DES_WITH_CBC_AND_PKCS7PADDING), isA(byte[].class));
	}

	@Test
	public void testTwoFishSymmetricEncrypt()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		assertThat(MOSIPENCRYPTOR.symmetricEncrypt(setSymmetricUp(16, "TWOFISH").getEncoded(), data,
				SecurityMethod.TWOFISH_WITH_CBC_AND_PKCS7PADDING), isA(byte[].class));
	}

	@Test(expected = InvalidKeyException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptMosipIo()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		MOSIPENCRYPTOR.asymmetricPrivateEncrypt("xx".getBytes(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

	@Test(expected = InvalidKeyException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptMosipInvalidKeyClassCast()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

	@Test(expected = NullKeyException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptMosipNullKeyClassCast()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		MOSIPENCRYPTOR.asymmetricPrivateEncrypt(null, data, SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

	@Test(expected = NullKeyException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptMosipInvalidKeyNullPointer()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {

		MOSIPENCRYPTOR.asymmetricPrivateEncrypt("".getBytes(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

	@Test(expected = InvalidKeyException.class)
	public void testRSAPKS1AsymmetricPublicEncryptMosipIo()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		MOSIPENCRYPTOR.asymmetricPublicEncrypt("xx".getBytes(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

	@Test(expected = NullKeyException.class)
	public void testRSAPKS1AsymmetricPublicEncryptMosipNull()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		MOSIPENCRYPTOR.asymmetricPublicEncrypt(null, data, SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

	@Test(expected = InvalidKeyException.class)
	public void testRSAPKS1AsymmetricPublicEncryptMosipIllegalArgument()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

	@Test(expected = NullKeyException.class)
	public void testRSAPKS1AsymmetricPublicEncryptMosipInvalidKeyNullPointer()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {

		MOSIPENCRYPTOR.asymmetricPublicEncrypt("".getBytes(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}

	@Test(expected = NoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPublicEncryptMosipInvalidKeyNoMethod()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {

		MOSIPENCRYPTOR.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(), data,
				SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	@Test(expected = NoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptMosipInvalidKeyNoMethod()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {

		MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	@Test(expected = NoSuchAlgorithmException.class)
	public void testAESSymmetricEncryptMosipInvalidKeyNoMethod()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {

		MOSIPENCRYPTOR.symmetricEncrypt(setSymmetricUp(16, "AES").getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING);
	}

	@Test(expected = InvalidKeyException.class)
	public void testAESSymmetricEncryptMosipIllegalArgument()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {

		MOSIPENCRYPTOR.symmetricEncrypt(setSymmetricUp(50, "AES").getEncoded(), data,
				SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	@Test(expected = NullKeyException.class)
	public void testAESSymmetricEncryptMosipNullKeyException()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {

		MOSIPENCRYPTOR.symmetricEncrypt(null, data, SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	@Test(expected = NullDataException.class)
	public void testAESSymmetricEncryptNullData()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {

		assertThat(MOSIPENCRYPTOR.symmetricEncrypt(setSymmetricUp(16, "AES").getEncoded(), null,
				SecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING), isA(byte[].class));
	}

	@Test(expected = NullDataException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptNullData()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		assertThat(MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), null,
				SecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING), isA(byte[].class));
	}

	@Test(expected = InvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptInvalidKey()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		generator.initialize(1024, random);
		rsaPair = generator.generateKeyPair();
		assertThat(MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), data,
				SecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING), isA(byte[].class));
	}
	@Test(expected = NullDataException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptNullDataException()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
		
		
		assertThat(MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), null,
				SecurityMethod.RSA_WITH_PKCS1PADDING), isA(byte[].class));
	}
	
	@Test(expected = InvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptDataLength()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
	assertThat(MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), new byte[10000],
				SecurityMethod.RSA_WITH_PKCS1PADDING), isA(byte[].class));
	}
	
	@Test(expected = NullMethodException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptNullMethod()
			throws InvalidKeyException, InvalidDataException, java.security.NoSuchAlgorithmException {
	assertThat(MOSIPENCRYPTOR.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(), new byte[10000],
				null), isA(byte[].class));
	}


}
