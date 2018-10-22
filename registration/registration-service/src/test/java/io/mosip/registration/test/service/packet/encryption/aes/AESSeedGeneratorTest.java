package io.mosip.registration.test.service.packet.encryption.aes;

import java.util.List;

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
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.encryption.aes.impl.AESSeedGeneratorImpl;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;

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

		ReflectionTestUtils.invokeMethod(aesSeedGeneratorImpl, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.setField(aesSeedGeneratorImpl, "logger", logger);
		ReflectionTestUtils.setField(RegBaseCheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegBaseUncheckedException.class, "LOGGER", logger);
		ReflectionTestUtils.setField(RegistrationSystemPropertiesChecker.class, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
	}

	@Test
	public void testGenerateAESKeySeeds() throws RegBaseCheckedException {
		UserContext userContext = SessionContext.getInstance().getUserContext();
		userContext.setName("Operator Name");
		List<String> aesKeySeeds = aesSeedGeneratorImpl.generateAESKeySeeds();
		assertNotNull(aesKeySeeds);
		assertFalse(aesKeySeeds.isEmpty());
	}
	
	@Test(expected = RegBaseUncheckedException.class)
	public void testUncheckedException() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(SessionContext.class, "userContext", null);
		aesSeedGeneratorImpl.generateAESKeySeeds();
	} 


}
