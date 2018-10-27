package io.mosip.registration.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.registration.test.config.SpringConfiguration;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.reader.PropertyFileReader;

public class RegistrationHealthCheckerTest extends SpringConfiguration {

	@Mock
	private RegistrationAppHealthCheckUtil registrationAppHealthCheckUtil;

	@Mock
	private MosipLogger logger;

	@Mock
	private PropertyFileReader propertyFileReader;

	private MosipRollingFileAppender mosipRollingFileAppender;

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
	public void diskSpaceAvailableTest() {
		ReflectionTestUtils.setField(registrationAppHealthCheckUtil, "LOGGER", logger);
		ReflectionTestUtils.invokeMethod(registrationAppHealthCheckUtil, "initializeLogger", mosipRollingFileAppender);
		boolean actualStatus = RegistrationAppHealthCheckUtil.isDiskSpaceAvailable();
		assertTrue(actualStatus);
	}

	@Ignore
	@Test
	public void networkAvailableTest() throws IOException, URISyntaxException {
		ReflectionTestUtils.setField(registrationAppHealthCheckUtil, "LOGGER", logger);
		ReflectionTestUtils.invokeMethod(registrationAppHealthCheckUtil, "initializeLogger", mosipRollingFileAppender);
		boolean actualStatus = RegistrationAppHealthCheckUtil.isNetworkAvailable();
		assertTrue(!actualStatus);
	}
}
