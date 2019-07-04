package io.mosip.registration.util;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;
import io.mosip.kernel.logger.logback.factory.Logfactory;

/**
 * LoggerFactory for Mosip
 * 
 * @author YASWANTH S
 *
 */
public class LoggerFactory {

	private static final RollingFileAppender MOSIP_ROLLING_APPENDER = new RollingFileAppender();

	static {

		MOSIP_ROLLING_APPENDER.setAppend(true);
		MOSIP_ROLLING_APPENDER.setAppenderName("org.apache.log4j.RollingFileAppender");
		MOSIP_ROLLING_APPENDER.setFileName("logs/registration.log");
		MOSIP_ROLLING_APPENDER.setFileNamePattern("logs/registration-%d{yyyy-MM-dd-HH}-%i.log");
		MOSIP_ROLLING_APPENDER.setMaxFileSize("5MB");
		MOSIP_ROLLING_APPENDER.setTotalCap("50MB");
		MOSIP_ROLLING_APPENDER.setMaxHistory(10);
		MOSIP_ROLLING_APPENDER.setImmediateFlush(true);
		MOSIP_ROLLING_APPENDER.setPrudent(true);
	}

	/**
	 * Get Logger for specific class
	 * 
	 * @param className
	 *            {@code Class} required classs where logger to be implemented
	 * @return Logger {@code Logger}
	 */
	public static Logger getLogger(Class<?> className) {
		return Logfactory.getDefaultRollingFileLogger(MOSIP_ROLLING_APPENDER, className);
	}
}
