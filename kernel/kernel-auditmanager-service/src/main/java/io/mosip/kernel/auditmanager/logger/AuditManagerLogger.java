package io.mosip.kernel.auditmanager.logger;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.appender.ConsoleAppender;
import io.mosip.kernel.logger.logback.appender.FileAppender;
import io.mosip.kernel.logger.logback.factory.Logfactory;

/**
 * Auditmanager logger.
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public final class AuditManagerLogger {

	/**
	 * Field for file appender
	 */
	private static FileAppender auditManagerFileAppender;

	/**
	 * Field for console appender
	 */
	private static ConsoleAppender auditManagerConsoleAppender;

	static {
		auditManagerFileAppender = new FileAppender();
		auditManagerFileAppender.setAppend(true);
		auditManagerFileAppender.setAppenderName("fileappender");
		auditManagerFileAppender.setFileName("logs/audit.log");
		auditManagerFileAppender.setImmediateFlush(true);
		auditManagerFileAppender.setPrudent(false);
		auditManagerConsoleAppender = new ConsoleAppender();
		auditManagerConsoleAppender.setAppenderName("fileappender");
		auditManagerConsoleAppender.setImmediateFlush(true);
	}

	/**
	 * Instantiates a new logger.
	 */
	private AuditManagerLogger() {
	}

	/**
	 * Method to get the rolling file logger for the class provided.
	 *
	 * @param clazz
	 *            the clazz
	 * @return the logger
	 */
	public static Logger getFileLogger(Class<?> clazz) {
		return Logfactory.getDefaultFileLogger(auditManagerFileAppender, clazz);
	}

	/**
	 * Method to get the console logger for the class provided.
	 *
	 * @param clazz
	 *            the clazz
	 * @return the logger
	 */
	public static Logger getConsoleLogger(Class<?> clazz) {
		return Logfactory.getDefaultConsoleLogger(auditManagerConsoleAppender, clazz);
	}
}