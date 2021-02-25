package io.mosip.authentication.core.logger;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.factory.Logfactory;

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
	public static Logger getLogger(Class<?> clazz) {
		return Logfactory.getSlf4jLogger(clazz);
	}
}