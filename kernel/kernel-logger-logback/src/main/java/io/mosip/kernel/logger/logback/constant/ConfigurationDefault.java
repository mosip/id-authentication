/*
 * 
 * 
 * 
 * 
 * 
 */
/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.logger.logback.constant;

/**
 * Default value of Configurations
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public final class ConfigurationDefault {

	/**
	 * Default constructor for this class
	 */
	private ConfigurationDefault() {

	}

	/**
	 * Default value of Immediate Flush
	 */
	public static final boolean DEFAULTIMMEDIATEFLUSH = true;
	/**
	 * Default value of Append
	 */
	public static final boolean DEFAULTAPPEND = true;
	/**
	 * Default value of Prudent
	 */
	public static final boolean DEFAULTPRUDENT = false;
	/**
	 * Default value of Target
	 */
	public static final String DEFAULTARGET = "System.out";
	/**
	 * Default value of Max File History
	 */
	public static final int DEFAULMAXFILEHISTORY = 0;
	/**
	 * Default value of Total History
	 */
	public static final String DEFAULTTOTALCAP = "";
	/**
	 * Default value of File Size
	 */
	public static final String DEFAULTFILESIZE = "";

	/**
	 * Default pattern of logs
	 */
	public static final String LOGPATTERN = "%d{yyyy-MM-dd'T'HH:mm:ssXXX} - [%logger] - %-5level - %msg%n";

}
