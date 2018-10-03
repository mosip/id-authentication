
/*
 * 
 * 
 * 
 * 
 * 
 * 
 *  
 */
package io.mosip.kernel.logger.factory;

import java.io.File;

import io.mosip.kernel.core.spi.logger.MosipLogger;

import io.mosip.kernel.logger.appender.MosipConsoleAppender;
import io.mosip.kernel.logger.appender.MosipFileAppender;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.constant.LogExeptionCodeConstants;
import io.mosip.kernel.logger.constant.MosipLoggerMethod;
import io.mosip.kernel.logger.exception.ImplementationNotFound;
import io.mosip.kernel.logger.impl.MosipLogback;
import io.mosip.kernel.logger.util.LoggerUtils;

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
	 *            reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultConsoleLogger(
			MosipConsoleAppender mosipConsoleAppender, Class<?> clazz) {
		return MosipLogback.getMosipConsoleLogger(mosipConsoleAppender,
				clazz.getName());
	}

	/**
	 * Default Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppender
	 *            {@link MosipConsoleAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultConsoleLogger(
			MosipConsoleAppender mosipConsoleAppender, String name) {
		return MosipLogback.getMosipConsoleLogger(mosipConsoleAppender, name);
	}

	/**
	 * Default File factory method to configure logger
	 * 
	 * @param mosipFileAppender
	 *            {@link MosipFileAppender} instance which contains all
	 *            configurations
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultFileLogger(
			MosipFileAppender mosipFileAppender, Class<?> clazz) {
		return MosipLogback.getMosipFileLogger(mosipFileAppender,
				clazz.getName());
	}

	/**
	 * Default File factory method to configure logger
	 * 
	 * @param mosipFileAppender
	 *            {@link MosipFileAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultFileLogger(
			MosipFileAppender mosipFileAppender, String name) {
		return MosipLogback.getMosipFileLogger(mosipFileAppender, name);
	}

	/**
	 * Default Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link MosipRollingFileAppender} instance which contains all
	 *            configurations
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultRollingFileLogger(
			MosipRollingFileAppender mosipRollingFileAppender, Class<?> clazz) {
		return MosipLogback.getMosipRollingFileLogger(mosipRollingFileAppender,
				clazz.getName());
	}

	/**
	 * Default Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link MosipRollingFileAppender} instance which contains all
	 *            configurations
	 * @param name
	 *            name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultRollingFileLogger(
			MosipRollingFileAppender mosipRollingFileAppender, String name) {
		return MosipLogback.getMosipRollingFileLogger(mosipRollingFileAppender,
				name);
	}

	/**
	 * Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppender
	 *            {@link MosipConsoleAppender} instance which contains all
	 *            configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation 
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipConsoleLogger(
			MosipConsoleAppender mosipConsoleAppender,
			MosipLoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod == MosipLoggerMethod.MOSIPLOGBACK) {
			return MosipLogback.getMosipConsoleLogger(mosipConsoleAppender,
					clazz.getName());
		} else {
			throw new ImplementationNotFound(
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
		}
	}

	/**
	 * Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppender
	 *            {@link MosipConsoleAppender} instance which contains all
	 *            configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation 
	 * @param name
	 *            name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipConsoleLogger(
			MosipConsoleAppender mosipConsoleAppender,
			MosipLoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod == MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipConsoleLogger(mosipConsoleAppender,
					name);
		else
			throw new ImplementationNotFound(
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * File factory method to configure logger
	 * 
	 * @param mosipFileAppender
	 *            {@link MosipFileAppender} instance which contains all
	 *            configurations
	  * @param mosipLoggerMethod
	 *            type of Logging implementation 
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipFileLogger(
			MosipFileAppender mosipFileAppender,
			MosipLoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod == MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipFileLogger(mosipFileAppender,
					clazz.getName());
		else
			throw new ImplementationNotFound(
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * File factory method to configure logger
	 * 
	 * @param mosipFileAppender
	 *            {@link MosipFileAppender} instance which contains all
	 *            configurations
	  * @param mosipLoggerMethod
	 *            type of Logging implementation 
	 * @param name
	 *            name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipFileLogger(
			MosipFileAppender mosipFileAppender,
			MosipLoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod == MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipFileLogger(mosipFileAppender, name);
		else
			throw new ImplementationNotFound(
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link MosipRollingFileAppender} instance which contains all
	 *            configurations
	 * @param mosipLoggerMethod
	 *            type of Logging implementation 
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipRollingFileLogger(
			MosipRollingFileAppender mosipRollingFileAppender,
			MosipLoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod == MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipRollingFileLogger(
					mosipRollingFileAppender, clazz.getName());
		else
			throw new ImplementationNotFound(
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppender
	 *            {@link MosipRollingFileAppender} instance which contains all
	 *            configurations
	  * @param mosipLoggerMethod
	 *            type of Logging implementation 
	 * @param name
	 *            name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipRollingFileLogger(
			MosipRollingFileAppender mosipRollingFileAppender,
			MosipLoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod == MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback
					.getMosipRollingFileLogger(mosipRollingFileAppender, name);
		else
			throw new ImplementationNotFound(
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

	/**
	 * Default Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppenderFile
	 *            XML file containing mosip console logger configurations
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultConsoleLogger(
			File mosipConsoleAppenderFile, Class<?> clazz) {
		return MosipLogback.getMosipConsoleLogger(
				(MosipConsoleAppender) LoggerUtils.unmarshall(
						mosipConsoleAppenderFile, MosipConsoleAppender.class),
				clazz.getName());
	}

	/**
	 * Default Console factory method to configure logger
	 * 
	 * @param mosipConsoleAppenderFile
	 *            XML file containing mosip console logger configurations
	 * @param name
	 *            name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultConsoleLogger(
			File mosipConsoleAppenderFile, String name) {
		return MosipLogback.getMosipConsoleLogger(
				(MosipConsoleAppender) LoggerUtils.unmarshall(
						mosipConsoleAppenderFile, MosipConsoleAppender.class),
				name);
	}

	/**
	 * Default File factory method to configure logger
	 * 
	 * @param mosipFileAppenderFile
	 *            XML file containing mosip file logger configurations
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultFileLogger(
			File mosipFileAppenderFile, Class<?> clazz) {
		return MosipLogback.getMosipFileLogger(
				(MosipFileAppender) LoggerUtils.unmarshall(
						mosipFileAppenderFile, MosipFileAppender.class),
				clazz.getName());
	}

	/**
	 * Default File factory method to configure logger
	 * 
	 * @param mosipFileAppenderFile
	 *            XML file containing mosip file logger configurations
	 * @param name
	 *            name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultFileLogger(
			File mosipFileAppenderFile, String name) {
		return MosipLogback
				.getMosipFileLogger(
						(MosipFileAppender) LoggerUtils.unmarshall(
								mosipFileAppenderFile, MosipFileAppender.class),
						name);
	}

	/**
	 * Default Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppenderFile
	 *            XML file containing mosip rolling file logger configurations
	 * @param clazz
	 *            reference of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultRollingFileLogger(
			File mosipRollingFileAppenderFile, Class<?> clazz) {
		return MosipLogback
				.getMosipRollingFileLogger(
						(MosipRollingFileAppender) LoggerUtils.unmarshall(
								mosipRollingFileAppenderFile,
								MosipRollingFileAppender.class),
						clazz.getName());
	}

	/**
	 * Default Rolling file factory method to configure logger
	 * 
	 * @param mosipRollingFileAppenderFile
	 *            XML file containing mosip rolling file logger configurations
	 * @param name
	 *            name of the calling class
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipDefaultRollingFileLogger(
			File mosipRollingFileAppenderFile, String name) {
		return MosipLogback.getMosipRollingFileLogger(
				(MosipRollingFileAppender) LoggerUtils.unmarshall(
						mosipRollingFileAppenderFile,
						MosipRollingFileAppender.class),
				name);
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
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipConsoleLogger(
			File mosipConsoleAppenderFile, MosipLoggerMethod mosipLoggerMethod,
			Class<?> clazz) {
		if (mosipLoggerMethod == MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback
					.getMosipConsoleLogger(
							(MosipConsoleAppender) LoggerUtils.unmarshall(
									mosipConsoleAppenderFile,
									MosipConsoleAppender.class),
							clazz.getName());
		else
			throw new ImplementationNotFound(
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
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
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipConsoleLogger(
			File mosipConsoleAppenderFile, MosipLoggerMethod mosipLoggerMethod,
			String name) {
		if (mosipLoggerMethod == MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback
					.getMosipConsoleLogger((MosipConsoleAppender) LoggerUtils
							.unmarshall(mosipConsoleAppenderFile,
									MosipConsoleAppender.class),
							name);
		else
			throw new ImplementationNotFound(
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
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
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipFileLogger(File mosipFileAppenderFile,
			MosipLoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod == MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipFileLogger(
					(MosipFileAppender) LoggerUtils.unmarshall(
							mosipFileAppenderFile, MosipFileAppender.class),
					clazz.getName());
		else
			throw new ImplementationNotFound(
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
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
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipFileLogger(File mosipFileAppenderFile,
			MosipLoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod == MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipFileLogger(
					(MosipFileAppender) LoggerUtils.unmarshall(
							mosipFileAppenderFile, MosipFileAppender.class),
					name);
		else
			throw new ImplementationNotFound(
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
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
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipRollingFileLogger(
			File mosipRollingFileAppenderFile,
			MosipLoggerMethod mosipLoggerMethod, Class<?> clazz) {
		if (mosipLoggerMethod == MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback
					.getMosipRollingFileLogger(
							(MosipRollingFileAppender) LoggerUtils.unmarshall(
									mosipRollingFileAppenderFile,
									MosipRollingFileAppender.class),
							clazz.getName());
		else
			throw new ImplementationNotFound(
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
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
	 * @return configured {@link MosipLogger} instance
	 */
	public static MosipLogger getMosipRollingFileLogger(
			File mosipRollingFileAppenderFile,
			MosipLoggerMethod mosipLoggerMethod, String name) {
		if (mosipLoggerMethod == MosipLoggerMethod.MOSIPLOGBACK)
			return MosipLogback.getMosipRollingFileLogger(
					(MosipRollingFileAppender) LoggerUtils.unmarshall(
							mosipRollingFileAppenderFile,
							MosipRollingFileAppender.class),
					name);
		else
			throw new ImplementationNotFound(
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

}
