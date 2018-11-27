package io.mosip.kernel.crypto.jce.test;

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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:/application-exception.properties")
public class DecryptorExceptionTest {

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

	@Test(expected=NoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPrivateDecrypt(){
	decryptorImpl.asymmetricPrivateDecrypt(rsaPair.getPrivate(), data);
	}

	

	@Test(expected=NoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPublicDecrypt()
			{
		decryptorImpl.asymmetricPublicDecrypt(rsaPair.getPublic(), data);
	}
 
	@Test(expected=NoSuchAlgorithmException.class)
	public void testAESSymmetricDecrypt() throws java.security.NoSuchAlgorithmException
			{
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		decryptorImpl.symmetricDecrypt(secretKeySpec, data);
	}
}
