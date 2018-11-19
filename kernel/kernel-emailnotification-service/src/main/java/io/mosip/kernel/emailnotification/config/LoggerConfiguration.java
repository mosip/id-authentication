package io.mosip.kernel.emailnotification.config;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.emailnotification.constant.MailNotifierConstants;
import io.mosip.kernel.emailnotification.exception.ApiExceptionHandler;
import io.mosip.kernel.logger.logback.appender.ConsoleAppender;
import io.mosip.kernel.logger.logback.factory.Logfactory;


/**
 * Logger configuration for using {@link Logger} and logging(in console)
 * exceptions which are not handled through controller advice {@link ApiExceptionHandler}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class LoggerConfiguration {
	/**
	 * Constructor for {@link LoggerConfiguration}
	 */
	private LoggerConfiguration() {
	}

	/**
	 * This method sets the logger target, and returns appender.
	 * 
	 * @param clazz
	 *            the class.
	 * @return the appender.
	 */
	public static Logger logConfig(Class<?> clazz) {
		ConsoleAppender appender = new ConsoleAppender();
		appender.setTarget(MailNotifierConstants.LOGGER_TARGET.getValue());
		return Logfactory.getDefaultConsoleLogger(appender, clazz);
	}
}
