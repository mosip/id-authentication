package io.mosip.preregistration.core.config;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.appender.ConsoleAppender;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;
import io.mosip.kernel.logger.logback.factory.Logfactory;

public final class LoggerConfiguration {
	
	/**
	 * Instantiates a new pre-reg  logger.
	 */
	private LoggerConfiguration() {

	}

	
	/** The mosip rolling file appender. */
	private static RollingFileAppender mosipRollingFileAppender;
	
	static {
		mosipRollingFileAppender = new RollingFileAppender();
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setAppenderName("fileappender");
		mosipRollingFileAppender.setFileName("/home/logs/preregistration.log");
		mosipRollingFileAppender.setFileNamePattern("/home/logs/preregistration-%d{yyyy-MM-dd}-%i.log");
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setMaxFileSize("3mb");
//		mosipRollingFileAppender.setMaxHistory(3);
		mosipRollingFileAppender.setPrudent(false);
//		mosipRollingFileAppender.setTotalCap("50mb");
	}
	
	public static Logger logConfig(Class<?> clazz) {
		return Logfactory.getDefaultRollingFileLogger(mosipRollingFileAppender, clazz);
	}

}
