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
	private static final String PKT_APPROVAL = APP_NAME + "REGISTRATION_APPROVAL - ";
	private static final String USER_REGISTRATION = APP_NAME + "USER_REGISTRATION - ";
	private static final String MASTER_SYNC = APP_NAME + "MASTER_SYNC - ";
	private static final String UIN_UPDATE = APP_NAME + "UIN_UPDATE - ";
	private static final String USER_ONBOARD = APP_NAME + "USER_ONBOARD - ";
	private static final String PKT_SCAN = APP_NAME + "PACKET_SCAN - ";
	private static final String SYNC = APP_NAME + "SYNC - ";
	private static final String OTP = APP_NAME + "OTP";
	private static final String PKT_HANDLER = APP_NAME + "PACKET_HANDLER - ";
	private static final String HEADER = APP_NAME + "HEADER - ";
	private static final String HOME = APP_NAME + "HOME - ";
	private static final String BASE = APP_NAME + "BASE - ";
	private static final String LOGIN = APP_NAME + "LOGIN - ";
	private static final String PGE_FLW = APP_NAME + "PAGE_FLOW - ";
	private static final String AUTH = APP_NAME + "AUTHENTICATION - ";
	private static final String AUTHZ = APP_NAME + "AUTHORIZATION - ";
	private static final String SERVICE_DELEGATE_UTIL = APP_NAME + "SERVICE_DELEGATE_UTIL - ";
	private static final String UI_VALIDATIONS = APP_NAME + "VALIDATION - ";
	private static final String REG_ID_MASTER_DATA_VALIDATOR = APP_NAME + "MASTER_DATA_VALIDATION - ";

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
	public static final String LOG_REG_PENDING_APPROVAL = PKT_APPROVAL + "REGISTRATION_APPROVAL_CONTROLLER";
	public static final String LOG_REG_PENDING_ACTION = PKT_APPROVAL + "REGISTRATION_PENDING_ACTION_CONTROLLER";
	public static final String LOG_REG_EOD_CONTROLLER = PKT_APPROVAL + "EOD_CONTROLLER";
	public static final String LOG_REG_ONHOLD_CONTROLLER = PKT_APPROVAL + "ONHOLD_CONTROLLER";
	public static final String LOG_REG_REJECT_CONTROLLER = PKT_APPROVAL + "REJECTION_CONTROLLER";
	public static final String LOG_REG_IRIS_CAPTURE_CONTROLLER = USER_REGISTRATION + "IRIS_CAPTURE_CONTROLLER";
	public static final String LOG_REG_BIOMETRIC_SCAN_CONTROLLER = USER_REGISTRATION + "FINGERPRINT_SCAN_CONTROLLER";
	public static final String LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER = USER_REGISTRATION
			+ "FINGERPRINT_CAPTURE_CONTROLLER";
	public static final String MDM_REQUEST_RESPONSE_BUILDER = USER_REGISTRATION + "MDM_REQUEST_RESPONSE_BUILDER";
	public static final String MOSIP_BIO_DEVICE_INTEGERATOR = USER_REGISTRATION + "MOSIP_BIO_DEVICE_INTEGERATOR";
	public static final String MOSIP_BIO_DEVICE_MANAGER = USER_REGISTRATION + "MOSIP BIO DEVICE MANAGER";
	public static final String LOG_REG_REGISTRATION_CONTROLLER = USER_REGISTRATION + "REGISTRATION_CONTROLLER";
	public static final String LOG_REG_SCAN_CONTROLLER = USER_REGISTRATION + "SCAN_CONTROLLER";
	public static final String LOG_REG_FINGERPRINT_FACADE = USER_REGISTRATION + "FINGERPRINT_FACADE";
	public static final String LOG_REG_IRIS_FACADE = USER_REGISTRATION + "IRIS_FACADE";
	public static final String LOG_REG_MASTER_SYNC = MASTER_SYNC + "MASTER_SYNC_CONTROLLER";
	public static final String LOG_REG_USER_SALT_SYNC = "USER_SALT_SYNC";
	public static final String LOG_REG_UIN_UPDATE = UIN_UPDATE + "UIN_UPDATE_CONTROLLER";
	public static final String LOG_TEMPLATE_GENERATOR = PKT_CREATION + "TEMPLATE_GENERATOR";
	public static final String LOG_REG_DOC_SCAN_CONTROLLER = USER_REGISTRATION + "DOCUMENT_SCAN_CONTROLLER";
	public static final String LOG_REG_IRIS_VALIDATOR = USER_REGISTRATION + "IRIS_VALIDATOR_IMPL";
	public static final String LOG_REG_FACE_VALIDATOR = USER_REGISTRATION + "FACE_VALIDATOR_IMPL";
	public static final String LOG_REG_FACE_FACADE = USER_REGISTRATION + "FACE_FACADE";
	public static final String LOG_REG_USER_ONBOARD = USER_ONBOARD + "USER_ONBOARD_IMLP";
	public static final String OPT_TO_REG_LOGGER_SESSION_ID = SYNC + "SYNC_STATUS_VALIDATOR_SERVICE_IMPL";
	public static final String LOG_REG_HEADER = HEADER + "HEADER_CONTROLLER";
	public static final String LOG_REG_HOME = HOME + "HOME_CONTROLLER";
	public static final String LOG_REG_BASE = BASE + "BASE_CONTROLLER";
	public static final String LOG_REG_LOGIN = LOGIN + "LOGIN_CONTROLLER";
	public static final String AUTHENTICATION_SRVICE = "AUTHENTICATION_SRVICE";
	public static final String FINGER_PRINT_AUTHENTICATION = "FINGER_PRINT_AUTHENTICATION";
	public static final String BIO_SERVICE = "BIO_SERVICE";
	public static final String LOG_REG_PAGE_FLOW = PGE_FLW + "PAGE_FLOW";
	public static final String LOG_REG_USER_DETAIL = SYNC + "USER_DETAIL_SERVICE_IMPL";
	public static final String LOG_REG_USER_DETAIL_DAO = SYNC + "USER_DETAIL_DAO_IMPL";
	public static final String LOG_REG_AUTH = AUTH + "AUTHENTICATION_CONTROLLER";
	public static final String LOG_REG_GUARDIAN_BIOMETRIC_CONTROLLER = USER_REGISTRATION
			+ "GUARDIAN_BIOMETRIC_CONTROLLER";
	// USER ONBOARD
	public static final String LOG_REG_FINGERPRINT_CAPTURE_USER_ONBOARD = USER_ONBOARD
			+ "FINGERPRINT_CAPTURE_CONTROLLER";
	public static final String LOG_REG_IRIS_CAPTURE_USER_ONBOARD = USER_ONBOARD + "IRIS_CAPTURE_CONTROLLER";
	public static final String LOG_REG_PARENT_USER_ONBOARD = USER_ONBOARD + "USER_ONBOARD_PARENT_CONTROLLER";

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
	public static final String AUDIT_SERVICE_LOGGER_TITLE = SYNC + "AUDIT_MANAGER_SERVICE";
	public static final String DELETE_AUDIT_LOGS_JOB = SYNC + "Delete_Audit_Logs_Job";
	public static final String KEY_POLICY_SYNC_JOB_TITLE = SYNC + "key_policy_synch_Job";
	public static final String REG_PACKET_SYNC_STATUS_JOB = SYNC + "Packet_Sync Status_Job";
	public static final String PACKET_HANDLER = PKT_HANDLER + "Packet_Handler_Controller";
	public static final String OTP_MANAGER_LOGGER_TITLE = OTP + "OTP_MANAGER";
	public static final String REG_PACKET_VIRUS_SCAN = PKT_SCAN + "Registration_packet virus_scan_job";
	public static final String USER_DETAIL_SERVICE_JOB_TITLE = SYNC + "user_detail_Service_Job";
	public static final String AUTHZ_ADVICE = AUTHZ + "REST_CLIENT_AUTH_ADVICE";
	public static final String LOG_SERVICE_DELEGATE_UTIL_GET = SERVICE_DELEGATE_UTIL + "GET";
	public static final String LOG_SERVICE_DELEGATE_UTIL_POST = SERVICE_DELEGATE_UTIL + "POST";
	public static final String LOG_SERVICE_DELEGATE_UTIL_PREPARE_GET = SERVICE_DELEGATE_UTIL + "PREPARE_GET_REQUEST";
	public static final String LOG_SERVICE_DELEGATE_UTIL_PREPARE_POST = SERVICE_DELEGATE_UTIL + "PREPARE_POST_REQUEST";
	public static final String LOG_SERVICE_DELEGATE_UTIL_PREPARE_REQUEST = SERVICE_DELEGATE_UTIL + "PREPARE_REQUEST";
	public static final String LOG_SERVICE_DELEGATE_AUTH_DTO = SERVICE_DELEGATE_UTIL + "PREPARE_AUTHN_REQUEST_DTO";
	public static final String LOG_SERVICE_DELEGATE_GET_TOKEN = SERVICE_DELEGATE_UTIL + "GET_AUTH_TOKEN";
	public static final String LOG_SERVICE_DELEGATE_VALIDATE_TOKEN = SERVICE_DELEGATE_UTIL + "IS_AUTH_TOKEN_VALID";
	public static final String DATE_VALIDATION = UI_VALIDATIONS + "DATE_VALIDATIONS";
	public static final String RESPONSE_SIGNATURE_VALIDATION = "RESPONSE_SIGNATURE_VALIDATION";
	public static final String AUTHORIZE_USER_ID = "AUTHORIZE_USER_ID";
	public static final String REGISTRATION_PUBLIC_KEY_SYNC ="REGISTRATION_PUBLIC_KEY_SYNC";
	public static final String PUBLIC_KEY_SYNC_STATUS_JOB_TITLE = SYNC + "Public_key_Sync_Status_Job";
	public static final String USER_SALT_SYNC_STATUS_JOB_TITLE = SYNC + "User_Salt_Sync_Status_Job";
	public static final String REG_ID_OBJECT_MASTER_DATA_VALIDATOR = REG_ID_MASTER_DATA_VALIDATOR + "REGISTRATION_ID_OBJECT_MASTER_DATA_VALIDATOR";

	// Util
	public static final String UTIL_PUBLIC_KEY_GENERATION = APP_NAME
			+ "PUBLIC_KEY_GENRATION_UTIL - GENERATE_PUBLIC_KEY";

	// Registration Update
	public static final String LOG_REG_UPDATE = HEADER + "REGISTARTION_UPDATE";

	// TPM
	private static final String TPM_SIGNATURE = APP_NAME + "SIGNATURE_SERVICE - ";
	private static final String TPM_SIGN_VALIDATION = APP_NAME + "SIGNATURE_VALIDATION_SERVICE - ";
	private static final String TPM_ASYMMETRIC_KEY_CREATION = APP_NAME + "ASYMMETRIC_KEY_CREATION - ";
	private static final String TPM_ASYMMETRIC_ENCRYPTION = APP_NAME + "ASYMMETRIC_ENCRYPTION_SERVICE - ";
	private static final String TPM_ASYMMETRIC_DECRYPTION = APP_NAME + "ASYMMETRIC_DECRYPTION_SERVICE - ";
	private static final String TPM_SERVICE = APP_NAME + "TPM_SERVICE - ";
	private static final String LOG_MODULE = APP_NAME + "TPM - ";

	public static final String LOG_TPM_INITIALIZATION = LOG_MODULE + "PLATFORM_TPM_INITIALIZATION";
	public static final String LOG_PUBLIC_KEY = LOG_MODULE + "SIGN_KEY_CREATION_SERVICE";
	public static final String TPM_SIGN_DATA = TPM_SIGNATURE + "SIGN_DATA";
	public static final String TPM_SIGN_VALIDATE_BY_KEY = TPM_SIGN_VALIDATION + "VALIDATE_SIGNATURE_USING_PUBLIC_KEY";
	public static final String TPM_ASYM_KEY_CREATION = TPM_ASYMMETRIC_KEY_CREATION + "CREATE_PERSISTENT_KEY";
	public static final String TPM_ASYM_ENCRYPTION = TPM_ASYMMETRIC_ENCRYPTION + "ENCRYPT_USING_TPM";
	public static final String TPM_ASYM_DECRYPTION = TPM_ASYMMETRIC_DECRYPTION + "DECRYPT_USING_TPM";
	public static final String TPM_SERVICE_SIGN = TPM_SERVICE + "SIGN_DATA";
	public static final String TPM_SERVICE_VALIDATE_SIGN_BY_PUBLIC_PART = TPM_SERVICE
			+ "VALIDATING_SIGNATURE_USING_PUBLIC_PART";
	public static final String TPM_SERVICE_ASYMMETRIC_ENCRYPTION = TPM_SERVICE + "ASYMMETRIC_ENCRYPT";
	public static final String TPM_SERVICE_ASYMMETRIC_DECRYPTION = TPM_SERVICE + "ASYMMETRIC_DECRYPT";
	public static final String TPM_SERVICE_GET_SIGN_PUBLIC = TPM_SERVICE + "GET_SIGNING_PUBLIC_PART";

	public static final String LOG_REG_MAC_ADDRESS = "MAC_ADDRESS_UTILL";

	public static final String ID_OBJECT_SCHEMA_VALIDATOR = UI_VALIDATIONS + "ID_OBJECT_SCHEMA_VALIDATOR";
	public static final String ID_OBJECT_PATTERN_VALIDATOR = UI_VALIDATIONS + "ID_OBJECT_PATTERN_VALIDATOR";

}
