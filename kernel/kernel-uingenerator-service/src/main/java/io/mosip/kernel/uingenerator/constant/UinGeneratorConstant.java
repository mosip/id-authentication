/**
 * 
 */
package io.mosip.kernel.uingenerator.constant;

/**
 * Constants for Uin generator
 * 
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public final class UinGeneratorConstant {

	/**
	 * Private constructor for UinGeneratorConstants
	 */
	private UinGeneratorConstant() {
	}

	/**
	 * The string field UTC
	 */
	public static final String UTC = "UTC";
	/**
	 * The string field DEFAULTADMIN_MOSIP_IO
	 */
	public static final String DEFAULTADMIN_MOSIP_IO = "defaultadmin@mosip.io";

	public static final String UNUSED = "UNUSED";
	public static final String ISSUED = "ISSUED";
	public static final String ASSIGNED = "ASSIGNED";
	public static final String UNASSIGNED = "UNASSIGNED";
	/**
	 * The string field for http port
	 */
	public static final String SERVER_PORT = "server.port";

	/**
	 * The string field SERVER_SERVLET_PATH
	 */
	public static final String SERVER_SERVLET_PATH = "server.servlet.path";

	/**
	 * The string field V1_0_UIN
	 */
	public static final String VUIN = "/uin";

	/**
	 * The string field COMMA
	 */
	public static final String COMMA = ",";
	/**
	 * The string field SPRING_APPLICATION_NAME
	 */
	public static final String SPRING_APPLICATION_NAME = "spring.application.name";
	/**
	 * The string field SPRING_APPLICATION_NAME
	 */
	public static final String SPRING_CLOUD_CONFIG_NAME = "spring.cloud.config.name";
	/**
	 * The string field PROPERTIES
	 */
	public static final String PROPERTIES = ".properties";
	/**
	 * The string field DASH
	 */
	public static final String DASH = "-";
	/**
	 * The string field SPRING_CLOUD_CONFIG_LABEL
	 */
	public static final String SPRING_CLOUD_CONFIG_LABEL = "spring.cloud.config.label";
	/**
	 * The string field FORWARD_SLASH
	 */
	public static final String FORWARD_SLASH = "/";
	/**
	 * The string field SPRING_PROFILES_ACTIVE
	 */
	public static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";
	/**
	 * The string field KERNEL
	 */
	public static final String KERNEL = "/kernel/";
	/**
	 * The string field SPRING_CLOUD_CONFIG_URI
	 */
	public static final String SPRING_CLOUD_CONFIG_URI = "spring.cloud.config.uri";

	/**
	 * The string field for GENERATE_UIN
	 */
	public static final String GENERATE_UIN = "GENERATE_UIN";

	/**
	 * The string field for UIN_GENERATOR_ADDRESS
	 */
	public static final String UIN_GENERATOR_ADDRESS = "UIN_GENERATOR_ADDRESS";

	/**
	 * The string field for http port
	 */
	public static final String HTTP_PORT = "http.port";

	/**
	 * The string field for empty string
	 */
	public static final String EMPTY_STRING = "";

	/**
	 * The string field 0
	 */
	public static final String ZERO = "0";

	/**
	 * The string field 2
	 */
	public static final String TWO = "2";

	/**
	 * The string field 9
	 */
	public static final String NINE = "9";

	/**
	 * The string field ZERO_TO_NINE
	 */
	public static final String ZERO_TO_NINE = "1234567890";

	/**
	 * The string field CONTENTTYPE
	 */
	public static final String CONTENTTYPE = "application/json";

	/**
	 * The string field HEALTH_ENDPOINT
	 */
	public static final String HEALTH_ENDPOINT = "/actuator/health";

	/**
	 * The string field current working directory
	 */
	public static final String CURRENT_WORKING_DIRECTORY = "user.dir";

	public static final String APPLICATION_JSON = "application/json";

	/**
	 * The static field for ConfigStoreOptions type to be used in
	 * ConfigRetriever[viz. spring-config-server , file etc.]
	 */
	public static final String CONFIG_STORE_OPTIONS_TYPE = "spring-config-server";
	/**
	 * The static field value for time-out
	 */
	public static final String CONFIG_SERVER_FETCH_TIME_OUT = "180000";
	/**
	 * The static field for url
	 */
	public static final String URL = "url";
	/**
	 * The static field for time-out
	 */
	public static final String TIME_OUT = "timeout";

	public static final String SWAGGER_UI_JSON_FILENAME = "swagger.json";

	public static final String SWAGGER_UI_PATH = "swagger-ui";

	/**
	 * The swagger ui json file path.
	 */
	public static final String SWAGGER_UI_JSON_PATH = SWAGGER_UI_PATH + "/" + SWAGGER_UI_JSON_FILENAME;

	/**
	 * The swagger json template file.
	 */
	public static final String SWAGGER_JSON_TEMPLATE = "swagger-template.vm";
}
