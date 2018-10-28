
package io.mosip.kernel.logger.logback.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import io.mosip.kernel.core.logger.exception.ClassNameNotFoundException;
import io.mosip.kernel.core.logger.exception.EmptyPatternException;
import io.mosip.kernel.core.logger.exception.FileNameNotProvided;
import io.mosip.kernel.core.logger.exception.MosipIllegalArgumentException;
import io.mosip.kernel.core.logger.exception.MosipIllegalStateException;
import io.mosip.kernel.core.logger.exception.PatternSyntaxException;
import io.mosip.kernel.core.logger.spi.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipConsoleAppender;
import io.mosip.kernel.logger.logback.appender.MosipFileAppender;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.constant.LogExeptionCodeConstant;
import io.mosip.kernel.logger.logback.constant.MosipConfigurationDefault;

/**
 * Logback implementation class for mosip
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipLogback implements MosipLogger {

	private static Map<String, Appender<ILoggingEvent>> rollingFileAppenders = new HashMap<>();
	private static Map<String, Appender<ILoggingEvent>> fileAppenders = new HashMap<>();
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
	private MosipLogback(MosipConsoleAppender mosipConsoleAppender, String name) {

		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		this.logger = context.getLogger(name);
		PatternLayoutEncoder ple = getdefaultPattern(context);
		ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
		consoleAppender.setContext(context);
		consoleAppender.setEncoder(ple);
		consoleAppender.setName(mosipConsoleAppender.getAppenderName());
		consoleAppender.setImmediateFlush(mosipConsoleAppender.isImmediateFlush());
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

		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		this.logger = context.getLogger(name);
		this.logger.setAdditive(false);
		PatternLayoutEncoder ple = getdefaultPattern(context);
		FileAppender<ILoggingEvent> fileAppender = null;
		if (!fileAppenders.containsKey(mosipFileAppender.getAppenderName())) {
			fileAppender = new FileAppender<>();
			fileAppender.setContext(context);
			fileAppender.setEncoder(ple);
			fileAppender.setName(mosipFileAppender.getAppenderName());
			fileAppender.setImmediateFlush(mosipFileAppender.isImmediateFlush());
			fileAppender.setAppend(mosipFileAppender.isAppend());
			fileAppender.setFile(mosipFileAppender.getFileName());
			fileAppender.setPrudent(mosipFileAppender.isPrudent());
			fileAppender.start();
			fileAppenders.put(fileAppender.getName(), fileAppender);
		} else {
			fileAppender = (FileAppender<ILoggingEvent>) fileAppenders.get(mosipFileAppender.getAppenderName());
		}
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
	private MosipLogback(MosipRollingFileAppender mosipRollingFileAppender, String name) {

		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		this.logger = context.getLogger(name);
		this.logger.setAdditive(false);
		PatternLayoutEncoder ple = getdefaultPattern(context);
		RollingFileAppender<ILoggingEvent> rollingFileAppender = null;
		if (!rollingFileAppenders.containsKey(mosipRollingFileAppender.getAppenderName())) {
			rollingFileAppender = new RollingFileAppender<>();
			rollingFileAppender.setContext(context);
			rollingFileAppender.setEncoder(ple);
			rollingFileAppender.setName(mosipRollingFileAppender.getAppenderName());
			rollingFileAppender.setImmediateFlush(mosipRollingFileAppender.isImmediateFlush());
			rollingFileAppender.setFile(mosipRollingFileAppender.getFileName());
			rollingFileAppender.setAppend(mosipRollingFileAppender.isAppend());
			rollingFileAppender.setPrudent(mosipRollingFileAppender.isPrudent());
			if (mosipRollingFileAppender.getMaxFileSize().trim().isEmpty()) {
				configureTimeBasedRollingPolicy(mosipRollingFileAppender, context, rollingFileAppender);
			} else {
				configureSizeAndTimeBasedPolicy(mosipRollingFileAppender, context, rollingFileAppender);
			}
			rollingFileAppenders.put(rollingFileAppender.getName(), rollingFileAppender);
			rollingFileAppender.start();
		} else {
			rollingFileAppender = (RollingFileAppender<ILoggingEvent>) rollingFileAppenders
					.get(mosipRollingFileAppender.getAppenderName());
		}

		this.logger.addAppender(rollingFileAppender);

	}

	/**
	 * Configures size and time based policy
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link MosipRollingFileAppender} instance to get values'p
	 * @param context
	 *            context of logger
	 * @param rollingFileAppender
	 *            {@link RollingFileAppender} instance by which this policy will
	 *            attach
	 */
	private void configureSizeAndTimeBasedPolicy(MosipRollingFileAppender mosipRollingFileAppender,
			LoggerContext context, RollingFileAppender<ILoggingEvent> rollingFileAppender) {
		SizeAndTimeBasedRollingPolicy<ILoggingEvent> sizeAndTimeBasedRollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
		sizeAndTimeBasedRollingPolicy.setContext(context);

		sizeAndTimeBasedRollingPolicy.setFileNamePattern(mosipRollingFileAppender.getFileNamePattern());
		sizeAndTimeBasedRollingPolicy.setMaxHistory(mosipRollingFileAppender.getMaxHistory());
		if (mosipRollingFileAppender.getTotalCap() != null
				&& !mosipRollingFileAppender.getTotalCap().trim().isEmpty()) {
			sizeAndTimeBasedRollingPolicy.setTotalSizeCap(FileSize.valueOf(mosipRollingFileAppender.getTotalCap()));
		}
		if (mosipRollingFileAppender.getMaxFileSize() != null) {
			sizeAndTimeBasedRollingPolicy.setMaxFileSize(FileSize.valueOf(mosipRollingFileAppender.getMaxFileSize()));
		}
		sizeAndTimeBasedRollingPolicy.setParent(rollingFileAppender);
		rollingFileAppender.setRollingPolicy(sizeAndTimeBasedRollingPolicy);
		sizeAndTimeBasedRollingPolicy.start();
	}

	/**
	 * Configures time based policy
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link MosipRollingFileAppender} instance to get values
	 * @param context
	 *            context of logger
	 * @param rollingFileAppender
	 *            {@link RollingFileAppender} instance by which this policy will
	 *            attach
	 */
	private void configureTimeBasedRollingPolicy(MosipRollingFileAppender mosipRollingFileAppender,
			LoggerContext context, RollingFileAppender<ILoggingEvent> rollingFileAppender) {
		TimeBasedRollingPolicy<ILoggingEvent> timeBasedRollingPolicy = new TimeBasedRollingPolicy<>();
		timeBasedRollingPolicy.setContext(context);
		timeBasedRollingPolicy.setFileNamePattern(mosipRollingFileAppender.getFileNamePattern());
		timeBasedRollingPolicy.setMaxHistory(mosipRollingFileAppender.getMaxHistory());
		if (mosipRollingFileAppender.getFileNamePattern().contains("%i")) {
			throw new PatternSyntaxException(LogExeptionCodeConstant.PATTERNSYNTAXEXCEPTION.getValue(),
					LogExeptionCodeConstant.PATTERNSYNTAXEXCEPTIONMESSAGENOTI.getValue());
		}
		if (mosipRollingFileAppender.getTotalCap() != null
				&& !mosipRollingFileAppender.getTotalCap().trim().isEmpty()) {
			timeBasedRollingPolicy.setTotalSizeCap(FileSize.valueOf(mosipRollingFileAppender.getTotalCap()));
		}
		timeBasedRollingPolicy.setParent(rollingFileAppender);
		rollingFileAppender.setRollingPolicy(timeBasedRollingPolicy);
		timeBasedRollingPolicy.start();
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
	public static MosipLogger getMosipConsoleLogger(MosipConsoleAppender consoleAppender, String name) {
		if (name.trim().isEmpty()) {
			throw new ClassNameNotFoundException(LogExeptionCodeConstant.CLASSNAMENOTFOUNDEXEPTION.getValue(),
					LogExeptionCodeConstant.CLASSNAMENOTFOUNDEXEPTIONMESSAGE.getValue());
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
	public static MosipLogger getMosipFileLogger(MosipFileAppender fileAppender, String name) {

		if (fileAppender.getFileName() == null)
			throw new FileNameNotProvided(LogExeptionCodeConstant.FILENAMENOTPROVIDED.getValue(),
					LogExeptionCodeConstant.FILENAMENOTPROVIDEDMESSAGENULL.getValue());
		else if (fileAppender.getFileName().trim().isEmpty())
			throw new FileNameNotProvided(LogExeptionCodeConstant.FILENAMENOTPROVIDED.getValue(),
					LogExeptionCodeConstant.FILENAMENOTPROVIDEDMESSAGEEMPTY.getValue());
		else if (name.trim().isEmpty())
			throw new ClassNameNotFoundException(LogExeptionCodeConstant.CLASSNAMENOTFOUNDEXEPTION.getValue(),
					LogExeptionCodeConstant.CLASSNAMENOTFOUNDEXEPTIONMESSAGE.getValue());
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
	public static MosipLogger getMosipRollingFileLogger(MosipRollingFileAppender rollingFileAppender, String name) {
		if (rollingFileAppender.getFileNamePattern() == null)
			throw new EmptyPatternException(LogExeptionCodeConstant.EMPTYPATTERNEXCEPTION.getValue(),
					LogExeptionCodeConstant.EMPTYPATTERNEXCEPTIONMESSAGENULL.getValue());
		else if (rollingFileAppender.getFileNamePattern().trim().isEmpty())
			throw new EmptyPatternException(LogExeptionCodeConstant.EMPTYPATTERNEXCEPTION.getValue(),
					LogExeptionCodeConstant.EMPTYPATTERNEXCEPTIONMESSAGEEMPTY.getValue());
		else if (!rollingFileAppender.getFileNamePattern().contains("%d"))
			throw new PatternSyntaxException(LogExeptionCodeConstant.PATTERNSYNTAXEXCEPTION.getValue(),
					LogExeptionCodeConstant.PATTERNSYNTAXEXCEPTIONMESSAGED.getValue());
		else if (!rollingFileAppender.getMaxFileSize().trim().isEmpty() && rollingFileAppender.getMaxFileSize() != null
				&& !rollingFileAppender.getFileNamePattern().contains("%i"))
			throw new PatternSyntaxException(LogExeptionCodeConstant.PATTERNSYNTAXEXCEPTION.getValue(),
					LogExeptionCodeConstant.PATTERNSYNTAXEXCEPTIONMESSAGEI.getValue());
		else if (rollingFileAppender.getFileName() == null)
			throw new FileNameNotProvided(LogExeptionCodeConstant.FILENAMENOTPROVIDED.getValue(),
					LogExeptionCodeConstant.FILENAMENOTPROVIDEDMESSAGENULL.getValue());
		else if (rollingFileAppender.getFileName().trim().isEmpty())
			throw new FileNameNotProvided(LogExeptionCodeConstant.FILENAMENOTPROVIDED.getValue(),
					LogExeptionCodeConstant.FILENAMENOTPROVIDEDMESSAGEEMPTY.getValue());
		else if (name.trim().isEmpty())
			throw new ClassNameNotFoundException(LogExeptionCodeConstant.CLASSNAMENOTFOUNDEXEPTION.getValue(),
					LogExeptionCodeConstant.CLASSNAMENOTFOUNDEXEPTIONMESSAGE.getValue());
		else
			try {
				return new MosipLogback(rollingFileAppender, name);
			} catch (IllegalStateException e) {
				throw new MosipIllegalStateException(LogExeptionCodeConstant.MOSIPILLEGALSTATEEXCEPTION.getValue(),
						LogExeptionCodeConstant.MOSIPILLEGALSTATEEXCEPTIONMESSAGE.getValue());
			} catch (IllegalArgumentException e) {
				throw new MosipIllegalArgumentException(
						LogExeptionCodeConstant.MOSIPILLEGALARGUMENTEXCEPTION.getValue(),
						LogExeptionCodeConstant.MOSIPILLEGALARGUMENTEXCEPTIONMESSAGE.getValue());
			}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.MosipLogger#debug(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void debug(String sessionId, String idType, String id, String description) {
		logger.debug(LOGDISPLAY, sessionId, idType, id, description);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.MosipLogger#warn(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void warn(String sessionId, String idType, String id, String description) {
		logger.warn(LOGDISPLAY, sessionId, idType, id, description);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.MosipLogger#error(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void error(String sessionId, String idType, String id, String description) {
		logger.error(LOGDISPLAY, sessionId, idType, id, description);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.MosipLogger#info(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void info(String sessionId, String idType, String id, String description) {
		logger.info(LOGDISPLAY, sessionId, idType, id, description);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.MosipLogger#trace(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void trace(String sessionId, String idType, String id, String description) {
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
		ple.setPattern(MosipConfigurationDefault.LOGPATTERN);
		ple.setContext(context);
		ple.start();
		return ple;
	}

}