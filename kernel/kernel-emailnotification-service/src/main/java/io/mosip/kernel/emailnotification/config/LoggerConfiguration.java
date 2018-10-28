package io.mosip.kernel.emailnotification.config;

import io.mosip.kernel.core.logger.spi.MosipLogger;
import io.mosip.kernel.emailnotification.constant.MailNotifierConstants;
import io.mosip.kernel.emailnotification.exception.MailNotifierControllerAdvice;
import io.mosip.kernel.logger.logback.appender.MosipConsoleAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;


/**
 * Logger configuration for using {@link MosipLogger} and logging(in console)
 * exceptions which are not handled through controller advice {@link MailNotifierControllerAdvice}.
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
	public static MosipLogger logConfig(Class<?> clazz) {
		MosipConsoleAppender appender = new MosipConsoleAppender();
		appender.setTarget(MailNotifierConstants.LOGGER_TARGET.getValue());
		return MosipLogfactory.getMosipDefaultConsoleLogger(appender, clazz);
	}
}
