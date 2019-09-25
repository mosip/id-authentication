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
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DecryptorTest {

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

	public SecretKeySpec setSymmetricUp(int length, String algo) throws java.security.NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		return new SecretKeySpec(keyBytes, algo);
	}

	@Test
	public void testAsymmetricDecrypt() {
		byte[] encryptedData = cryptoCore.asymmetricEncrypt(rsaPair.getPublic(), data);
		assertThat(cryptoCore.asymmetricDecrypt(rsaPair.getPrivate(), encryptedData), isA(byte[].class));
	}



	@Test
	public void testAESSymmetricDecrypt() throws java.security.NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		byte[] encryptedData = cryptoCore.symmetricEncrypt(secretKeySpec, data,MOCKAAD.getBytes());
		assertThat(cryptoCore.symmetricDecrypt(secretKeySpec, encryptedData,MOCKAAD.getBytes()), isA(byte[].class));
	}
	
	@Test
	public void testAESSymmetricSaltDecrypt() throws java.security.NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[16];
		random.nextBytes(keyBytes);
		byte[] encryptedData = cryptoCore.symmetricEncrypt(secretKeySpec, data,MOCKAAD.getBytes(),keyBytes);
		assertThat(cryptoCore.symmetricDecrypt(secretKeySpec, encryptedData,MOCKAAD.getBytes(),keyBytes), isA(byte[].class));
	}

	@Test(expected = NullPointerException.class)
	public void testAESSymmetricDecryptInvalidKey() throws java.security.NoSuchAlgorithmException {
		SecretKeySpec secretKeySpec = setSymmetricUp(32, "AES");
		byte[] encryptedData = cryptoCore.symmetricEncrypt(secretKeySpec, data,MOCKAAD.getBytes());
		cryptoCore.symmetricDecrypt(null, encryptedData,MOCKAAD.getBytes());
	}

	@Test(expected = InvalidDataException.class)
	public void testAESSymmetricDecryptInvalidDataArrayIndexOutOfBounds()
			throws java.security.NoSuchAlgorithmException {
		cryptoCore.symmetricDecrypt(setSymmetricUp(32, "AES"), "aa".getBytes(),MOCKAAD.getBytes());
	}

	@Test(expected = InvalidDataException.class)
	public void testAESSymmetricDecryptInvalidDataIllegalBlockSize() throws java.security.NoSuchAlgorithmException {
		cryptoCore.symmetricDecrypt(setSymmetricUp(32, "AES"), new byte[121],MOCKAAD.getBytes());
	}

	// @Test(expected=InvalidDataException.class)
	public void testAESSymmetricDecryptInvalidDataBadPadding() throws java.security.NoSuchAlgorithmException {
		cryptoCore.symmetricDecrypt(setSymmetricUp(32, "AES"), new byte[32],MOCKAAD.getBytes());
	}

	@Test(expected = InvalidDataException.class)
	public void testRSAPKS1AsymmetricPrivateDecryptInvalidDataIllegalBlockSize() {
		cryptoCore.asymmetricDecrypt(rsaPair.getPrivate(), new byte[121]);
	}

}
