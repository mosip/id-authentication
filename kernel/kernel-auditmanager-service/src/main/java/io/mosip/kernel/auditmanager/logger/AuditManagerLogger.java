package io.mosip.kernel.auditmanager.logger;

import io.mosip.kernel.core.logger.spi.Logger;
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

	private static FileAppender auditManagerFileAppender;

	static {
		auditManagerFileAppender = new FileAppender();
		auditManagerFileAppender.setAppend(true);
		auditManagerFileAppender.setAppenderName("fileappender");
		auditManagerFileAppender.setFileName("logs/audit.log");
		auditManagerFileAppender.setImmediateFlush(true);
		auditManagerFileAppender.setPrudent(false);
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
	public static Logger getLogger(Class<?> clazz) {
		return Logfactory.getDefaultFileLogger(auditManagerFileAppender, clazz);
	}
}