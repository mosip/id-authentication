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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:/application-exception.properties")
public class EncryptorExceptionTest {

	@Autowired
	private Encryptor<PrivateKey, PublicKey, SecretKey> encryptorImpl;


	private KeyPair rsaPair;

	private byte[] data;

	@Before
	public void setRSAUp() throws java.security.NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		generator.initialize(2048, random);
		rsaPair = generator.generateKeyPair();
		data = "a".getBytes();

	}

	public SecretKeySpec setSymmetricUp(int length, String algo)
			throws java.security.NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		return new SecretKeySpec(keyBytes, algo);
	}

	@Test(expected=NoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPrivateEncrypt() {
		assertThat(encryptorImpl.asymmetricPrivateEncrypt(rsaPair.getPrivate(),
				data), isA(byte[].class));
	}

	@Test(expected=NoSuchAlgorithmException.class)
	public void testRSAPKS1AsymmetricPublicEncrypt() {
		assertThat(encryptorImpl.asymmetricPublicEncrypt(rsaPair.getPublic(),
				data), isA(byte[].class));
	}

	@Test(expected=NoSuchAlgorithmException.class)
	public void testAESSymmetricEncrypt()
			throws java.security.NoSuchAlgorithmException {
		assertThat(
				encryptorImpl.symmetricEncrypt(setSymmetricUp(32, "AES"), data),
				isA(byte[].class));
	}

	
}
