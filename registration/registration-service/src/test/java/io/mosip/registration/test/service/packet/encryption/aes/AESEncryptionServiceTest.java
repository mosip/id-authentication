package io.mosip.registration.test.service.packet.encryption.aes;

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

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;

import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.encryption.aes.AESEncryptionServiceImpl;
import io.mosip.registration.service.packet.encryption.aes.AESSeedGenerator;
import io.mosip.registration.service.packet.encryption.rsa.RSAEncryptionService;
import io.mosip.registration.util.keymanager.AESKeyManager;
import io.mosip.registration.util.keymanager.impl.AESKeyManagerImpl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
	private MosipLogger logger;
	@Mock
	private AuditFactory auditFactory;
	@Mock
	private Environment environment;

	List<String> seeds;
	AESKeyManagerImpl aesKeyManagerImpl;
	String keySplitter = "#Key_Splitter#";

	@Before
	public void initialize() throws RegBaseCheckedException {
		MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName("org.apache.log4j.RollingFileAppender");
		mosipRollingFileAppender.setFileName("logs");
		mosipRollingFileAppender.setFileNamePattern("logs/registration-processor-%d{yyyy-MM-dd-HH-mm}-%i.log");
		mosipRollingFileAppender.setMaxFileSize("1MB");
		mosipRollingFileAppender.setTotalCap("10MB");
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);

		ReflectionTestUtils.invokeMethod(aesEncryptionServiceImpl, "initializeLogger", mosipRollingFileAppender);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		aesKeyManagerImpl = new AESKeyManagerImpl();
		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(aesKeyManagerImpl, "logger", logger);
		ReflectionTestUtils.setField(aesEncryptionServiceImpl, "logger", logger);
		ReflectionTestUtils.setField(aesEncryptionServiceImpl, "environment", environment);
		ReflectionTestUtils.setField(aesKeyManagerImpl, "environment", environment);

		when(environment.getProperty(RegConstants.AES_KEY_MANAGER_ALG)).thenReturn("AES");
		when(environment.getProperty(RegConstants.AES_KEY_SEED_LENGTH)).thenReturn("32");
		when(environment.getProperty(RegConstants.AES_SESSION_KEY_LENGTH)).thenReturn("256");
		when(environment.getProperty(RegConstants.AES_KEY_CIPHER_SPLITTER)).thenReturn(keySplitter);

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
