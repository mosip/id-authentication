package io.mosip.registration.processor.core.config;

/**
 * @author Pranav Kumar
 * @since 0.11.0
 * 
 * This is class containing all the constants for accessing config server
 *
 */
public class ConfigurationUtil {
	
	public static final String CLOUD_CONFIG_URI = "spring.cloud.config.uri";
	public static final String CLOUD_CONFIG_LABEL = "spring.cloud.config.label";
	public static final String ACTIVE_PROFILES = "spring.profiles.active";
	public static final String APPLICATION_NAMES = "spring.application.name";
	public static final String CONFIG_SERVER_TYPE = "spring-config-server";
	public static final String CONFIG_SERVER_TIME_OUT = "70000";
	
	private ConfigurationUtil() {
		
	}
}
