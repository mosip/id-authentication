package io.mosip.registration.tpm.config;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;
import io.mosip.kernel.logger.logback.factory.Logfactory;

/**
 * The class for logger
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class TPMLogger {

	private TPMLogger() {
	}

	private static final RollingFileAppender MOSIP_ROLLING_APPENDER = new RollingFileAppender();

	static {
		MOSIP_ROLLING_APPENDER.setAppend(true);
		MOSIP_ROLLING_APPENDER.setAppenderName("org.apache.log4j.RollingFileAppender");
		MOSIP_ROLLING_APPENDER.setFileName("logs/registration_utility.log");
		MOSIP_ROLLING_APPENDER.setFileNamePattern("logs/registration_utility-%d{yyyy-MM-dd-HH}-%i.log");
		MOSIP_ROLLING_APPENDER.setMaxFileSize("5MB");
		MOSIP_ROLLING_APPENDER.setTotalCap("50MB");
		MOSIP_ROLLING_APPENDER.setMaxHistory(10);
		MOSIP_ROLLING_APPENDER.setImmediateFlush(true);
		MOSIP_ROLLING_APPENDER.setPrudent(true);
	}

	/**
	 * Returns the instance of {@link Logger}
	 * 
	 * @param className
	 *            the name of the class
	 * @return the instance of {@link Logger}
	 */
	public static Logger getLogger(Class<?> className) {
		return Logfactory.getDefaultRollingFileLogger(MOSIP_ROLLING_APPENDER, className);
	}

}
