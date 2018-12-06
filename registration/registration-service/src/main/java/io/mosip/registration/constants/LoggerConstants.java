package io.mosip.registration.constants;

/**
 * Contains the constants to be used for logging
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class LoggerConstants {

	// Private Constructor
	private LoggerConstants() {

	}

	// Application Name
	private static final String APP_NAME = "REGISTRATION - ";

	// Components
	private static final String PKT_CREATION = APP_NAME + "PACKET_CREATION - ";
	private static final String PKT_STORAGE = APP_NAME + "PACKET_STORAGE_MANAGER - ";
	private static final String AUDIT_LOG_SYNC = APP_NAME + "AUDIT_LOG_SYNCHER - ";
	private static final String DEVICE_ONBOARD = APP_NAME + "DEVICE_ONBOARDING - ";
	private static final String PKT_APPROVAL = APP_NAME + "REGISTRATION_APPROVAL - ";
	private static final String USER_REGISTRATION = APP_NAME + "USER_REGISTRATION - ";

	// Session IDs' for logging
	public static final String LOG_PKT_HANLDER = PKT_CREATION + "PACKET_HANDLER";
	public static final String LOG_PKT_CREATION = PKT_CREATION + "CREATE";
	public static final String LOG_ZIP_CREATION = PKT_CREATION + "ZIP_PACKET";
	public static final String LOG_PKT_ENCRYPTION = PKT_CREATION + "PACKET_ENCRYPTION";
	public static final String LOG_PKT_AES_ENCRYPTION = PKT_CREATION + "PACKET_AES_ENCRPTION_SERVICE";
	public static final String LOG_PKT_AES_SEEDS = PKT_CREATION + "AES_SESSION_KEY_SEEDS_GENERATION";
	public static final String LOG_PKT_AES_KEY_GENERATION = PKT_CREATION + "AES_SESSION_KEY_GENERATION";
	public static final String LOG_PKT_RSA_ENCRYPTION = PKT_CREATION + "RSA_ENCRYPTION_SERVICE";
	public static final String LOG_SAVE_PKT = PKT_CREATION + "SAVE_REGISTRATION";
	public static final String LOG_PKT_STORAGE = PKT_STORAGE + "PACKET_STORAGE_SERVICE";
	public static final String LOG_AUDIT_DAO = AUDIT_LOG_SYNC + "AUDIT_DAO";
	public static final String DEVICE_ONBOARD_CONTROLLER = DEVICE_ONBOARD + "DEVICE_ONBOARDING_CONTROLLER";
	public static final String DEVICE_ONBOARD_PAGE_NAVIGATION = DEVICE_ONBOARD
			+ "REGISTRATION_OFFICER_DETAILS_CONTROLLER";
	public static final String LOG_REG_PENDING_APPROVAL = PKT_APPROVAL + "REGISTRATION_APPROVAL_CONTROLLER";
	public static final String LOG_REG_PENDING_ACTION = PKT_APPROVAL + "REGISTRATION_PENDING_ACTION_CONTROLLER";
	public static final String LOG_REG_EOD_CONTROLLER = PKT_APPROVAL + "EOD_CONTROLLER";
	public static final String LOG_REG_ONHOLD_CONTROLLER = PKT_APPROVAL + "ONHOLD_CONTROLLER";
	public static final String LOG_REG_REJECT_CONTROLLER = PKT_APPROVAL + "REJECTION_CONTROLLER";
	public static final String LOG_REG_IRIS_CAPTURE_CONTROLLER = USER_REGISTRATION + "IRIS_CAPTURE_CONTROLLER";
	public static final String LOG_REG_BIOMETRIC_SCAN_CONTROLLER = USER_REGISTRATION + "FINGERPRINT_SCAN_CONTROLLER";
	public static final String LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER = USER_REGISTRATION + "FINGERPRINT_CAPTURE_CONTROLLER";
	public static final String LOG_REG_REGISTRATION_CONTROLLER = USER_REGISTRATION + "REGISTRATION_CONTROLLER";
	public static final String LOG_REG_SCAN_CONTROLLER = USER_REGISTRATION + "SCAN_CONTROLLER";
	public static final String LOG_REG_FINGERPRINT_FACADE = USER_REGISTRATION + "FINGERPRINT_FACADE";

}
