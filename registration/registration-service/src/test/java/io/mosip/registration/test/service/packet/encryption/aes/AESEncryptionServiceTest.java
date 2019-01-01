package io.mosip.registration.test.service.packet.encryption.aes;

import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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
import io.mosip.registration.service.AESSeedGenerator;
import io.mosip.registration.service.RSAEncryptionService;
import io.mosip.registration.service.impl.AESEncryptionServiceImpl;
import io.mosip.registration.util.keymanager.AESKeyManager;
import io.mosip.registration.util.keymanager.impl.AESKeyManagerImpl;

public class AESEncryptionServiceTest {

	@InjectMocks
	private AESEncryptionServiceImpl aesEncryptionServiceImpl;
	@Mock
	private AESKeyManager aesKeyManager;
	@Mock
	private AESSeedGenerator aesSeedGenerator;
	@Mock
	private RSAEncryptionService rsaEncryptionManager;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private AuditFactory auditFactory;
	@Mock
	private Environment environment;

	private List<String> seeds;
	private AESKeyManagerImpl aesKeyManagerImpl;
	private String keySplitter = "#Key_Splitter#";

	@Before
	public void initialize() throws RegBaseCheckedException {

		aesKeyManagerImpl = new AESKeyManagerImpl();
		ReflectionTestUtils.setField(aesEncryptionServiceImpl, "environment", environment);
		ReflectionTestUtils.setField(aesKeyManagerImpl, "environment", environment);

		when(environment.getProperty(RegistrationConstants.AES_KEY_MANAGER_ALG)).thenReturn("AES");
		when(environment.getProperty(RegistrationConstants.AES_KEY_SEED_LENGTH)).thenReturn("32");
		when(environment.getProperty(RegistrationConstants.AES_SESSION_KEY_LENGTH)).thenReturn("256");
		when(environment.getProperty(RegistrationConstants.AES_KEY_CIPHER_SPLITTER)).thenReturn(keySplitter);

		seeds = new ArrayList<>();
		seeds.add("name");
		seeds.add(String.valueOf(System.currentTimeMillis()));
	}

	@Test
	public void aesEncryptionTest() throws RegBaseCheckedException {
		SecretKey sessionKey = aesKeyManagerImpl.generateSessionKey(seeds);
		when(aesSeedGenerator.generateAESKeySeeds()).thenReturn(new ArrayList<>());
		when(aesKeyManager.generateSessionKey(Mockito.anyListOf(String.class))).thenReturn(sessionKey);
		when(rsaEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());

		byte[] dataToEncrypt = "original data".getBytes();
		byte[] encryptedData = aesEncryptionServiceImpl.encrypt(dataToEncrypt);
		String ciperText = new String(encryptedData);
		Assert.assertTrue(ciperText.contains("rsa" + keySplitter));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void aesEncryptionRuntimeExpTest() throws RegBaseCheckedException {
		SecretKey sessionKey = aesKeyManagerImpl.generateSessionKey(seeds);
		when(aesSeedGenerator.generateAESKeySeeds()).thenReturn(new ArrayList<>());
		when(aesKeyManager.generateSessionKey(Mockito.anyListOf(String.class))).thenReturn(sessionKey);
		when(rsaEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());
		aesEncryptionServiceImpl.encrypt(null);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void invalidKeyExpTest() throws RegBaseCheckedException, NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
		SecretKey sessionKey = keyGenerator.generateKey();
		when(aesSeedGenerator.generateAESKeySeeds()).thenReturn(new ArrayList<>());
		when(aesKeyManager.generateSessionKey(Mockito.anyListOf(String.class))).thenReturn(sessionKey);
		when(rsaEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());
		aesEncryptionServiceImpl.encrypt("encrypt".getBytes());
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void concatExceptionTest() {
		ReflectionTestUtils.invokeMethod(aesEncryptionServiceImpl, "concat", "encrypted".getBytes(), null);
	}

}
