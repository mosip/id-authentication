package io.mosip.kernel.crypto.jce.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest
public class CryptoCoreNoSuchAlgorithmExceptionTest {

	private static final String MOCKAAD = "MOCKAAD";

	@Autowired
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;

	private KeyPair rsaPair;

	private byte[] data;
	
	private byte[] keyBytes;
	
	private final SecureRandom random = new SecureRandom();

	@Before
	public void init() throws java.security.NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048, random);
		rsaPair = generator.generateKeyPair();
		data = "test".getBytes();
	    keyBytes = new byte[16];
		random.nextBytes(keyBytes);
		ReflectionTestUtils.setField(cryptoCore, "asymmetricAlgorithm", "INVALIDALGO");
		ReflectionTestUtils.setField(cryptoCore, "symmetricAlgorithm", "INVALIDALGO");
		ReflectionTestUtils.setField(cryptoCore, "signAlgorithm", "INVALIDALGO");
		ReflectionTestUtils.setField(cryptoCore, "passwordAlgorithm", "INVALIDALGO");
	 }

	private SecretKeySpec setSymmetricUp(int length, String algo) throws java.security.NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		return new SecretKeySpec(keyBytes, algo);
	}

	@Test(expected = NoSuchAlgorithmException.class)
	public void testAsymmetricPublicEncryptNoSuchAlgorithmException() {
		assertThat(cryptoCore.asymmetricEncrypt(rsaPair.getPublic(), data), isA(byte[].class));
	}

	
	@Test(expected = NoSuchAlgorithmException.class)
	public void testAESSymmetricEncryptNoSuchAlgorithmException() throws java.security.NoSuchAlgorithmException {
		assertThat(cryptoCore.symmetricEncrypt(setSymmetricUp(32, "AES"), data,null, MOCKAAD.getBytes()), isA(byte[].class));
	}
	
	@Test(expected = NoSuchAlgorithmException.class)
	public void testAESSymmetricSaltEncryptNoSuchAlgorithmException() throws java.security.NoSuchAlgorithmException {
		assertThat(cryptoCore.symmetricEncrypt(setSymmetricUp(32, "AES"), data,keyBytes, MOCKAAD.getBytes()), isA(byte[].class));
	}
	
	@Test(expected = NoSuchAlgorithmException.class)
	public void testAsymmetricDecryptNoSuchAlgorithmException() {
		byte[] encryptedData = cryptoCore.asymmetricEncrypt(rsaPair.getPublic(), data);
		assertThat(cryptoCore.asymmetricDecrypt(rsaPair.getPrivate(), encryptedData), isA(byte[].class));
	}

	@Test(expected = NoSuchAlgorithmException.class)
	public void testAESSymmetricDecryptNoSuchAlgorithmException() throws java.security.NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		assertThat(cryptoCore.symmetricDecrypt(secretKeySpec, "encryptedData".getBytes(),MOCKAAD.getBytes()), isA(byte[].class));
	}
	
	@Test(expected = NoSuchAlgorithmException.class)
	public void testAESSymmetricSaltDecryptNoSuchAlgorithmException() throws java.security.NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		assertThat(cryptoCore.symmetricDecrypt(secretKeySpec, "encryptedData".getBytes(),MOCKAAD.getBytes(),keyBytes), isA(byte[].class));
	}
	
	@Test(expected = NoSuchAlgorithmException.class)
	public void testHashNoSuchAlgorithmException() throws NoSuchAlgorithmException, InvalidKeySpecException {
		assertThat(cryptoCore.hash(data, keyBytes), isA(String.class));
	}
	
	@Test(expected = NoSuchAlgorithmException.class)
	public void testSignNoSuchAlgorithmException() throws NoSuchAlgorithmException, InvalidKeySpecException {
		assertThat(cryptoCore.sign(data,rsaPair.getPrivate()), isA(String.class));
	}
	
	@Test(expected = NoSuchAlgorithmException.class)
	public void testVerifyNoSuchAlgorithmException() throws NoSuchAlgorithmException, InvalidKeySpecException {
		assertThat(cryptoCore.verifySignature(data, "InvalidSignature", rsaPair.getPublic()), is(true));
	}
}
