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
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EncryptorTest {
	
	private static final String MOCKAAD = "MOCKAAD";

	@Autowired
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;

	private KeyPair rsaPair;

	private byte[] data;

	@Before
	public void setRSAUp() throws java.security.NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		generator.initialize(2048, random);
		rsaPair = generator.generateKeyPair();
		data = "test".getBytes();

	}

	private SecretKeySpec setSymmetricUp(int length, String algo) throws java.security.NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		return new SecretKeySpec(keyBytes, algo);
	}

	@Test
	public void testAsymmetricPublicEncrypt() {
		assertThat(cryptoCore.asymmetricEncrypt(rsaPair.getPublic(), data), isA(byte[].class));
	}

	@Test
	public void testAESSymmetricEncrypt() throws java.security.NoSuchAlgorithmException {
		assertThat(cryptoCore.symmetricEncrypt(setSymmetricUp(32, "AES"), data,MOCKAAD.getBytes()), isA(byte[].class));
	}
	
	@Test
	public void testAESSymmetricSaltEncrypt() throws java.security.NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[16];
		random.nextBytes(keyBytes);
		assertThat(cryptoCore.symmetricEncrypt(setSymmetricUp(32, "AES"), data,keyBytes,MOCKAAD.getBytes()), isA(byte[].class));
	}


	@Test(expected = NullPointerException.class)
	public void testAESSymmetricEncryptInvalidKey() throws java.security.NoSuchAlgorithmException {
		cryptoCore.symmetricEncrypt(null, data,MOCKAAD.getBytes());
	}

}
