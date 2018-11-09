package io.mosip.kernel.emailnotification.smtp.test.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.emailnotification.smtp.NotificationEmailBootApplication;
import io.mosip.kernel.emailnotification.smtp.config.LoggerConfiguration;
import io.mosip.kernel.emailnotification.smtp.exception.MailNotifierAsyncHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=NotificationEmailBootApplication.class)
public class MailNotifierLoggerTest {
	@Test
	public void testLogger() {
		MosipLogger mosipLogger = LoggerConfiguration.logConfig(MailNotifierAsyncHandler.class);
		mosipLogger.info("", "", "", "Testing Logging");		
	}
}
