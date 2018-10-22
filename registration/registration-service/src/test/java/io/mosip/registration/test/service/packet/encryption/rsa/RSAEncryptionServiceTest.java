package io.mosip.registration.test.service.packet.encryption.rsa;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.decryption.MosipDecryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.encryption.rsa.RSAEncryptionService;
import io.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator;

import static io.mosip.registration.constants.RegProcessorExceptionEnum.REG_NO_SUCH_ALGORITHM_ERROR_CODE;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class RSAEncryptionServiceTest {

	@InjectMocks
	private RSAEncryptionService rsaEncryptionService;
	@Mock
	private RSAKeyGenerator rsaKeyGenerator;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private MosipLogger logger;

	private static KeyPair keyPair;

	@BeforeClass
	public static void generateKeyPair() {
		// Generate Key Pair
		KeyPairGenerator keyPairGenerator = null;
		try {
			// Generate key pair generator
			keyPairGenerator = KeyPairGenerator.getInstance(RegConstants.RSA_ALG);
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RegBaseUncheckedException(REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorCode(),
					REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorMessage(), noSuchAlgorithmException);
		}
		// initialize key pair generator
		keyPairGenerator.initialize(2048);
		// get key pair
		keyPair = keyPairGenerator.genKeyPair();
	}

	@Before
	public void initialize() {
		MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName("org.apache.log4j.RollingFileAppender");
		mosipRollingFileAppender.setFileName("logs");
		mosipRollingFileAppender.setFileNamePattern("logs/registration-processor-%d{yyyy-MM-dd-HH-mm}-%i.log");
		mosipRollingFileAppender.setMaxFileSize("1MB");
		mosipRollingFileAppender.setTotalCap("10MB");
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);

		ReflectionTestUtils.invokeMethod(rsaEncryptionService, "initializeLogger", mosipRollingFileAppender);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());

		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(rsaEncryptionService, "logger", logger);
	}

	@Test
	public void rsaPacketCreation()
			throws RegBaseCheckedException, MosipInvalidDataException, MosipInvalidKeyException {
		when(rsaKeyGenerator.getEncodedKey(true)).thenReturn(keyPair.getPublic().getEncoded());

		byte[] dataToEncrypt = "aesEncryptedInformationInBytes".getBytes();

		byte[] rsaEncryptedBytes = rsaEncryptionService.encrypt(dataToEncrypt);
		byte[] rsaDecryptedBytes = MosipDecryptor.asymmetricPrivateDecrypt(keyPair.getPrivate().getEncoded(),
				rsaEncryptedBytes, MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
		Assert.assertArrayEquals(dataToEncrypt, rsaDecryptedBytes);

	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testNullData() throws RegBaseCheckedException {
		when(rsaKeyGenerator.getEncodedKey(true)).thenReturn(keyPair.getPublic().getEncoded());
		rsaEncryptionService.encrypt(null);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidKey() throws RegBaseCheckedException {
		when(rsaKeyGenerator.getEncodedKey(true)).thenReturn("key".getBytes());
		rsaEncryptionService.encrypt(null);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidData() throws RegBaseCheckedException {
		when(rsaKeyGenerator.getEncodedKey(true)).thenReturn(keyPair.getPublic().getEncoded());
		StringBuilder input = new StringBuilder();
		for (int index = 0; index < 4500; index++) {
			input.append(index);
		}
		rsaEncryptionService.encrypt(input.toString().getBytes());
	}
}