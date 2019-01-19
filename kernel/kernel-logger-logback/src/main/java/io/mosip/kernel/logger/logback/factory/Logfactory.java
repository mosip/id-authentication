
/*
 * 
 * 
 * 
 * 
 * 
 * 
 *  
 */
package io.mosip.kernel.logger.logback.factory;

import java.io.File;

import io.mosip.kernel.core.logger.exception.ImplementationNotFound;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.appender.ConsoleAppender;
import io.mosip.kernel.logger.logback.appender.FileAppender;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;
import io.mosip.kernel.logger.logback.constant.LogExeptionCodeConstant;
import io.mosip.kernel.logger.logback.constant.LoggerMethod;
import io.mosip.kernel.logger.logback.impl.LoggerImpl;
import io.mosip.kernel.logger.logback.util.LoggerUtils;

/**
 * Factory class for mosip
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class Logfactory {

	/**
	 * Default constructor for this class
	 */
	private Logfactory() {

	}

	/**
	 * Default Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppender
	 *            {@link ConsoleAppender} instance which contains all
	 *            configurations
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getDefaultConsoleLogger(ConsoleAppender mosipConsoleAppender, Class<?> clazz) {
		return LoggerImpl.getConsoleLogger(mosipConsoleAppender, clazz.getName());
	}

	/**
	 * Default Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppender
	 *            {@link ConsoleAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getDefaultConsoleLogger(ConsoleAppender mosipConsoleAppender, String name) {
		return LoggerImpl.getConsoleLogger(mosipConsoleAppender, name);
	}

	/**
	 * Default File factory method to configure logger
	 * 
	 * @param mosipFileAppender
	 *            {@link FileAppender} instance which contains all
	 *            configurations
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getDefaultFileLogger(FileAppender mosipFileAppender, Class<?> clazz) {
		return LoggerImpl.getFileLogger(mosipFileAppender, clazz.getName());
	}

	/**
	 * Default File factory method to configure logger
	 * 
	 * @param mosipFileAppender
	 *            {@link FileAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getDefaultFileLogger(FileAppender mosipFileAppender, String name) {
		return LoggerImpl.getFileLogger(mosipFileAppender, name);
	}

	/**
	 * Default Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link RollingFileAppender} instance which contains all
	 *            configurations
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getDefaultRollingFileLogger(RollingFileAppender mosipRollingFileAppender,
			Class<?> clazz) {
		return LoggerImpl.getRollingFileLogger(mosipRollingFileAppender, clazz.getName());
	}

	/**
	 * Default Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link RollingFileAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getDefaultRollingFileLogger(RollingFileAppender mosipRollingFileAppender,
			String name) {
		return LoggerImpl.getRollingFileLogger(mosipRollingFileAppender, name);
	}

	/**
	 * Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppender
	 *            {@link ConsoleAppender} instance which contains all
	 *            configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getConsoleLogger(ConsoleAppender mosipConsoleAppender,
			LoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK) {
			return LoggerImpl.getConsoleLogger(mosipConsoleAppender, clazz.getName());
		} else {
			throw new ImplementationNotFound(LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUND.getValue(),
					LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUNDMESSAGE.getValue());
		}
	}

	/**
	 * Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppender
	 *            {@link ConsoleAppender} instance which contains all
	 *            configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation
	 * @param name
	 *            name of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getConsoleLogger(ConsoleAppender mosipConsoleAppender,
			LoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return LoggerImpl.getConsoleLogger(mosipConsoleAppender, name);
		else
			throw new ImplementationNotFound(LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUND.getValue(),
					LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUNDMESSAGE.getValue());
	}

	/**
	 * File factory method to configure logger
	 * 
	 * @param mosipFileAppender
	 *            {@link FileAppender} instance which contains all
	 *            configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getFileLogger(FileAppender mosipFileAppender,
			LoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return LoggerImpl.getFileLogger(mosipFileAppender, clazz.getName());
		else
			throw new ImplementationNotFound(LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUND.getValue(),
					LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUNDMESSAGE.getValue());
	}

	/**
	 * File factory method to configure logger
	 * 
	 * @param mosipFileAppender
	 *            {@link FileAppender} instance which contains all
	 *            configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation
	 * @param name
	 *            name of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getFileLogger(FileAppender mosipFileAppender,
			LoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return LoggerImpl.getFileLogger(mosipFileAppender, name);
		else
			throw new ImplementationNotFound(LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUND.getValue(),
					LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUNDMESSAGE.getValue());
	}

	/**
	 * Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link RollingFileAppender} instance which contains all
	 *            configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getRollingFileLogger(RollingFileAppender mosipRollingFileAppender,
			LoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return LoggerImpl.getRollingFileLogger(mosipRollingFileAppender, clazz.getName());
		else
			throw new ImplementationNotFound(LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUND.getValue(),
					LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUNDMESSAGE.getValue());
	}

	/**
	 * Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link RollingFileAppender} instance which contains all
	 *            configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation
	 * @param name
	 *            name of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getRollingFileLogger(RollingFileAppender mosipRollingFileAppender,
			LoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return LoggerImpl.getRollingFileLogger(mosipRollingFileAppender, name);
		else
			throw new ImplementationNotFound(LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUND.getValue(),
					LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUNDMESSAGE.getValue());
	}

	/**
	 * Default Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppenderFile
	 *            XML file containing mosip console logger configurations
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getDefaultConsoleLogger(File mosipConsoleAppenderFile, Class<?> clazz) {
		return LoggerImpl.getConsoleLogger(
				(ConsoleAppender) LoggerUtils.unmarshall(mosipConsoleAppenderFile, ConsoleAppender.class),
				clazz.getName());
	}

	/**
	 * Default Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppenderFile
	 *            XML file containing mosip console logger configurations
	 * @param name
	 *            name of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getDefaultConsoleLogger(File mosipConsoleAppenderFile, String name) {
		return LoggerImpl.getConsoleLogger(
				(ConsoleAppender) LoggerUtils.unmarshall(mosipConsoleAppenderFile, ConsoleAppender.class),
				name);
	}

	/**
	 * Default File factory method to configure logger
	 * 
	 * @param mosipFileAppenderFile
	 *            XML file containing mosip file logger configurations
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getDefaultFileLogger(File mosipFileAppenderFile, Class<?> clazz) {
		return LoggerImpl.getFileLogger(
				(FileAppender) LoggerUtils.unmarshall(mosipFileAppenderFile, FileAppender.class),
				clazz.getName());
	}

	/**
	 * Default File factory method to configure logger
	 * 
	 * @param mosipFileAppenderFile
	 *            XML file containing mosip file logger configurations
	 * @param name
	 *            name of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getDefaultFileLogger(File mosipFileAppenderFile, String name) {
		return LoggerImpl.getFileLogger(
				(FileAppender) LoggerUtils.unmarshall(mosipFileAppenderFile, FileAppender.class), name);
	}

	/**
	 * Default Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppenderFile
	 *            XML file containing mosip rolling file logger configurations
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getDefaultRollingFileLogger(File mosipRollingFileAppenderFile, Class<?> clazz) {
		return LoggerImpl.getRollingFileLogger((RollingFileAppender) LoggerUtils
				.unmarshall(mosipRollingFileAppenderFile, RollingFileAppender.class), clazz.getName());
	}

	/**
	 * Default Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppenderFile
	 *            XML file containing mosip rolling file logger configurations
	 * @param name
	 *            name of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getDefaultRollingFileLogger(File mosipRollingFileAppenderFile, String name) {
		return LoggerImpl.getRollingFileLogger((RollingFileAppender) LoggerUtils
				.unmarshall(mosipRollingFileAppenderFile, RollingFileAppender.class), name);
	}

	/**
	 * Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppenderFile
	 *            XML file containing mosip console logger configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getConsoleLogger(File mosipConsoleAppenderFile, LoggerMethod mosipLoggerMethod,
			Class<?> clazz) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return LoggerImpl.getConsoleLogger(
					(ConsoleAppender) LoggerUtils.unmarshall(mosipConsoleAppenderFile, ConsoleAppender.class),
					clazz.getName());
		else
			throw new ImplementationNotFound(LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUND.getValue(),
					LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUNDMESSAGE.getValue());
	}

	/**
	 * Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppenderFile
	 *            XML file containing mosip console logger configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation
	 * @param name
	 *            name of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getConsoleLogger(File mosipConsoleAppenderFile, LoggerMethod mosipLoggerMethod,
			String name) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return LoggerImpl.getConsoleLogger(
					(ConsoleAppender) LoggerUtils.unmarshall(mosipConsoleAppenderFile, ConsoleAppender.class),
					name);
		else
			throw new ImplementationNotFound(LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUND.getValue(),
					LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUNDMESSAGE.getValue());
	}

	/**
	 * File factory method to configure logger
	 * 
	 * @param mosipFileAppenderFile
	 *            XML file containing mosip file logger configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getFileLogger(File mosipFileAppenderFile, LoggerMethod mosipLoggerMethod,
			Class<?> clazz) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return LoggerImpl.getFileLogger(
					(FileAppender) LoggerUtils.unmarshall(mosipFileAppenderFile, FileAppender.class),
					clazz.getName());
		else
			throw new ImplementationNotFound(LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUND.getValue(),
					LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUNDMESSAGE.getValue());
	}

	/**
	 * File factory method to configure logger
	 * 
	 * @param mosipFileAppenderFile
	 *            XML file containing mosip file logger configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation
	 * @param name
	 *            name of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getFileLogger(File mosipFileAppenderFile, LoggerMethod mosipLoggerMethod,
			String name) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return LoggerImpl.getFileLogger(
					(FileAppender) LoggerUtils.unmarshall(mosipFileAppenderFile, FileAppender.class), name);
		else
			throw new ImplementationNotFound(LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUND.getValue(),
					LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUNDMESSAGE.getValue());
	}

	/**
	 * Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppenderFile
	 *            XML file containing mosip rolling file logger configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getRollingFileLogger(File mosipRollingFileAppenderFile,
			LoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return LoggerImpl.getRollingFileLogger((RollingFileAppender) LoggerUtils
					.unmarshall(mosipRollingFileAppenderFile, RollingFileAppender.class), clazz.getName());
		else
			throw new ImplementationNotFound(LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUND.getValue(),
					LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUNDMESSAGE.getValue());
	}

	/**
	 * Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppenderFile
	 *            XML file containing mosip rolling file logger configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation
	 * @param name
	 *            name of the calling class
	 * @return configured {@link Logger} instance
	 */
	public static Logger getRollingFileLogger(File mosipRollingFileAppenderFile,
			LoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return LoggerImpl.getRollingFileLogger((RollingFileAppender) LoggerUtils
					.unmarshall(mosipRollingFileAppenderFile, RollingFileAppender.class), name);
		else
			throw new ImplementationNotFound(LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUND.getValue(),
					LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUNDMESSAGE.getValue());
	}
	
	public static void stop(String appendersName) {
		LoggerImpl.stop(appendersName);
	}

	
	public static void stopAll() {
		LoggerImpl.stopAll();
	}

}
