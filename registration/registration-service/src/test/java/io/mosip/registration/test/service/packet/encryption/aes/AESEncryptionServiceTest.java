package io.mosip.registration.test.service.packet.encryption.aes;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.RSAEncryptionService;
import io.mosip.registration.service.impl.AESEncryptionServiceImpl;

import static org.mockito.Mockito.when;

public class AESEncryptionServiceTest {

	@InjectMocks
	private AESEncryptionServiceImpl aesEncryptionServiceImpl;
	@Mock
	private io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator keyGenerator;
	@Mock
	private RSAEncryptionService rsaEncryptionManager;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private AuditFactory auditFactory;
	@Mock
	private Environment environment;

	private String keySplitter = "#Key_Splitter#";

	@Before
	public void initialize() throws RegBaseCheckedException {

		ReflectionTestUtils.setField(aesEncryptionServiceImpl, "environment", environment);

		when(environment.getProperty(RegistrationConstants.AES_KEY_MANAGER_ALG)).thenReturn("AES");
		when(environment.getProperty(RegistrationConstants.AES_KEY_SEED_LENGTH)).thenReturn("32");
		when(environment.getProperty(RegistrationConstants.AES_SESSION_KEY_LENGTH)).thenReturn("256");
		when(environment.getProperty(RegistrationConstants.AES_KEY_CIPHER_SPLITTER)).thenReturn(keySplitter);
	}

	@Test
	public void aesEncryptionTest() throws RegBaseCheckedException, NoSuchAlgorithmException {
		KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES");
		aesKeyGenerator.init(256);
		SecretKey sessionKey = aesKeyGenerator.generateKey();
		when(keyGenerator.getSymmetricKey()).thenReturn(sessionKey);
		when(rsaEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());

		byte[] dataToEncrypt = "original data".getBytes();
		byte[] encryptedData = aesEncryptionServiceImpl.encrypt(dataToEncrypt);
		String ciperText = new String(encryptedData);
		Assert.assertTrue(ciperText.contains("rsa" + keySplitter));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void aesEncryptionRuntimeExpTest() throws RegBaseCheckedException {
		when(rsaEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());
		aesEncryptionServiceImpl.encrypt(null);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void invalidKeyExpTest() throws RegBaseCheckedException, NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
		SecretKey sessionKey = keyGenerator.generateKey();
		when(this.keyGenerator.getSymmetricKey()).thenReturn(sessionKey);
		when(rsaEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());
		aesEncryptionServiceImpl.encrypt("encrypt".getBytes());
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void concatExceptionTest() {
		ReflectionTestUtils.invokeMethod(aesEncryptionServiceImpl, "concat", "encrypted".getBytes(), null);
	}

}
