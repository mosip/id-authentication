package io.mosip.kernel.batchframework.config;

import io.mosip.kernel.batchframework.constant.BatchPropertyConstant;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.appender.ConsoleAppender;
import io.mosip.kernel.logger.logback.factory.Logfactory;

/**
 * Logger configuration for using {@link Logger} and logging(in console)
 * exceptions which are not handled through controller advice.
 * 
 * @author Ritesh Sinha
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
		appender.setTarget(BatchPropertyConstant.LOGGER_TARGET.getProperty());
		return Logfactory.getDefaultConsoleLogger(appender, clazz);
	}
}
