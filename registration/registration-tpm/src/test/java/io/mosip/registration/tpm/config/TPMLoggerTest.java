package io.mosip.registration.tpm.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;
import io.mosip.kernel.logger.logback.factory.Logfactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Logfactory.class })
public class TPMLoggerTest {

	@Test
	public void getLoggerTest() throws Exception {
		PowerMockito.mockStatic(Logfactory.class);
		Logger logger = PowerMockito.mock(Logger.class);

		PowerMockito.when(Logfactory.getDefaultRollingFileLogger(Mockito.any(RollingFileAppender.class),
				Mockito.any(Class.class))).thenReturn(logger);

		Assert.assertSame(logger, TPMLogger.getLogger(TPMLoggerTest.class));
	}

}
