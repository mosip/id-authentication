package io.mosip.registration.test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.MissingMethodInvocationException;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import io.mosip.registration.test.config.SpringConfiguration;
import io.mosip.registration.test.util.rsa.RSADecryption;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.service.packet.encryption.rsa.RSAEncryption;

import static org.mockito.Mockito.when;

public class RSAEncryptionTest extends SpringConfiguration {	
	RegConstants regConstants;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Autowired
	private RSAEncryption rsaEncryption;
	@Autowired
	private RSADecryption rsaDecryption;
	PublicKey publicKey;
	PrivateKey privateKey;

	@Before
	public void initialize() {
		KeyPairGenerator keyPairGenerator = null;
		try {
			// Generate key pair generator
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// initialize key pair generator
		keyPairGenerator.initialize(2048);
		// get key pair
		KeyPair keyPair = keyPairGenerator.genKeyPair();
		// get public key from key pair
		 publicKey = keyPair.getPublic();
		 privateKey = keyPair.getPrivate();
	}

	@Test
	public void rsaEncryptionTest() throws Throwable {
		//when(propertyFileReader.getPropertyValue(RSA_ALG)).thenReturn("RSA");
		byte[] rsaEncryptedBytes = rsaEncryption.encrypt("yash".getBytes(), publicKey);
		byte[] rsaDecryptedBytes = rsaDecryption.decryptRsaEncryptedBytes(rsaEncryptedBytes, privateKey);
		Assert.assertArrayEquals("yash".getBytes(), rsaDecryptedBytes);
	}

	@Test(expected = MissingMethodInvocationException.class)
	public void NoAlgorithmTest() {

		when(RegConstants.RSA_ALG).thenReturn("NORSA");
		rsaEncryption.encrypt("aesEncryptedBytes".getBytes(), publicKey);
	}

}
