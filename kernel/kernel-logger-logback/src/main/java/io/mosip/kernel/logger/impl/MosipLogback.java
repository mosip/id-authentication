/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */

package io.mosip.kernel.logger.impl;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import io.mosip.kernel.logger.appender.MosipConsoleAppender;
import io.mosip.kernel.logger.appender.MosipFileAppender;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.constant.LogExeptionCodeConstants;
import io.mosip.kernel.logger.constant.MosipConfigurationDefaults;
import io.mosip.kernel.logger.exception.ClassNameNotFoundException;
import io.mosip.kernel.logger.exception.EmptyPatternException;
import io.mosip.kernel.logger.exception.FileNameNotProvided;
import io.mosip.kernel.logger.exception.MosipIllegalArgumentException;
import io.mosip.kernel.logger.exception.MosipIlligalStateException;
import io.mosip.kernel.logger.exception.PatternSyntaxExeption;

/**
 * Logback implementation class for mosip
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipLogback implements MosipLogger {

	/**
	 * Logger Instance per Class
	 */
	private Logger logger;

	/**
	 * Display pattern of logs
	 */
	private static final String LOGDISPLAY = "{} - {} - {} - {}";

	/**
	 * Builds a logger instance
	 * 
	 * @param mosipConsoleAppender
	 *            {@link MosipConsoleAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of calling class to get logger
	 */
	private MosipLogback(MosipConsoleAppender mosipConsoleAppender,
			String name) {

		LoggerContext context = (LoggerContext) LoggerFactory
				.getILoggerFactory();
		this.logger = context.getLogger(name);
		PatternLayoutEncoder ple = getdefaultPattern(context);
		ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
		consoleAppender.setContext(context);
		consoleAppender.setEncoder(ple);
		consoleAppender.setName(mosipConsoleAppender.getAppenderName());
		consoleAppender
				.setImmediateFlush(mosipConsoleAppender.isImmediateFlush());
		consoleAppender.setTarget(mosipConsoleAppender.getTarget());
		consoleAppender.start();
		this.logger.setAdditive(false);
		this.logger.addAppender(consoleAppender);
	}

	/**
	 * Builds a logger instance
	 * 
	 * @param mosipFileAppender
	 *            {@link MosipFileAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of calling class to get logger
	 */
	private MosipLogback(MosipFileAppender mosipFileAppender, String name) {

		LoggerContext context = (LoggerContext) LoggerFactory
				.getILoggerFactory();
		this.logger = context.getLogger(name);
		this.logger.setAdditive(false);
		PatternLayoutEncoder ple = getdefaultPattern(context);
		FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
		fileAppender.setContext(context);
		fileAppender.setEncoder(ple);
		fileAppender.setName(mosipFileAppender.getAppenderName());
		fileAppender.setImmediateFlush(mosipFileAppender.isImmediateFlush());
		fileAppender.setAppend(mosipFileAppender.isAppend());
		fileAppender.setFile(mosipFileAppender.getFileName());
		fileAppender.setPrudent(mosipFileAppender.isPrudent());
		fileAppender.start();
		this.logger.addAppender(fileAppender);
	}

	/**
	 * Builds a logger instance
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link MosipRollingFileAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of calling class to get logger
	 */
	private MosipLogback(MosipRollingFileAppender mosipRollingFileAppender,
			String name) {

		LoggerContext context = (LoggerContext) LoggerFactory
				.getILoggerFactory();
		this.logger = context.getLogger(name);
		this.logger.setAdditive(false);
		PatternLayoutEncoder ple = getdefaultPattern(context);
		RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
		rollingFileAppender.setContext(context);
		rollingFileAppender.setEncoder(ple);
		rollingFileAppender.setName(mosipRollingFileAppender.getAppenderName());
		rollingFileAppender
				.setImmediateFlush(mosipRollingFileAppender.isImmediateFlush());
		rollingFileAppender.setFile(mosipRollingFileAppender.getFileName());
		rollingFileAppender.setAppend(mosipRollingFileAppender.isAppend());
		rollingFileAppender.setPrudent(mosipRollingFileAppender.isPrudent());
		if (mosipRollingFileAppender.getMaxFileSize().isEmpty()) {
			TimeBasedRollingPolicy<ILoggingEvent> timeBasedRollingPolicy = new TimeBasedRollingPolicy<>();
			timeBasedRollingPolicy.setContext(context);
			timeBasedRollingPolicy.setFileNamePattern(
					mosipRollingFileAppender.getFileNamePattern());
			timeBasedRollingPolicy
					.setMaxHistory(mosipRollingFileAppender.getMaxHistory());
			if (mosipRollingFileAppender.getFileNamePattern().contains("%i")) {
				throw new PatternSyntaxExeption(
						LogExeptionCodeConstants.PATTERNSYNTAXEXCEPTION,
						LogExeptionCodeConstants.PATTERNSYNTAXEXCEPTIONMESSAGENOTI);
			}
			if (mosipRollingFileAppender.getTotalCap() != null
					&& !mosipRollingFileAppender.getTotalCap().isEmpty()) {
				timeBasedRollingPolicy.setTotalSizeCap(FileSize
						.valueOf(mosipRollingFileAppender.getTotalCap()));
			}
			timeBasedRollingPolicy.setParent(rollingFileAppender);
			rollingFileAppender.setRollingPolicy(timeBasedRollingPolicy);
			timeBasedRollingPolicy.start();
		} else {
			SizeAndTimeBasedRollingPolicy<ILoggingEvent> sizeAndTimeBasedRollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
			sizeAndTimeBasedRollingPolicy.setContext(context);

			sizeAndTimeBasedRollingPolicy.setFileNamePattern(
					mosipRollingFileAppender.getFileNamePattern());
			sizeAndTimeBasedRollingPolicy
					.setMaxHistory(mosipRollingFileAppender.getMaxHistory());
			if (mosipRollingFileAppender.getTotalCap() != null
					&& !mosipRollingFileAppender.getTotalCap().isEmpty()) {
				sizeAndTimeBasedRollingPolicy.setTotalSizeCap(FileSize
						.valueOf(mosipRollingFileAppender.getTotalCap()));
			}
			if (mosipRollingFileAppender.getMaxFileSize() != null) {
				sizeAndTimeBasedRollingPolicy.setMaxFileSize(FileSize
						.valueOf(mosipRollingFileAppender.getMaxFileSize()));
			}
			sizeAndTimeBasedRollingPolicy.setParent(rollingFileAppender);
			rollingFileAppender.setRollingPolicy(sizeAndTimeBasedRollingPolicy);
			sizeAndTimeBasedRollingPolicy.start();
		}
		rollingFileAppender.start();

		this.logger.addAppender(rollingFileAppender);

	}

	/**
	 * Verifies configurations
	 * 
	 * @param consoleAppender
	 *            {@link MosipConsoleAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of the calling class
	 * @return Configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipConsoleLogger(
			MosipConsoleAppender consoleAppender, String name) {
		if (name.isEmpty()) {
			throw new ClassNameNotFoundException(
					LogExeptionCodeConstants.CLASSNAMENOTFOUNDEXEPTION,
					LogExeptionCodeConstants.CLASSNAMENOTFOUNDEXEPTIONMESSAGE);
		} else {
			return new MosipLogback(consoleAppender, name);
		}
	}

	/**
	 * Verifies configurations
	 * 
	 * @param fileAppender
	 *            {@link MosipFileAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of the calling class
	 * @return Configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipFileLogger(MosipFileAppender fileAppender,
			String name) {

		if (fileAppender.getFileName() == null)
			throw new FileNameNotProvided(
					LogExeptionCodeConstants.FILENAMENOTPROVIDED,
					LogExeptionCodeConstants.FILENAMENOTPROVIDEDMESSAGENULL);
		else if (fileAppender.getFileName().isEmpty())
			throw new FileNameNotProvided(
					LogExeptionCodeConstants.FILENAMENOTPROVIDED,
					LogExeptionCodeConstants.FILENAMENOTPROVIDEDMESSAGEEMPTY);
		else if (name.isEmpty())
			throw new ClassNameNotFoundException(
					LogExeptionCodeConstants.CLASSNAMENOTFOUNDEXEPTION,
					LogExeptionCodeConstants.CLASSNAMENOTFOUNDEXEPTIONMESSAGE);
		else {
			return new MosipLogback(fileAppender, name);
		}
	}

	/**
	 * Verifies configurations
	 * 
	 * @param rollingFileAppender
	 *            {@link MosipRollingFileAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of the calling class
	 * @return Configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipRollingFileLogger(
			MosipRollingFileAppender rollingFileAppender, String name) {
		if (rollingFileAppender.getFileNamePattern() == null)
			throw new EmptyPatternException(
					LogExeptionCodeConstants.EMPTYPATTERNEXCEPTION,
					LogExeptionCodeConstants.EMPTYPATTERNEXCEPTIONMESSAGENULL);
		else if (rollingFileAppender.getFileNamePattern().isEmpty())
			throw new EmptyPatternException(
					LogExeptionCodeConstants.EMPTYPATTERNEXCEPTION,
					LogExeptionCodeConstants.EMPTYPATTERNEXCEPTIONMESSAGEEMPTY);
		else if (!rollingFileAppender.getFileNamePattern().contains("%d"))
			throw new PatternSyntaxExeption(
					LogExeptionCodeConstants.PATTERNSYNTAXEXCEPTION,
					LogExeptionCodeConstants.PATTERNSYNTAXEXCEPTIONMESSAGED);
		else if (!rollingFileAppender.getMaxFileSize().isEmpty()
				&& rollingFileAppender.getMaxFileSize() != null
				&& !rollingFileAppender.getFileNamePattern().contains("%i"))
			throw new PatternSyntaxExeption(
					LogExeptionCodeConstants.PATTERNSYNTAXEXCEPTION,
					LogExeptionCodeConstants.PATTERNSYNTAXEXCEPTIONMESSAGEI);
		else if (rollingFileAppender.getFileName() == null)
			throw new FileNameNotProvided(
					LogExeptionCodeConstants.FILENAMENOTPROVIDED,
					LogExeptionCodeConstants.FILENAMENOTPROVIDEDMESSAGENULL);
		else if (rollingFileAppender.getFileName().isEmpty())
			throw new FileNameNotProvided(
					LogExeptionCodeConstants.FILENAMENOTPROVIDED,
					LogExeptionCodeConstants.FILENAMENOTPROVIDEDMESSAGEEMPTY);
		else if (name.isEmpty())
			throw new ClassNameNotFoundException(
					LogExeptionCodeConstants.CLASSNAMENOTFOUNDEXEPTION,
					LogExeptionCodeConstants.CLASSNAMENOTFOUNDEXEPTIONMESSAGE);
		else
			try {
				return new MosipLogback(rollingFileAppender, name);
			} catch (IllegalStateException e) {
				throw new MosipIlligalStateException(
						LogExeptionCodeConstants.MOSIPILLEGALSTATEEXCEPTION,
						LogExeptionCodeConstants.MOSIPILLEGALSTATEEXCEPTIONMESSAGE);
			} catch (IllegalArgumentException e) {
				throw new MosipIllegalArgumentException(
						LogExeptionCodeConstants.MOSIPILLEGALARGUMENTEXCEPTION,
						LogExeptionCodeConstants.MOSIPILLEGALARGUMENTEXCEPTIONMESSAGE);
			}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.MosipLogger#debug(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void debug(String sessionId, String idType, String id,
			String description) {
		logger.debug(LOGDISPLAY, sessionId, idType, id, description);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.MosipLogger#warn(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void warn(String sessionId, String idType, String id,
			String description) {
		logger.warn(LOGDISPLAY, sessionId, idType, id, description);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.MosipLogger#error(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void error(String sessionId, String idType, String id,
			String description) {
		logger.error(LOGDISPLAY, sessionId, idType, id, description);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.MosipLogger#info(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void info(String sessionId, String idType, String id,
			String description) {
		logger.info(LOGDISPLAY, sessionId, idType, id, description);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.MosipLogger#trace(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void trace(String sessionId, String idType, String id,
			String description) {
		logger.trace(LOGDISPLAY, sessionId, idType, id, description);
	}

	/**
	 * Configures Layout for Mosip
	 * 
	 * @param context
	 *            {@link LoggerContext} instance
	 * @return {@link PatternLayoutEncoder} instance
	 */
	private PatternLayoutEncoder getdefaultPattern(LoggerContext context) {
		PatternLayoutEncoder ple = new PatternLayoutEncoder();
		ple.setPattern(MosipConfigurationDefaults.LOGPATTERN);
		ple.setContext(context);
		ple.start();
		return ple;
	}

}
