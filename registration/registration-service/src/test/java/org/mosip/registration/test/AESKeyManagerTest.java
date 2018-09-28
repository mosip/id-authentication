package org.mosip.registration.test;

import static java.lang.System.currentTimeMillis;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;

import javax.crypto.SecretKey;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.test.config.SpringConfiguration;
import org.mosip.registration.util.keymanager.impl.AESKeyManagerImpl;
import org.springframework.test.util.ReflectionTestUtils;

public class AESKeyManagerTest extends SpringConfiguration {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private AESKeyManagerImpl aesKeyManagerImpl;
	private MosipRollingFileAppender mosipRollingFileAppender;
	@Mock
	private MosipLogger logger;
	
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
	}

	@Test
	public void testGenerateAESKey() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(aesKeyManagerImpl, "LOGGER", logger);
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
		ReflectionTestUtils.setField(aesKeyManagerImpl, "LOGGER", logger);
		ReflectionTestUtils.invokeMethod(aesKeyManagerImpl, "initializeLogger", mosipRollingFileAppender);
		aesKeyManagerImpl.generateSessionKey(null);
	}

}
