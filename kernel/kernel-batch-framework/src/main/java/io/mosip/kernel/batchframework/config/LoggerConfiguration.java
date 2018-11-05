package io.mosip.kernel.batchframework.config;

import io.mosip.kernel.batchframework.constant.BatchPropertyConstant;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipConsoleAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;


/**
 * Logger configuration for using {@link MosipLogger} and logging(in console)
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
	public static MosipLogger logConfig(Class<?> clazz) {
		MosipConsoleAppender appender = new MosipConsoleAppender();
		appender.setTarget(BatchPropertyConstant.LOGGER_TARGET.getProperty());
		return MosipLogfactory.getMosipDefaultConsoleLogger(appender, clazz);
	}
}
