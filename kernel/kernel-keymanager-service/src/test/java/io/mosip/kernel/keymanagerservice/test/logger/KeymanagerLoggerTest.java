package io.mosip.kernel.keymanagerservice.test.logger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.keymanagerservice.logger.KeymanagerLogger;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class KeymanagerLoggerTest {

	@Test
	public void test() {
		Logger logger = KeymanagerLogger.getLogger(KeymanagerLoggerTest.class);
		assertThat(logger.getClass().getName(), is("io.mosip.kernel.logger.logback.impl.LoggerImpl"));
	}

}