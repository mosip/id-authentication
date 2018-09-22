package org.mosip.kernel.core.security.test;



import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.spec.SecretKeySpec;

import org.junit.Before;
import org.junit.Test;
import org.mosip.kernel.core.security.constants.MosipSecurityMethod;
import org.mosip.kernel.core.security.encryption.MosipEncryptor;
import org.mosip.kernel.core.security.exception.MosipInvalidDataException;
import org.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import org.mosip.kernel.core.security.exception.MosipNoSuchAlgorithmException;
import org.mosip.kernel.core.security.exception.MosipNullDataException;
import org.mosip.kernel.core.security.exception.MosipNullKeyException;

public class MosipEncryptorTest {

	private KeyPair rsaPair;
	
	private byte[] data;
	
	@Before
	public void setRSAUp() throws NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		generator.initialize(2048, random);
		rsaPair = generator.generateKeyPair();
        data="a".getBytes();
	}
	
	

	
	
	public SecretKeySpec setSymmetricUp(int length,String algo) throws NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		return new SecretKeySpec(keyBytes, algo);
	}
	
	@Test
	public void testRSAPKS1AsymmetricPrivateEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		generator.initialize(4096, random);
		rsaPair = generator.generateKeyPair();
		assertThat(MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(),data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING),isA(byte[].class));
	}
	
	@Test
	public void testRSAMD5AsymmetricPrivateEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		assertThat(MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(),data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING),isA(byte[].class));
	}
	
	@Test
	public void testRSASHA3512AsymmetricPrivateEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     assertThat(MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(),data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING),isA(byte[].class));
	}
	
	@Test
	public void testPlainRSAPKS1AsymmetricPrivateEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
	 assertThat(MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(),data, MosipSecurityMethod.RSA_WITH_PKCS1PADDING),isA(byte[].class));
	}
	
	@Test
	public void testPlainRSAMD5AsymmetricPrivateEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		assertThat(MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(),data, MosipSecurityMethod.RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING),isA(byte[].class));
	}
	
	@Test
	public void testPlainRSASHA3512AsymmetricPrivateEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     assertThat(MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(),data, MosipSecurityMethod.RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING),isA(byte[].class));
	}
	
	@Test
	public void testRSAPKS1AsymmetricPublicEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     assertThat(MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(),data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING),isA(byte[].class));
	}
	
	@Test
	public void testRSAMD5AsymmetricPublicEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     assertThat(MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(),data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING),isA(byte[].class));
	}
	
	@Test
	public void testRSASHA3512AsymmetricPublicEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     assertThat(MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(),data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING),isA(byte[].class));
	}
	
	@Test
	public void testPlainRSAPKS1AsymmetricPublicEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     assertThat(MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(),data, MosipSecurityMethod.RSA_WITH_PKCS1PADDING),isA(byte[].class));
	}
	
	@Test
	public void testPlainRSAMD5AsymmetricPublicEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     assertThat(MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(),data, MosipSecurityMethod.RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING),isA(byte[].class));
	}
	
	@Test
	public void testPlainRSASHA3512AsymmetricPublicEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     assertThat(MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(),data, MosipSecurityMethod.RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING),isA(byte[].class));
	}
	
	
	
	@Test
	public void testAESSymmetricEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		
		assertThat(MosipEncryptor.symmetricEncrypt(setSymmetricUp(16,"AES").getEncoded(),data, MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING),isA(byte[].class));
	}
	
	@Test
	public void testDESSymmetricEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		assertThat(MosipEncryptor.symmetricEncrypt(setSymmetricUp(8,"DES").getEncoded(),data, MosipSecurityMethod.DES_WITH_CBC_AND_PKCS7PADDING),isA(byte[].class));
	}
	
	@Test
	public void testTwoFishSymmetricEncrypt() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		assertThat(MosipEncryptor.symmetricEncrypt(setSymmetricUp(16,"TWOFISH").getEncoded(),data, MosipSecurityMethod.TWOFISH_WITH_CBC_AND_PKCS7PADDING),isA(byte[].class));
	}
	
	@Test(expected=MosipInvalidKeyException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptMosipIo() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     MosipEncryptor.asymmetricPrivateEncrypt("xx".getBytes(),data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}
	
	@Test(expected=MosipInvalidKeyException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptMosipInvalidKeyClassCast() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPublic().getEncoded(),data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}
	
	@Test(expected=MosipNullKeyException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptMosipNullKeyClassCast() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     MosipEncryptor.asymmetricPrivateEncrypt(null,data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}
	
	@Test(expected=MosipNullKeyException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptMosipInvalidKeyNullPointer() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		
       MosipEncryptor.asymmetricPrivateEncrypt("".getBytes(),data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}
	
	@Test(expected=MosipInvalidKeyException.class)
	public void testRSAPKS1AsymmetricPublicEncryptMosipIo() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     MosipEncryptor.asymmetricPublicEncrypt("xx".getBytes(),data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}
	
	@Test(expected=MosipNullKeyException.class)
	public void testRSAPKS1AsymmetricPublicEncryptMosipNull() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     MosipEncryptor.asymmetricPublicEncrypt(null,data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}
	@Test(expected=MosipInvalidKeyException.class)
	public void testRSAPKS1AsymmetricPublicEncryptMosipIllegalArgument() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPrivate().getEncoded(),data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}
	
	@Test(expected=MosipNullKeyException.class)
	public void testRSAPKS1AsymmetricPublicEncryptMosipInvalidKeyNullPointer() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		
       MosipEncryptor.asymmetricPublicEncrypt("".getBytes(),data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING);
	}
	
	@Test(expected=MosipNoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPublicEncryptMosipInvalidKeyNoMethod() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		
       MosipEncryptor.asymmetricPublicEncrypt(rsaPair.getPublic().getEncoded(),data, MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}
	
	@Test(expected=MosipNoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptMosipInvalidKeyNoMethod() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		
       MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(),data, MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}
	
	@Test(expected=MosipNoSuchAlgorithmException.class)
	public void testAESSymmetricEncryptMosipInvalidKeyNoMethod() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		
       MosipEncryptor.symmetricEncrypt(setSymmetricUp(16, "AES").getEncoded(),data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING);
	}
	
	@Test(expected=MosipInvalidKeyException.class)
	public void testAESSymmetricEncryptMosipIllegalArgument() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException  {
		
       MosipEncryptor.symmetricEncrypt(setSymmetricUp(50, "AES").getEncoded(),data, MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}
	
	@Test(expected=MosipNullKeyException.class)
	public void testAESSymmetricEncryptMosipNullKeyException() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException  {
		
       MosipEncryptor.symmetricEncrypt(null,data, MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}
	
	@Test(expected=MosipNullDataException.class)
	public void testAESSymmetricEncryptNullData() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		
		assertThat(MosipEncryptor.symmetricEncrypt(setSymmetricUp(16,"AES").getEncoded(),null, MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING),isA(byte[].class));
	}
	
	@Test(expected=MosipNullDataException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptNullData() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
     assertThat(MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(),null, MosipSecurityMethod.HYBRID_RSA_AES_WITH_PKCS1PADDING),isA(byte[].class));
	}
	
	@Test(expected=MosipInvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateEncryptInvalidKey() throws MosipInvalidKeyException, MosipInvalidDataException, NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		generator.initialize(1024, random);
		rsaPair = generator.generateKeyPair();
		assertThat(MosipEncryptor.asymmetricPrivateEncrypt(rsaPair.getPrivate().getEncoded(),data, MosipSecurityMethod.HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING),isA(byte[].class));
	}

}
