package io.mosip.registration.test.util.keymanager;

import java.util.Arrays;
import java.util.LinkedList;

import javax.crypto.SecretKey;

import static java.lang.System.currentTimeMillis;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;

import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.util.keymanager.impl.AESKeyManagerImpl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AESKeyManagerTest  {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private AESKeyManagerImpl aesKeyManagerImpl;
	private MosipRollingFileAppender mosipRollingFileAppender;
	@Mock
	private MosipLogger logger;
	@Mock
	private Environment environment;
	
	@Before
	public void initialize() {
		mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName("org.apache.log4j.RollingFileAppender");
		mosipRollingFileAppender.setFileName("logs");
		mosipRollingFileAppender.setFileNamePattern("logs/registration-processor-%d{yyyy-MM-dd-HH-mm}-%i.log");
		mosipRollingFileAppender.setMaxFileSize("1MB");
		mosipRollingFileAppender.setTotalCap("10MB");
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);

		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		when(environment.getProperty(RegistrationConstants.AES_KEY_MANAGER_ALG)).thenReturn("AES");
		when(environment.getProperty(RegistrationConstants.AES_KEY_SEED_LENGTH)).thenReturn("32");
		when(environment.getProperty(RegistrationConstants.AES_SESSION_KEY_LENGTH)).thenReturn("256");
	}

	@Test
	public void testGenerateAESKey() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(aesKeyManagerImpl, "logger", logger);
		SecretKey sessionKey = aesKeyManagerImpl.generateSessionKey(new LinkedList<String>(
				Arrays.asList(new String[] { "D46D6D302186", "user", String.valueOf(currentTimeMillis()) })));
		assertNotNull(sessionKey);
		assertTrue(sessionKey.getEncoded().length == 32);
		
		sessionKey = aesKeyManagerImpl.generateSessionKey(new LinkedList<String>(
				Arrays.asList(new String[] { "D46D6D302186", "Balaji Sridharan", String.valueOf(currentTimeMillis()) })));
		assertNotNull(sessionKey);
		assertTrue(sessionKey.getEncoded().length == 32);
	}
	
	@Test(expected = RegBaseUncheckedException.class)
	public void testUncheckedException() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(aesKeyManagerImpl, "logger", logger);
		ReflectionTestUtils.invokeMethod(aesKeyManagerImpl, "initializeLogger", mosipRollingFileAppender);
		aesKeyManagerImpl.generateSessionKey(null);
	}

}
