package io.mosip.kernel.emailnotification.test.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.emailnotification.NotificationEmailBootApplication;
import io.mosip.kernel.emailnotification.config.LoggerConfiguration;
import io.mosip.kernel.emailnotification.exception.EmailNotificationAsyncHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=NotificationEmailBootApplication.class)
public class MailNotifierLoggerTest {
	@Test
	public void testLogger() {
		Logger mosipLogger = LoggerConfiguration.logConfig(EmailNotificationAsyncHandler.class);
		mosipLogger.info("", "", "", "Testing Logging");		
	}
}
