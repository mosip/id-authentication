package io.mosip.authentication.core.logger;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;

/**
 * Logger for IDA which provides implementation from kernel logback.
 * 
 * @author Manoj SP
 *
 */
public final class IdaLogger {
	
	private static MosipRollingFileAppender mosipRollingFileAppender;
	
	static {
		mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppend(true);
		mosipRollingFileAppender.setAppenderName("fileappender");
		mosipRollingFileAppender.setFileName("logs/id-auth.log");
		mosipRollingFileAppender.setFileNamePattern("logs/id-auth-%d{yyyy-MM-dd}-%i.log");
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setMaxFileSize("1mb");
		mosipRollingFileAppender.setMaxHistory(3);
		mosipRollingFileAppender.setPrudent(false);
		mosipRollingFileAppender.setTotalCap("10mb");
	}

	/**
	 * Instantiates a new ida logger.
	 */
	private IdaLogger() {
	}

	/**
	 * Method to get the rolling file logger for the class provided.
	 *
	 * @param clazz
	 *            the clazz
	 * @return the logger
	 */
	public static MosipLogger getLogger(Class<?> clazz) {
		return MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, clazz);
	}
}
