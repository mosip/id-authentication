
package io.mosip.kernel.logger.logback.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.core.exception.IllegalStateException;
import io.mosip.kernel.core.logger.exception.ClassNameNotFoundException;
import io.mosip.kernel.core.logger.exception.EmptyPatternException;
import io.mosip.kernel.core.logger.exception.FileNameNotProvided;
import io.mosip.kernel.core.exception.PatternSyntaxException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.appender.ConsoleAppender;
import io.mosip.kernel.logger.logback.appender.FileAppender;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;
import io.mosip.kernel.logger.logback.constant.LogExeptionCodeConstant;
import io.mosip.kernel.logger.logback.constant.ConfigurationDefault;

/**
 * Logback implementation class for mosip
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class LoggerImpl implements Logger {

	private static Map<String, Appender<ILoggingEvent>> rollingFileAppenders = new HashMap<>();
	private static Map<String, Appender<ILoggingEvent>> fileAppenders = new HashMap<>();
	/**
	 * Logger Instance per Class
	 */
	private ch.qos.logback.classic.Logger logger;

	/**
	 * Display pattern of logs
	 */
	private static final String LOGDISPLAY = "{} - {} - {} - {}";

	/**
	 * Builds a logger instance
	 * 
	 * @param mosipConsoleAppender
	 *            {@link ConsoleAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of calling class to get logger
	 */
	private LoggerImpl(ConsoleAppender mosipConsoleAppender, String name) {

		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		this.logger = context.getLogger(name);
		PatternLayoutEncoder ple = getdefaultPattern(context);
		ch.qos.logback.core.ConsoleAppender<ILoggingEvent> consoleAppender = new ch.qos.logback.core.ConsoleAppender<>();
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
	 *            {@link FileAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of calling class to get logger
	 */
	private LoggerImpl(FileAppender mosipFileAppender, String name) {

		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		this.logger = context.getLogger(name);
		this.logger.setAdditive(false);
		PatternLayoutEncoder ple = getdefaultPattern(context);
		ch.qos.logback.core.FileAppender<ILoggingEvent> fileAppender = null;
		if (!fileAppenders.containsKey(mosipFileAppender.getAppenderName())) {
			fileAppender = new ch.qos.logback.core.FileAppender<>();
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
			fileAppender = (ch.qos.logback.core.FileAppender<ILoggingEvent>) fileAppenders.get(mosipFileAppender.getAppenderName());
		}
		this.logger.addAppender(fileAppender);
	}

	/**
	 * Builds a logger instance
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link RollingFileAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of calling class to get logger
	 */
	private LoggerImpl(RollingFileAppender mosipRollingFileAppender, String name) {

		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		this.logger = context.getLogger(name);
		this.logger.setAdditive(false);
		PatternLayoutEncoder ple = getdefaultPattern(context);
		ch.qos.logback.core.rolling.RollingFileAppender<ILoggingEvent> rollingFileAppender = null;
		if (!rollingFileAppenders.containsKey(mosipRollingFileAppender.getAppenderName())) {
			rollingFileAppender = new ch.qos.logback.core.rolling.RollingFileAppender<>();
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
			rollingFileAppender = (ch.qos.logback.core.rolling.RollingFileAppender<ILoggingEvent>) rollingFileAppenders
					.get(mosipRollingFileAppender.getAppenderName());
		}

		this.logger.addAppender(rollingFileAppender);

	}

	/**
	 * Configures size and time based policy
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link RollingFileAppender} instance to get values'p
	 * @param context
	 *            context of logger
	 * @param rollingFileAppender
	 *            {@link RollingFileAppender} instance by which this policy will
	 *            attach
	 */
	private void configureSizeAndTimeBasedPolicy(RollingFileAppender mosipRollingFileAppender,
			LoggerContext context, ch.qos.logback.core.rolling.RollingFileAppender<ILoggingEvent> rollingFileAppender) {
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
	 *            {@link RollingFileAppender} instance to get values
	 * @param context
	 *            context of logger
	 * @param rollingFileAppender
	 *            {@link RollingFileAppender} instance by which this policy will
	 *            attach
	 */
	private void configureTimeBasedRollingPolicy(RollingFileAppender mosipRollingFileAppender,
			LoggerContext context, ch.qos.logback.core.rolling.RollingFileAppender<ILoggingEvent> rollingFileAppender) {
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
	 *            {@link ConsoleAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of the calling class
	 * @return Configured {@link Logger} instance
	 */
	public static Logger getConsoleLogger(ConsoleAppender consoleAppender, String name) {
		if (name.trim().isEmpty()) {
			throw new ClassNameNotFoundException(LogExeptionCodeConstant.CLASSNAMENOTFOUNDEXEPTION.getValue(),
					LogExeptionCodeConstant.CLASSNAMENOTFOUNDEXEPTIONMESSAGE.getValue());
		} else {
			return new LoggerImpl(consoleAppender, name);
		}
	}

	/**
	 * Verifies configurations
	 * 
	 * @param fileAppender
	 *            {@link FileAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of the calling class
	 * @return Configured {@link Logger} instance
	 */
	public static Logger getFileLogger(FileAppender fileAppender, String name) {

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
			return new LoggerImpl(fileAppender, name);
		}
	}

	/**
	 * Verifies configurations
	 * 
	 * @param rollingFileAppender
	 *            {@link RollingFileAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of the calling class
	 * @return Configured {@link Logger} instance
	 */
	public static Logger getRollingFileLogger(RollingFileAppender rollingFileAppender, String name) {
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
				return new LoggerImpl(rollingFileAppender, name);
			} catch (java.lang.IllegalStateException e) {
				throw new IllegalStateException(LogExeptionCodeConstant.MOSIPILLEGALSTATEEXCEPTION.getValue(),
						LogExeptionCodeConstant.MOSIPILLEGALSTATEEXCEPTIONMESSAGE.getValue());
			} catch (java.lang.IllegalArgumentException e) {
				throw new IllegalArgumentException(
						LogExeptionCodeConstant.MOSIPILLEGALARGUMENTEXCEPTION.getValue(),
						LogExeptionCodeConstant.MOSIPILLEGALARGUMENTEXCEPTIONMESSAGE.getValue());
			}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.Logger#debug(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void debug(String sessionId, String idType, String id, String description) {
		logger.debug(LOGDISPLAY, sessionId, idType, id, description);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.Logger#warn(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void warn(String sessionId, String idType, String id, String description) {
		logger.warn(LOGDISPLAY, sessionId, idType, id, description);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.Logger#error(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void error(String sessionId, String idType, String id, String description) {
		logger.error(LOGDISPLAY, sessionId, idType, id, description);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.Logger#info(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void info(String sessionId, String idType, String id, String description) {
		logger.info(LOGDISPLAY, sessionId, idType, id, description);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.logging.Logger#trace(java.lang.String,
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
		ple.setPattern(ConfigurationDefault.LOGPATTERN);
		ple.setContext(context);
		ple.start();
		return ple;
	}
	
	public static void stop(String appenderName) {
		if(fileAppenders.containsKey(appenderName)) {
		    fileAppenders.get(appenderName).stop();
		    fileAppenders.remove(appenderName);
		}else if (rollingFileAppenders.containsKey(appenderName)) {
			rollingFileAppenders.get(appenderName).stop();
			rollingFileAppenders.remove(appenderName);
		}
	}
	
	public static void stopAll() {
		rollingFileAppenders.values().forEach(x -> x.stop());
		fileAppenders.values().forEach(x -> x.stop());
	}
}