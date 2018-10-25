package io.mosip.registration.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.packet.encryption.aes.impl.AESSeedGeneratorImpl;

public class AESSeedGeneratorTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private AESSeedGeneratorImpl aesSeedGeneratorImpl;
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
	public void testGenerateAESKeySeeds() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(aesSeedGeneratorImpl, "LOGGER", logger);
		ReflectionTestUtils.invokeMethod(aesSeedGeneratorImpl, "initializeLogger", mosipRollingFileAppender);
		List<String> aesKeySeeds = aesSeedGeneratorImpl.generateAESKeySeeds();
		assertNotNull(aesKeySeeds);
		assertFalse(aesKeySeeds.isEmpty());
	}

}
