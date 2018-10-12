package io.mosip.registration.test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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

import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.decryption.MosipDecryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.encryption.aes.AESEncryptionManager;
import io.mosip.registration.service.packet.encryption.aes.AESSeedGenerator;
import io.mosip.registration.service.packet.encryption.rsa.RSAEncryptionManager;
import io.mosip.registration.util.keymanager.AESKeyManager;
import io.mosip.registration.util.keymanager.impl.AESKeyManagerImpl;
import io.mosip.registration.util.reader.PropertyFileReader;

public class AESEncryptionManagerTest {

	@InjectMocks
	private AESEncryptionManager aesEncryptionManager;
	@Mock
	private AESKeyManager aesKeyManager;
	@Mock
	private AESSeedGenerator aesSeedGenerator;
	@Mock
	private RSAEncryptionManager rsaEncryptionManager;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private MosipLogger logger;
	@Mock
	private AuditFactory auditFactory;

	private SecretKey sessionKey;

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

		ReflectionTestUtils.invokeMethod(aesEncryptionManager, "initializeLogger", mosipRollingFileAppender);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(AESKeyManagerImpl.class, "LOGGER", logger);
		ReflectionTestUtils.setField(aesEncryptionManager, "LOGGER", logger);

		List<String> seeds = new ArrayList<>();
		seeds.add("name");
		seeds.add(String.valueOf(System.currentTimeMillis()));
		sessionKey = new AESKeyManagerImpl().generateSessionKey(seeds);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void aesEncryptionTest()
			throws RegBaseCheckedException, MosipInvalidDataException, MosipInvalidKeyException {
		// Enable AES 256 bit encryption
		when(aesSeedGenerator.generateAESKeySeeds()).thenReturn(new ArrayList<>());
		when(aesKeyManager.generateSessionKey(Mockito.anyList())).thenReturn(sessionKey);
		when(rsaEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());

		byte[] dataToEncrypt = "original data".getBytes();
		byte[] encryptedData = aesEncryptionManager.encrypt(dataToEncrypt);
		String ciperText = new String(encryptedData);
		String splitter = PropertyFileReader.getPropertyValue(RegConstants.AES_KEY_CIPHER_SPLITTER);
		int startIndex = ciperText.indexOf(splitter) + splitter.length();
		byte[] aesEncryptedData = new byte[ciperText.length() - startIndex];
		System.arraycopy(encryptedData, startIndex, aesEncryptedData, 0, aesEncryptedData.length);
		byte[] decryptedData = MosipDecryptor.symmetricDecrypt(sessionKey.getEncoded(), aesEncryptedData,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
		byte[] actualData = new byte[dataToEncrypt.length];
		System.arraycopy(decryptedData, 0, actualData, 0, dataToEncrypt.length);
		Assert.assertArrayEquals(dataToEncrypt, actualData);
	}

}
