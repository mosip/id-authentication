package io.mosip.authentication.core.logger;

import java.io.File;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.kernel.logger.logback.util.LoggerUtils;

/**
 * Logger for IDA which provides implementation from kernel logback.
 * 
 * @author Manoj SP
 *
 */
public final class IdaLogger {

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
		File logbackFile = new File(ClassLoader.getSystemClassLoader().getResource("idaLogger.xml").getPath());
		MosipRollingFileAppender mosipRollingFileAppender = (MosipRollingFileAppender) LoggerUtils
				.unmarshall(logbackFile, MosipRollingFileAppender.class);
		return MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, clazz);
	}
}
