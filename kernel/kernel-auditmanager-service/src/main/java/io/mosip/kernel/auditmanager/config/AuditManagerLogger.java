package io.mosip.kernel.auditmanager.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auditmanager.exception.AuditAsyncExceptionHandler;
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
@Component
public class AuditManagerLogger {

	private static final String CONSOLE_LOGGER_NAME = AuditAsyncExceptionHandler.class.getName()
			.concat("_console_logger");

	private static final String FILE_LOGGER_NAME = AuditAsyncExceptionHandler.class.getName().concat("_file_logger");

	private static Logger fileLogger;

	private static Logger consoleLogger;

	@Value("${mosip.kernel.auditmanager-service-logs-location}")
	private String logFileLocation;

	@PostConstruct
	public void postConsAuditManagerLogger() {
		final FileAppender auditManagerFileAppender = new FileAppender();
		auditManagerFileAppender.setAppend(true);
		auditManagerFileAppender.setAppenderName("fileappender");
		auditManagerFileAppender.setFileName(logFileLocation);
		auditManagerFileAppender.setImmediateFlush(true);
		auditManagerFileAppender.setPrudent(false);
		fileLogger = Logfactory.getDefaultFileLogger(auditManagerFileAppender, FILE_LOGGER_NAME);

		ConsoleAppender auditManagerConsoleAppender = new ConsoleAppender();
		auditManagerConsoleAppender.setAppenderName("consoleappender");
		auditManagerConsoleAppender.setImmediateFlush(true);
		consoleLogger = Logfactory.getDefaultConsoleLogger(auditManagerConsoleAppender, CONSOLE_LOGGER_NAME);
	}

	/**
	 * Method to get the file logger for the class provided.
	 * 
	 * @param sessionId   session id
	 * @param idType      id type
	 * @param id          id
	 * @param description description
	 */
	public static void fileLoggerError(String sessionId, String idType, String id, String description) {
		fileLogger.error(sessionId, idType, id, description);
	}

	/**
	 * Method to get the console logger for the class provided.
	 * 
	 * @param sessionId   session id
	 * @param idType      id type
	 * @param id          id
	 * @param description description
	 */
	public static void consoleLoggerError(String sessionId, String idType, String id, String description) {
		consoleLogger.error(sessionId, idType, id, description);
	}

}