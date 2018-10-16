package io.mosip.registration.test;

import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.registration.test.config.SpringConfiguration;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

public class RegistrationSystemPropertiesCheckerTest extends SpringConfiguration {

	@Mock
	private RegistrationSystemPropertiesChecker registrationSystemPropertiesChecker;

	@Mock
	private MosipLogger logger;

	private MosipRollingFileAppender mosipRollingFileAppender;

	private static final String PATTERN = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

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
	public void macAddressValidationTest() {
		ReflectionTestUtils.setField(registrationSystemPropertiesChecker, "LOGGER", logger);
		ReflectionTestUtils.invokeMethod(registrationSystemPropertiesChecker, "initializeLogger",
				mosipRollingFileAppender);
		String macAddress = RegistrationSystemPropertiesChecker.getMachineId();
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(macAddress);
		assertTrue(matcher.matches());
	}
}
