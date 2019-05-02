package io.mosip.preregistration.core.config;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.appender.ConsoleAppender;
import io.mosip.kernel.logger.logback.factory.Logfactory;

public class LoggerConfiguration {
	private LoggerConfiguration() {

	}

	public static Logger logConfig(Class<?> clazz) {
		ConsoleAppender appender = new ConsoleAppender();
		appender.setTarget("System.err");
		return Logfactory.getDefaultConsoleLogger(appender,clazz);
	}

}
