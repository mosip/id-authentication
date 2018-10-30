
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
import io.mosip.kernel.logger.logback.impl.Logback;
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
	public static Logger getMosipDefaultConsoleLogger(ConsoleAppender mosipConsoleAppender, Class<?> clazz) {
		return Logback.getMosipConsoleLogger(mosipConsoleAppender, clazz.getName());
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
	public static Logger getMosipDefaultConsoleLogger(ConsoleAppender mosipConsoleAppender, String name) {
		return Logback.getMosipConsoleLogger(mosipConsoleAppender, name);
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
	public static Logger getMosipDefaultFileLogger(FileAppender mosipFileAppender, Class<?> clazz) {
		return Logback.getMosipFileLogger(mosipFileAppender, clazz.getName());
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
	public static Logger getMosipDefaultFileLogger(FileAppender mosipFileAppender, String name) {
		return Logback.getMosipFileLogger(mosipFileAppender, name);
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
	public static Logger getMosipDefaultRollingFileLogger(RollingFileAppender mosipRollingFileAppender,
			Class<?> clazz) {
		return Logback.getMosipRollingFileLogger(mosipRollingFileAppender, clazz.getName());
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
	public static Logger getMosipDefaultRollingFileLogger(RollingFileAppender mosipRollingFileAppender,
			String name) {
		return Logback.getMosipRollingFileLogger(mosipRollingFileAppender, name);
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
	public static Logger getMosipConsoleLogger(ConsoleAppender mosipConsoleAppender,
			LoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK) {
			return Logback.getMosipConsoleLogger(mosipConsoleAppender, clazz.getName());
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
	public static Logger getMosipConsoleLogger(ConsoleAppender mosipConsoleAppender,
			LoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return Logback.getMosipConsoleLogger(mosipConsoleAppender, name);
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
	public static Logger getMosipFileLogger(FileAppender mosipFileAppender,
			LoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return Logback.getMosipFileLogger(mosipFileAppender, clazz.getName());
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
	public static Logger getMosipFileLogger(FileAppender mosipFileAppender,
			LoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return Logback.getMosipFileLogger(mosipFileAppender, name);
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
	public static Logger getMosipRollingFileLogger(RollingFileAppender mosipRollingFileAppender,
			LoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return Logback.getMosipRollingFileLogger(mosipRollingFileAppender, clazz.getName());
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
	public static Logger getMosipRollingFileLogger(RollingFileAppender mosipRollingFileAppender,
			LoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return Logback.getMosipRollingFileLogger(mosipRollingFileAppender, name);
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
	public static Logger getMosipDefaultConsoleLogger(File mosipConsoleAppenderFile, Class<?> clazz) {
		return Logback.getMosipConsoleLogger(
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
	public static Logger getMosipDefaultConsoleLogger(File mosipConsoleAppenderFile, String name) {
		return Logback.getMosipConsoleLogger(
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
	public static Logger getMosipDefaultFileLogger(File mosipFileAppenderFile, Class<?> clazz) {
		return Logback.getMosipFileLogger(
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
	public static Logger getMosipDefaultFileLogger(File mosipFileAppenderFile, String name) {
		return Logback.getMosipFileLogger(
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
	public static Logger getMosipDefaultRollingFileLogger(File mosipRollingFileAppenderFile, Class<?> clazz) {
		return Logback.getMosipRollingFileLogger((RollingFileAppender) LoggerUtils
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
	public static Logger getMosipDefaultRollingFileLogger(File mosipRollingFileAppenderFile, String name) {
		return Logback.getMosipRollingFileLogger((RollingFileAppender) LoggerUtils
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
	public static Logger getMosipConsoleLogger(File mosipConsoleAppenderFile, LoggerMethod mosipLoggerMethod,
			Class<?> clazz) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return Logback.getMosipConsoleLogger(
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
	public static Logger getMosipConsoleLogger(File mosipConsoleAppenderFile, LoggerMethod mosipLoggerMethod,
			String name) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return Logback.getMosipConsoleLogger(
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
	public static Logger getMosipFileLogger(File mosipFileAppenderFile, LoggerMethod mosipLoggerMethod,
			Class<?> clazz) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return Logback.getMosipFileLogger(
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
	public static Logger getMosipFileLogger(File mosipFileAppenderFile, LoggerMethod mosipLoggerMethod,
			String name) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return Logback.getMosipFileLogger(
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
	public static Logger getMosipRollingFileLogger(File mosipRollingFileAppenderFile,
			LoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return Logback.getMosipRollingFileLogger((RollingFileAppender) LoggerUtils
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
	public static Logger getMosipRollingFileLogger(File mosipRollingFileAppenderFile,
			LoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod == LoggerMethod.MOSIPLOGBACK)
			return Logback.getMosipRollingFileLogger((RollingFileAppender) LoggerUtils
					.unmarshall(mosipRollingFileAppenderFile, RollingFileAppender.class), name);
		else
			throw new ImplementationNotFound(LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUND.getValue(),
					LogExeptionCodeConstant.IMPLEMENTATIONNOTFOUNDMESSAGE.getValue());
	}

}
