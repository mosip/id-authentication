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
	private static final String MASTER_SYNC = APP_NAME + "MASTER_SYNC - ";
	private static final String UIN_UPDATE = APP_NAME + "UIN_UPDATE - ";
	private static final String USER_ONBOARD = APP_NAME + "USER_ONBOARD - ";
	private static final String SYNC = APP_NAME + "SYNC - ";

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
	public static final String LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER = USER_REGISTRATION
			+ "FINGERPRINT_CAPTURE_CONTROLLER";
	public static final String LOG_REG_REGISTRATION_CONTROLLER = USER_REGISTRATION + "REGISTRATION_CONTROLLER";
	public static final String LOG_REG_SCAN_CONTROLLER = USER_REGISTRATION + "SCAN_CONTROLLER";
	public static final String LOG_REG_FINGERPRINT_FACADE = USER_REGISTRATION + "FINGERPRINT_FACADE";
	public static final String LOG_REG_IRIS_FACADE = USER_REGISTRATION + "IRIS_FACADE";
	public static final String LOG_REG_MASTER_SYNC = MASTER_SYNC + "MASTER_SYNC_CONTROLLER";
	public static final String LOG_REG_UIN_UPDATE = UIN_UPDATE + "UIN_UPDATE_CONTROLLER";
	public static final String LOG_TEMPLATE_GENERATOR = PKT_CREATION + "TEMPLATE_GENERATOR";
	public static final String LOG_REG_DOC_SCAN_CONTROLLER = USER_REGISTRATION + "DOCUMENT_SCAN_CONTROLLER";
	public static final String LOG_REG_IRIS_VALIDATOR = USER_REGISTRATION + "IRIS_VALIDATOR_IMPL";
	public static final String LOG_REG_FACE_VALIDATOR = USER_REGISTRATION + "FACE_VALIDATOR_IMPL";
	public static final String LOG_REG_FACE_FACADE = USER_REGISTRATION + "FACE_FACADE";
	public static final String LOG_REG_USER_ONBOARD = USER_ONBOARD + "USER_ONBOARD_IMLP";
	public static final String OPT_TO_REG_LOGGER_SESSION_ID = SYNC + "SYNC_STATUS_VALIDATOR_SERVICE_IMPL";

	// USER ONBOARD
	public static final String LOG_REG_FINGERPRINT_CAPTURE_USER_ONBOARD = USER_ONBOARD
			+ "FINGERPRINT_CAPTURE_CONTROLLER";
	public static final String LOG_REG_IRIS_CAPTURE_USER_ONBOARD = USER_ONBOARD + "IRIS_CAPTURE_CONTROLLER";

	public static final String BASE_JOB_TITLE = SYNC + "Base_Job";
	public static final String BATCH_JOBS_PROCESS_LOGGER_TITLE = SYNC + "Job_Process_Listener";
	public static final String BATCH_JOBS_TRIGGER_LOGGER_TITLE = SYNC + "Job_Trigger_Listener";
	public static final String PACKET_SYNC_STATUS_JOB_TITLE = SYNC + "Packet_Sync Status_Job";
	public static final String MASTER_SYNC_STATUS_JOB_TITLE = SYNC + "Master_Sync_Status_Job";
	public static final String BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE = SYNC + "Sync_Transaction_Manager";
	public static final String BATCH_JOBS_CONFIG_LOGGER_TITLE = SYNC + "Job_Configuration_Service";
	public static final String PRE_REG_DATA_SYNC_JOB_LOGGER_TITLE = SYNC + "Pre_Registration_Data_Sync_Job";
	public static final String PRE_REG_DATA_SYNC_SERVICE_LOGGER_TITLE = SYNC + "Pre_Registration_Data_Sync_Service";
	public static final String PRE_REG_DATA_SYNC_DAO_LOGGER_TITLE = SYNC + "Pre Registration_Data_Sync_DAO";
	public static final String REGISTRATION_DELETION_JOB_LOGGER_TITLE = SYNC + "Registration_Deletion_Job";
	public static final String SYNC_CONFIG_DATA_JOB_LOGGER_TITLE = SYNC + "Sync_Config_Data Job";
	public static final String GLOBAL_PARAM_SERVICE_LOGGER_TITLE = SYNC + "GLOBAL_PARAMS_SERVICE";
	public static final String AUDIT_SERVICE_LOGGER_TITLE = SYNC + "AUDIT_SERVICE";
	public static final String DELETE_AUDIT_LOGS_JOB = SYNC + "Delete_Audit_Logs_Job";
	public static final String KEY_POLICY_SYNC_JOB_TITLE =SYNC+"key_policy_synch_Job";


}
