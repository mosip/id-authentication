package org.mosip.registration.test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.service.packet.encryption.rsa.RSAEncryption;
import org.mosip.registration.service.packet.encryption.rsa.RSAEncryptionManager;
import org.mosip.registration.test.config.SpringConfiguration;
import org.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.when;
import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_NO_SUCH_ALGORITHM_ERROR_CODE;

public class RSAEncryptionManagerTest extends SpringConfiguration {

	@Autowired
	private RSAEncryptionManager rsaEncryptionManager;
	@Mock
	RSAKeyGenerator rsaKeyGenerator;
	@Mock
	RSAEncryption rsaEncryption;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Test
	public void rsaPacketCreation() {

		KeyPairGenerator keyPairGenerator=null;
		try {
			// Generate key pair generator
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RegBaseUncheckedException(REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorCode(),
					REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorMessage(), noSuchAlgorithmException);
		}
		// initialize key pair generator
		keyPairGenerator.initialize(2048);
		// get key pair
		KeyPair keyPair = keyPairGenerator.genKeyPair();
		// get public key from key pair
		PublicKey publicKey = keyPair.getPublic();

		when(rsaKeyGenerator.readPublickey(RegConstants.RSA_PUBLIC_KEY_FILE))
				.thenReturn(publicKey);

		byte[] encryptedBytes = "aesEncryptedBytes".getBytes();
		when(rsaEncryption.encrypt("aesEncryptedBytes".getBytes(), publicKey))
				.thenReturn(encryptedBytes);
		byte[] rsaEncryptedBytes = rsaEncryptionManager
				.encrypt("aesEncryptedInformationInBytes".getBytes());
		Assert.assertNotNull(rsaEncryptedBytes);

	}
}
