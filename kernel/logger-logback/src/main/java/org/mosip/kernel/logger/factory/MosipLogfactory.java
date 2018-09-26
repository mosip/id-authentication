
/*
 * 
 * 
 * 
 * 
 * 
 * 
 *  
 */
package org.mosip.kernel.logger.factory;

import java.io.File;

import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipConsoleAppender;
import org.mosip.kernel.logger.appenders.MosipFileAppender;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.constants.LogExeptionCodeConstants;
import org.mosip.kernel.logger.constants.MosipLoggerMethod;
import org.mosip.kernel.logger.exception.ImplementationNotFound;
import org.mosip.kernel.logger.impl.MosipLogback;
import org.mosip.kernel.logger.utils.LoggerUtils;

/**
 * Factory class for mosip
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipLogfactory {

	/**
	 * Default constructor for this class
	 */
	private MosipLogfactory() {

	}

	/**
	 * Default Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppender
	 *            {@link MosipConsoleAppender} instance which contains all
	 *            configurations
	 * @param clazz
	 *            Reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultConsoleLogger(MosipConsoleAppender mosipConsoleAppender, Class<?> clazz) {
		return MosipLogback.getMosipConsoleLogger(mosipConsoleAppender, clazz.getName());
	}

	/**
	 * Default Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppender
	 *            {@link MosipConsoleAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            Name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultConsoleLogger(MosipConsoleAppender mosipConsoleAppender, String name) {
		return MosipLogback.getMosipConsoleLogger(mosipConsoleAppender, name);
	}

	/**
	 * Default File factory method to configure logger
	 * 
	 * @param mosipFileAppender
	 *            {@link MosipFileAppender} instance which contains all
	 *            configurations
	 * @param clazz
	 *            Reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultFileLogger(MosipFileAppender mosipFileAppender, Class<?> clazz) {
		return MosipLogback.getMosipFileLogger(mosipFileAppender, clazz.getName());
	}

	/**
	 * Default File factory method to configure logger
	 * 
	 * @param mosipFileAppender
	 *            {@link MosipFileAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            Name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultFileLogger(MosipFileAppender mosipFileAppender, String name) {
		return MosipLogback.getMosipFileLogger(mosipFileAppender, name);
	}

	/**
	 * Default Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link MosipRollingFileAppender} instance which contains all
	 *            configurations
	 * @param clazz
	 *            Reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultRollingFileLogger(MosipRollingFileAppender mosipRollingFileAppender,
			Class<?> clazz) {
		return MosipLogback.getMosipRollingFileLogger(mosipRollingFileAppender, clazz.getName());
	}

	/**
	 * Default Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link MosipRollingFileAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            Name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultRollingFileLogger(MosipRollingFileAppender mosipRollingFileAppender,
			String name) {
		return MosipLogback.getMosipRollingFileLogger(mosipRollingFileAppender, name);
	}

	/**
	 * Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppender
	 *            {@link MosipConsoleAppender} instance which contains all
	 *            configurations
	 * @param implementationClazz
	 *            Type of Logging implementation class reference
	 * @param clazz
	 *            Reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipConsoleLogger(MosipConsoleAppender mosipConsoleAppender,
			MosipLoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod==MosipLoggerMethod.MOSIPLOGBACK){
			return MosipLogback.getMosipConsoleLogger(mosipConsoleAppender, clazz.getName());
		} else {
			throw new ImplementationNotFound(LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
		}
	}

	/**
	 * Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppender
	 *            {@link MosipConsoleAppender} instance which contains all
	 *            configurations
	 * @param implementationClazz
	 *            Type of Logging implementation class reference
	 * @param name
	 *            Name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipConsoleLogger(MosipConsoleAppender mosipConsoleAppender,
			MosipLoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod==MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipConsoleLogger(mosipConsoleAppender, name);
		else
			throw new ImplementationNotFound(LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * File factory method to configure logger
	 * 
	 * @param mosipFileAppender
	 *            {@link MosipFileAppender} instance which contains all
	 *            configurations
	 * @param implementationClazz
	 *            Type of Logging implementation class reference
	 * @param clazz
	 *            Reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipFileLogger(MosipFileAppender mosipFileAppender,
			MosipLoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod==MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipFileLogger(mosipFileAppender, clazz.getName());
		else
			throw new ImplementationNotFound(LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * File factory method to configure logger
	 * 
	 * @param mosipFileAppender
	 *            {@link MosipFileAppender} instance which contains all
	 *            configurations
	 * @param implementationClazz
	 *            Type of Logging implementation class reference
	 * @param name
	 *            Name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipFileLogger(MosipFileAppender mosipFileAppender,
			MosipLoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod==MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipFileLogger(mosipFileAppender, name);
		else
			throw new ImplementationNotFound(LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link MosipRollingFileAppender} instance which contains all
	 *            configurations
	 * @param implementationClazz
	 *            Type of Logging implementation class reference
	 * @param clazz
	 *            Reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipRollingFileLogger(MosipRollingFileAppender mosipRollingFileAppender,
			MosipLoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod==MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipRollingFileLogger(mosipRollingFileAppender, clazz.getName());
		else
			throw new ImplementationNotFound(LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link MosipRollingFileAppender} instance which contains all
	 *            configurations
	 * @param implementationClazz
	 *            Type of Logging implementation class reference
	 * @param name
	 *            Name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipRollingFileLogger(MosipRollingFileAppender mosipRollingFileAppender,
			MosipLoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod==MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipRollingFileLogger(mosipRollingFileAppender, name);
		else
			throw new ImplementationNotFound(LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * Default Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppenderFile
	 *            XML file containing mosip console logger configurations
	 * @param clazz
	 *            Reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultConsoleLogger(File mosipConsoleAppenderFile, Class<?> clazz) {
		return MosipLogback.getMosipConsoleLogger(
				(MosipConsoleAppender) LoggerUtils.unmarshell(mosipConsoleAppenderFile, MosipConsoleAppender.class),
				clazz.getName());
	}

	/**
	 * Default Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppenderFile
	 *            XML file containing mosip console logger configurations
	 * @param name
	 *            Name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultConsoleLogger(File mosipConsoleAppenderFile, String name) {
		return MosipLogback.getMosipConsoleLogger(
				(MosipConsoleAppender) LoggerUtils.unmarshell(mosipConsoleAppenderFile, MosipConsoleAppender.class),
				name);
	}

	/**
	 * Default File factory method to configure logger
	 * 
	 * @param mosipFileAppenderFile
	 *            XML file containing mosip file logger configurations
	 * @param clazz
	 *            Reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultFileLogger(File mosipFileAppenderFile, Class<?> clazz) {
		return MosipLogback.getMosipFileLogger(
				(MosipFileAppender) LoggerUtils.unmarshell(mosipFileAppenderFile, MosipFileAppender.class),
				clazz.getName());
	}

	/**
	 * Default File factory method to configure logger
	 * 
	 * @param mosipFileAppenderFile
	 *            XML file containing mosip file logger configurations
	 * @param name
	 *            Name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultFileLogger(File mosipFileAppenderFile, String name) {
		return MosipLogback.getMosipFileLogger(
				(MosipFileAppender) LoggerUtils.unmarshell(mosipFileAppenderFile, MosipFileAppender.class), name);
	}

	/**
	 * Default Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppenderFile
	 *            XML file containing mosip rolling file logger configurations
	 * @param clazz
	 *            Reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultRollingFileLogger(File mosipRollingFileAppenderFile, Class<?> clazz) {
		return MosipLogback.getMosipRollingFileLogger((MosipRollingFileAppender) LoggerUtils
				.unmarshell(mosipRollingFileAppenderFile, MosipRollingFileAppender.class), clazz.getName());
	}

	/**
	 * Default Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppenderFile
	 *            XML file containing mosip rolling file logger configurations
	 * @param name
	 *            Name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultRollingFileLogger(File mosipRollingFileAppenderFile, String name) {
		return MosipLogback.getMosipRollingFileLogger((MosipRollingFileAppender) LoggerUtils
				.unmarshell(mosipRollingFileAppenderFile, MosipRollingFileAppender.class), name);
	}

	/**
	 * Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppenderFile
	 *            XML file containing mosip console logger configurations
	 * @param implementationClazz
	 *            Type of Logging implementation class reference
	 * @param clazz
	 *            Reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipConsoleLogger(File mosipConsoleAppenderFile, MosipLoggerMethod mosipLoggerMethod,
			Class<?> clazz) {
		if (mosipLoggerMethod==MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipConsoleLogger(
					(MosipConsoleAppender) LoggerUtils.unmarshell(mosipConsoleAppenderFile, MosipConsoleAppender.class),
					clazz.getName());
		else
			throw new ImplementationNotFound(LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppenderFile
	 *            XML file containing mosip console logger configurations
	 * @param implementationClazz
	 *            Type of Logging implementation class reference
	 * @param name
	 *            Name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipConsoleLogger(File mosipConsoleAppenderFile, MosipLoggerMethod mosipLoggerMethod,
			String name) {
		if (mosipLoggerMethod==MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipConsoleLogger(
					(MosipConsoleAppender) LoggerUtils.unmarshell(mosipConsoleAppenderFile, MosipConsoleAppender.class),
					name);
		else
			throw new ImplementationNotFound(LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * File factory method to configure logger
	 * 
	 * @param mosipFileAppenderFile
	 *            XML file containing mosip file logger configurations
	 * @param implementationClazz
	 *            Type of Logging implementation class reference
	 * @param clazz
	 *            Reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipFileLogger(File mosipFileAppenderFile, MosipLoggerMethod mosipLoggerMethod,
			Class<?> clazz) {
		if (mosipLoggerMethod==MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipFileLogger(
					(MosipFileAppender) LoggerUtils.unmarshell(mosipFileAppenderFile, MosipFileAppender.class),
					clazz.getName());
		else
			throw new ImplementationNotFound(LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * File factory method to configure logger
	 * 
	 * @param mosipFileAppenderFile
	 *            XML file containing mosip file logger configurations
	 * @param implementationClazz
	 *            Type of Logging implementation class reference
	 * @param name
	 *            Name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipFileLogger(File mosipFileAppenderFile, MosipLoggerMethod mosipLoggerMethod,
			String name) {
		if (mosipLoggerMethod==MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipFileLogger(
					(MosipFileAppender) LoggerUtils.unmarshell(mosipFileAppenderFile, MosipFileAppender.class), name);
		else
			throw new ImplementationNotFound(LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppenderFile
	 *            XML file containing mosip rolling file logger configurations
	 * @param implementationClazz
	 *            Type of Logging implementation class reference
	 * @param clazz
	 *            Reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipRollingFileLogger(File mosipRollingFileAppenderFile,
			MosipLoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod==MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipRollingFileLogger((MosipRollingFileAppender) LoggerUtils
					.unmarshell(mosipRollingFileAppenderFile, MosipRollingFileAppender.class), clazz.getName());
		else
			throw new ImplementationNotFound(LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppenderFile
	 *            XML file containing mosip rolling file logger configurations
	 * @param implementationClazz
	 *            Type of Logging implementation class reference
	 * @param name
	 *            Name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipRollingFileLogger(File mosipRollingFileAppenderFile,
			MosipLoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod==MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipRollingFileLogger((MosipRollingFileAppender) LoggerUtils
					.unmarshell(mosipRollingFileAppenderFile, MosipRollingFileAppender.class), name);
		else
			throw new ImplementationNotFound(LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

}
