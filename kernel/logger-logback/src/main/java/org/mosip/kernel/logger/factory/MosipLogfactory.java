
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

import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipConsoleAppender;
import org.mosip.kernel.logger.appenders.MosipFileAppender;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.constants.LogExeptionCodeConstants;
import org.mosip.kernel.logger.exception.ImplementationNotFound;
import org.mosip.kernel.logger.impl.MosipLogback;

/** Factory class for mosip
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
			Class<?> implementationClazz, Class<?> clazz) {
		if (implementationClazz.equals(MosipLogback.class))
			return MosipLogback.getMosipConsoleLogger(mosipConsoleAppender, clazz.getName());
		else
			throw new ImplementationNotFound(LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
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
			Class<?> implementationClazz, String name) {
		if (implementationClazz.equals(MosipLogback.class))
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
	public static MosipLogger getMosipFileLogger(MosipFileAppender mosipFileAppender, Class<?> implementationClazz,
			Class<?> clazz) {
		if (implementationClazz.equals(MosipLogback.class))
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
	public static MosipLogger getMosipFileLogger(MosipFileAppender mosipFileAppender, Class<?> implementationClazz,
			String name) {
		if (implementationClazz.equals(MosipLogback.class))
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
			Class<?> implementationClazz, Class<?> clazz) {
		if (implementationClazz.equals(MosipLogback.class))
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
			Class<?> implementationClazz, String name) {
		if (implementationClazz.equals(MosipLogback.class))
			return MosipLogback.getMosipRollingFileLogger(mosipRollingFileAppender, name);
		else
			throw new ImplementationNotFound(LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUND,
					LogExeptionCodeConstants.IMPLEMENTATIONNOTFOUNDMESSAGE);
	}

}
