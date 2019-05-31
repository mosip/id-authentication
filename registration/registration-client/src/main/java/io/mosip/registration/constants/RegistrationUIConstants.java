package io.mosip.registration.constants;

import java.util.ResourceBundle;

import io.mosip.registration.context.ApplicationContext;

public class RegistrationUIConstants {

	// Key values to read value from messages.properties file

	public static final ResourceBundle bundle = ApplicationContext.applicationMessagesBundle();
	public static final String OTP_VALIDITY = bundle.getString("OTP_VALIDITY");
	public static final String MINUTES = bundle.getString("MINUTES");

	public static String getMessageLanguageSpecific(String key) {
		return bundle.getString(key);
	}

	// ALERT
	public static final String ERROR = bundle.getString("ERROR");
	public static final String INFORMATION = bundle.getString("INFORMATION");
	public static final String SUCCESS = bundle.getString("SUCCESS");
	public static final String FAILURE = bundle.getString("FAILURE");

	// LOGIN
	public static final String UNABLE_LOAD_LOGIN_SCREEN = bundle.getString("UNABLE_LOAD_LOGIN_SCREEN");
	public static final String BIOMETRIC_DISABLE_SCREEN_1 = bundle.getString("BIOMETRIC_DISABLE_SCREEN_1");
	public static final String BIOMETRIC_DISABLE_SCREEN_2 = bundle.getString("BIOMETRIC_DISABLE_SCREEN_2");
	public static final String MISSING_MANDATOTY_FIELDS = bundle.getString("MISSING_MANDATOTY_FIELDS");
	public static final String CREDENTIALS_FIELD_EMPTY = bundle.getString("CREDENTIALS_FIELD_EMPTY");
	public static final String USERNAME_FIELD_EMPTY = bundle.getString("USERNAME_FIELD_EMPTY");
	public static final String PWORD_FIELD_EMPTY = bundle.getString("PWORD_FIELD_EMPTY");
	public static final String USRNAME_LENGTH = bundle.getString("USRNAME_LENGTH");
	public static final String PWORD_LENGTH = bundle.getString("PWORD_LENGTH");
	public static final String USER_NOT_ONBOARDED = bundle.getString("USER_NOT_ONBOARDED");
	public static final String USER_NOT_AUTHORIZED = bundle.getString("USER_NOT_AUTHORIZED");
	public static final String INCORRECT_PWORD = bundle.getString("INCORRECT_PWORD");
	public static final String BLOCKED_USER_ERROR = bundle.getString("BLOCKED_USER_ERROR");
	public static final String USERNAME_FIELD_ERROR = bundle.getString("USERNAME_FIELD_ERROR");
	public static final String OTP_FIELD_EMPTY = bundle.getString("OTP_FIELD_EMPTY");
	public static final String OTP_GENERATION_SUCCESS_MESSAGE = bundle.getString("OTP_GENERATION_SUCCESS_MESSAGE");
	public static final String OTP_GENERATION_ERROR_MESSAGE = bundle.getString("OTP_GENERATION_ERROR_MESSAGE");
	public static final String OTP_VALIDATION_SUCCESS_MESSAGE = bundle.getString("OTP_VALIDATION_SUCCESS_MESSAGE");
	public static final String OTP_VALIDATION_ERROR_MESSAGE = bundle.getString("OTP_VALIDATION_ERROR_MESSAGE");
	public static final String FINGER_PRINT_MATCH = bundle.getString("FINGER_PRINT_MATCH");
	public static final String IRIS_MATCH = bundle.getString("IRIS_MATCH");
	public static final String FACE_MATCH = bundle.getString("FACE_MATCH");
	public static final String FINGERPRINT = bundle.getString("FINGERPRINT");
	public static final String RECAPTURE = bundle.getString("RECAPTURE");
	public static final String SECONDS = bundle.getString("SECONDS");

	// Lost UIN
	public static final String LOST_UIN_REQUEST_ERROR = bundle.getString("LOST_UIN_REQUEST_ERROR");

	// AUTHORIZATION
	public static final String ROLES_EMPTY_ERROR = bundle.getString("ROLES_EMPTY_ERROR");
	public static final String MACHINE_MAPPING_ERROR = bundle.getString("MACHINE_MAPPING_ERROR");
	public static final String AUTHORIZATION_ERROR = bundle.getString("AUTHORIZATION_ERROR");

	// MACHINE CENTER REMAP
	public static final String REMAP_NO_ACCESS_MESSAGE = bundle.getString("REMAP_NO_ACCESS_MESSAGE");
	public static final String REMAP_EOD_PROCESS_MESSAGE = bundle.getString("REMAP_EOD_PROCESS_MESSAGE");
	public static final String REMAP_CLICK_OK = bundle.getString("REMAP_CLICK_OK");
	public static final String REMAP_PROCESS_SUCCESS = bundle.getString("REMAP_PROCESS_SUCCESS");
	public static final String REMAP_NOT_APPLICABLE = bundle.getString("REMAP_NOT_APPLICABLE");
	public static final String REMAP_PROCESS_STILL_PENDING = bundle.getString("REMAP_PROCESS_STILL_PENDING");

	// DEVICE
	public static final String DEVICE_FP_NOT_FOUND = bundle.getString("DEVICE_FP_NOT_FOUND");
	public static final String FP_DEVICE_TIMEOUT = bundle.getString("FP_DEVICE_TIMEOUT");
	public static final String FP_DEVICE_ERROR = bundle.getString("FP_DEVICE_ERROR");
	public static final String FP_CAPTURE_SUCCESS = bundle.getString("FP_CAPTURE_SUCCESS");
	public static final String WEBCAM_ALERT_CONTEXT = bundle.getString("WEBCAM_ALERT_CONTEXT");
	public static final String FACE_CAPTURE_ERROR = bundle.getString("FACE_CAPTURE_ERROR");
	public static final String FACE_SCANNING_ERROR = bundle.getString("FACE_SCANNING_ERROR");
	public static final String DEVICE_ONBOARD_NOTIFICATION = bundle.getString("DEVICE_ONBOARD_NOTIFICATION");

	// LOCK ACCOUNT
	public static final String USER_ACCOUNT_LOCK_MESSAGE_NUMBER = bundle.getString("USER_ACCOUNT_LOCK_MESSAGE_NUMBER");
	public static final String USER_ACCOUNT_LOCK_MESSAGE = bundle.getString("USER_ACCOUNT_LOCK_MESSAGE");
	public static final String USER_ACCOUNT_LOCK_MESSAGE_MINUTES = bundle
			.getString("USER_ACCOUNT_LOCK_MESSAGE_MINUTES");

	// NOTIFICATIONS
	public static final String EMAIL_NOTIFICATION_SUCCESS = bundle.getString("EMAIL_NOTIFICATION_SUCCESS");
	public static final String SMS_NOTIFICATION_SUCCESS = bundle.getString("SMS_NOTIFICATION_SUCCESS");
	public static final String NO_VALID_EMAIL = bundle.getString("NO_VALID_EMAIL");
	public static final String NO_VALID_MOBILE = bundle.getString("NO_VALID_MOBILE");
	public static final String NOTIFICATION_SUCCESS = bundle.getString("NOTIFICATION_SUCCESS");
	public static final String NOTIFICATION_FAIL = bundle.getString("NOTIFICATION_FAIL");
	public static final String NOTIFICATION_SMS_FAIL = bundle.getString("NOTIFICATION_SMS_FAIL");
	public static final String NOTIFICATION_EMAIL_FAIL = bundle.getString("NOTIFICATION_EMAIL_FAIL");
	public static final String NOTIFICATION_LIMIT_EXCEEDED = bundle.getString("NOTIFICATION_LIMIT_EXCEEDED");
	public static final String PACKET_CREATION_FAILURE = bundle.getString("PACKET_CREATION_FAILURE");

	// SUCCESS
	public static final String PACKET_CREATED_SUCCESS = bundle.getString("PACKET_CREATED_SUCCESS");
	public static final String PRINT_INITIATION_SUCCESS = bundle.getString("PRINT_INITIATION_SUCCESS");
	public static final String REREGISTRATION_APPROVE_SUCCESS = bundle.getString("REREGISTRATION_APPROVE_SUCCESS");
	public static final String REREGISTER_TITLEPANE = bundle.getString("REREGISTER_TITLEPANE");
	public static final String APPLICATIONS = bundle.getString("APPLICATIONS");
	public static final String NO_PENDING_APPLICATIONS = bundle.getString("NO_PENDING_APPLICATIONS");
	public static final String NO_RE_REGISTER_APPLICATIONS = bundle.getString("NO_RE_REGISTER_APPLICATIONS");

	// AUTHENTICATION
	public static final String AUTHENTICATION_FAILURE = bundle.getString("AUTHENTICATION_FAILURE");
	public static final String AUTH_APPROVAL_SUCCESS_MSG = bundle.getString("AUTH_APPROVAL_SUCCESS_MSG");
	public static final String AUTH_PENDING_ACTION_SUCCESS_MSG = bundle.getString("AUTH_PENDING_ACTION_SUCCESS_MSG");
	public static final String AUTHENTICATION_ERROR_MSG = bundle.getString("AUTHENTICATION_ERROR_MSG");
	public static final String APPROVED = bundle.getString("APPROVED");
	public static final String REJECTED = bundle.getString("REJECTED");
	public static final String PENDING = bundle.getString("PENDING");
	public static final String INFORMED = bundle.getString("INFORMED");
	public static final String CANTINFORMED = bundle.getString("CANTINFORMED");

	// CAMERA
	public static final String APPLICANT_IMAGE_ERROR = bundle.getString("APPLICANT_IMAGE_ERROR");
	public static final String DEMOGRAPHIC_DETAILS_ERROR_CONTEXT = bundle
			.getString("DEMOGRAPHIC_DETAILS_ERROR_CONTEXT");

	// REGISTRATION
	public static final String AGE_WARNING = bundle.getString("AGE_WARNING");
	public static final String TO = bundle.getString("TO");
	public static final String MIN_AGE_WARNING = bundle.getString("MIN_AGE_WARNING");
	public static final String POA_DOCUMENT_EMPTY = bundle.getString("poaDocuments");
	public static final String POI_DOCUMENT_EMPTY = bundle.getString("poiDocuments");
	public static final String POR_DOCUMENT_EMPTY = bundle.getString("porDocuments");
	public static final String DOB_DOCUMENT_EMPTY = bundle.getString("dobDocuments");
	public static final String SCAN_DOCUMENT_ERROR = bundle.getString("SCAN_DOCUMENT_ERROR");
	public static final String UNABLE_LOAD_SCAN_POPUP = bundle.getString("UNABLE_LOAD_SCAN_POPUP");
	public static final String SCAN_DOC_TITLE = bundle.getString("SCAN_DOC_TITLE");
	public static final String SCAN_DOC_CATEGORY_MULTIPLE = bundle.getString("SCAN_DOC_CATEGORY_MULTIPLE");
	public static final String SCAN_DOC_SUCCESS = bundle.getString("SCAN_DOC_SUCCESS");
	public static final String SCAN_DOC_SIZE = bundle.getString("SCAN_DOC_SIZE");
	public static final String SCAN_DOC_INFO = bundle.getString("SCAN_DOC_INFO");
	public static final String SCAN_DOCUMENT_CONNECTION_ERR = bundle.getString("SCAN_DOCUMENT_CONNECTION_ERR");
	public static final String SCAN_DOCUMENT_EMPTY = bundle.getString("SCAN_DOCUMENT_EMPTY");
	public static final String SCAN_DOCUMENT_CONVERTION_ERR = bundle.getString("SCAN_DOCUMENT_CONVERTION_ERR");
	public static final String PRE_REG_ID_EMPTY = bundle.getString("PRE_REG_ID_EMPTY");
	public static final String REG_LGN_001 = bundle.getString("REG_LGN_001");
	public static final String PRE_REG_ID_NOT_VALID = bundle.getString("PRE_REG_ID_NOT_VALID");
	public static final String REG_ID_JSON_VALIDATION_FAILED = bundle.getString("REG_ID_JSON_VALIDATION_FAILED");
	public static final String SCAN = bundle.getString("SCAN");
	public static final String PLEASE_SELECT = bundle.getString("PLEASE_SELECT");
	public static final String DOCUMENT = bundle.getString("DOCUMENT");
	public static final String DATE_VALIDATION_MSG = bundle.getString("DATE_VALIDATION_MSG");
	public static final String PHOTO_CAPTURE = bundle.getString("PHOTO_CAPTURE");
	public static final String PREVIOUS_ADDRESS = bundle.getString("PREVIOUS_ADDRESS");
	public static final String PREVIEW_DOC = bundle.getString("PREVIEW_DOC");
	public static final String RID_INVALID = bundle.getString("RID_INVALID");
	public static final String UIN_INVALID = bundle.getString("UIN_INVALID");
	public static final String IS_BLOCKED_WORD = bundle.getString("IS_BLOCKED_WORD");
	public static final String THRESHOLD = bundle.getString("THRESHOLD");
	public static final String INVALID_DATE = bundle.getString("INVALID_DATE");
	public static final String INVALID_YEAR = bundle.getString("INVALID_YEAR");
	public static final String FUTURE_DOB = bundle.getString("FUTURE_DOB");
	public static final String INVALID_AGE = bundle.getString("INVALID_AGE");
	public static final String INVALID_MONTH = bundle.getString("INVALID_MONTH");
	public static final String SELECT = bundle.getString("SELECT");
	public static final String LEFT_SLAP = bundle.getString("LEFT_SLAP");
	public static final String RIGHT_SLAP = bundle.getString("RIGHT_SLAP");
	public static final String THUMBS = bundle.getString("THUMBS");
	public static final String RIGHT_IRIS = bundle.getString("RIGHT_IRIS");
	public static final String LEFT_IRIS = bundle.getString("LEFT_IRIS");
	public static final String PHOTO = bundle.getString("PHOTO");
	public static final String TAKE_PHOTO = bundle.getString("TAKE_PHOTO");

	public static final String PLACEHOLDER_LABEL = bundle.getString("PLACEHOLDER_LABEL");
	public static final String PARENT_BIO_MSG = bundle.getString("PARENT_BIO_MSG");

	// OPT TO REGISTER
	public static final String REG_PKT_APPRVL_CNT_EXCEED = bundle.getString("REG_PKT_APPRVL_CNT_EXCEED");
	public static final String REG_PKT_APPRVL_TIME_EXCEED = bundle.getString("REG_PKT_APPRVL_TIME_EXCEED");
	public static final String OPT_TO_REG_TIME_SYNC_EXCEED = bundle.getString("OPT_TO_REG_TIME_SYNC_EXCEED");
	public static final String OPT_TO_REG_TIME_EXPORT_EXCEED = bundle.getString("OPT_TO_REG_TIME_EXPORT_EXCEED");
	public static final String OPT_TO_REG_REACH_MAX_LIMIT = bundle.getString("OPT_TO_REG_REACH_MAX_LIMIT");
	public static final String OPT_TO_REG_OUTSIDE_LOCATION = bundle.getString("OPT_TO_REG_OUTSIDE_LOCATION");
	public static final String OPT_TO_REG_WEAK_GPS = bundle.getString("OPT_TO_REG_WEAK_GPS");
	public static final String OPT_TO_REG_INSERT_GPS = bundle.getString("OPT_TO_REG_INSERT_GPS");
	public static final String OPT_TO_REG_GPS_PORT_MISMATCH = bundle.getString("OPT_TO_REG_GPS_PORT_MISMATCH");
	public static final String OPT_TO_REG_LAST_SOFTWAREUPDATE_CHECK = bundle
			.getString("OPT_TO_REG_LAST_SOFTWAREUPDATE_CHECK");

	// PACKET EXPORT
	public static final String PACKET_EXPORT_SUCCESS_MESSAGE = bundle.getString("PACKET_EXPORT_SUCCESS_MESSAGE");
	public static final String PACKET_EXPORT_MESSAGE = bundle.getString("PACKET_EXPORT_MESSAGE");
	public static final String PACKET_EXPORT_FAILURE = bundle.getString("PACKET_EXPORT_FAILURE");

	// JOBS
	public static final String EXECUTE_JOB_ERROR_MESSAGE = bundle.getString("EXECUTE_JOB_ERROR_MESSAGE");
	public static final String BATCH_JOB_START_SUCCESS_MESSAGE = bundle.getString("BATCH_JOB_START_SUCCESS_MESSAGE");
	public static final String BATCH_JOB_STOP_SUCCESS_MESSAGE = bundle.getString("BATCH_JOB_STOP_SUCCESS_MESSAGE");
	public static final String START_SCHEDULER_ERROR_MESSAGE = bundle.getString("START_SCHEDULER_ERROR_MESSAGE");
	public static final String STOP_SCHEDULER_ERROR_MESSAGE = bundle.getString("STOP_SCHEDULER_ERROR_MESSAGE");
	public static final String CURRENT_JOB_DETAILS_ERROR_MESSAGE = bundle
			.getString("CURRENT_JOB_DETAILS_ERROR_MESSAGE");
	public static final String SYNC_SUCCESS = bundle.getString("SYNC_SUCCESS");
	public static final String SYNC_FAILURE = bundle.getString("SYNC_FAILURE");

	// MACHINE MAPPING
	public static final String MACHINE_MAPPING_SUCCESS_MESSAGE = bundle.getString("MACHINE_MAPPING_SUCCESS_MESSAGE");
	public static final String MACHINE_MAPPING_ERROR_MESSAGE = bundle.getString("MACHINE_MAPPING_ERROR_MESSAGE");
	public static final String MACHINE_MAPPING_ENTITY_SUCCESS_MESSAGE = bundle
			.getString("MACHINE_MAPPING_ENTITY_SUCCESS_MESSAGE");
	public static final String MACHINE_MAPPING_ENTITY_ERROR_NO_RECORDS = bundle
			.getString("MACHINE_MAPPING_ENTITY_ERROR_NO_RECORDS");

	// SYNC
	public static final String PACKET_STATUS_SYNC_SUCCESS_MESSAGE = bundle
			.getString("PACKET_STATUS_SYNC_SUCCESS_MESSAGE");
	public static final String PACKET_STATUS_SYNC_ERROR_RESPONSE = bundle
			.getString("PACKET_STATUS_SYNC_ERROR_RESPONSE");

	// GENERIC
	public static final String UNABLE_LOAD_HOME_PAGE = bundle.getString("UNABLE_LOAD_HOME_PAGE");
	public static final String UNABLE_LOAD_LOGOUT_PAGE = bundle.getString("UNABLE_LOAD_LOGOUT_PAGE");
	public static final String UNABLE_LOAD_APPROVAL_PAGE = bundle.getString("UNABLE_LOAD_APPROVAL_PAGE");
	public static final String UNABLE_LOAD_REG_PAGE = bundle.getString("UNABLE_LOAD_REG_PAGE");
	public static final String UNABLE_LOAD_DEMOGRAPHIC_PAGE = bundle.getString("UNABLE_LOAD_DEMOGRAPHIC_PAGE");
	public static final String UNABLE_LOAD_NOTIFICATION_PAGE = bundle.getString("UNABLE_LOAD_NOTIFICATION_PAGE");
	public static final String UNABLE_LOAD_PREVIEW_PAGE = bundle.getString("UNABLE_LOAD_PREVIEW_PAGE");
	public static final String UNABLE_LOAD_ACKNOWLEDGEMENT_PAGE = bundle.getString("UNABLE_LOAD_ACKNOWLEDGEMENT_PAGE");

	// Individual Registartion - Iris Capture
	public static final String UNABLE_LOAD_IRIS_SCAN_POPUP = bundle.getString("UNABLE_LOAD_IRIS_SCAN_POPUP");
	public static final String IRIS_SUCCESS_MSG = bundle.getString("IRIS_SUCCESS_MSG");
	public static final String IRIS_NAVIGATE_NEXT_SECTION_ERROR = bundle.getString("IRIS_NAVIGATE_NEXT_SECTION_ERROR");
	public static final String IRIS_NAVIGATE_PREVIOUS_SECTION_ERROR = bundle
			.getString("IRIS_NAVIGATE_PREVIOUS_SECTION_ERROR");
	public static final String BIOMETRIC_SCANNING_ERROR = bundle.getString("BIOMETRIC_SCANNING_ERROR");
	public static final String IRIS_SCANNING_ERROR = bundle.getString("IRIS_SCANNING_ERROR");
	public static final String FINGERPRINT_SCANNING_ERROR = bundle.getString("FINGERPRINT_SCANNING_ERROR");
	public static final String NO_DEVICE_FOUND = bundle.getString("NO_DEVICE_FOUND");
public static final String FINGERPRINT_SELECTION_PANE_ALERT = bundle.getString("FINGERPRINT_SELECTION_PANE_ALERT");
	public static final String FINGERPRINT_SCAN_ALERT = bundle.getString("FINGERPRINT_SCAN_ALERT");
	public static final String IRIS_VALIDATION_ERROR = bundle.getString("IRIS_VALIDATION_ERROR");
	public static final String FINGERPRINT_DUPLICATION_ALERT = bundle.getString("FINGERPRINT_DUPLICATION_ALERT");
	public static final String FINGERPRINT_MAX_RETRIES_ALERT = bundle.getString("FINGERPRINT_MAX_RETRIES_ALERT");
	public static final String FINGERPRINT_NAVIGATE_NEXT_SECTION_ERROR = bundle
			.getString("FINGERPRINT_NAVIGATE_NEXT_SECTION_ERROR");
	public static final String FINGERPRINT_NAVIGATE_PREVIOUS_SECTION_ERROR = bundle
			.getString("FINGERPRINT_NAVIGATE_PREVIOUS_SECTION_ERROR");
	public static final String UNABLE_LOAD_FINGERPRINT_SCAN_POPUP = bundle
			.getString("UNABLE_LOAD_FINGERPRINT_SCAN_POPUP");
	public static final String IRIS_SCAN_RETRIES_EXCEEDED = bundle.getString("IRIS_SCAN_RETRIES_EXCEEDED");
	public static final String IRIS_QUALITY_SCORE_ERROR = bundle.getString("IRIS_QUALITY_SCORE_ERROR");
	public static final String IRIS_SCAN = bundle.getString("IRIS_SCAN");

	// UIN update
	public static final String UPDATE_UIN_ENTER_UIN_ALERT = bundle.getString("UPDATE_UIN_ENTER_UIN_ALERT");
	public static final String UPDATE_UIN_VALIDATION_ALERT = bundle.getString("UPDATE_UIN_VALIDATION_ALERT");
	public static final String UPDATE_UIN_SELECTION_ALERT = bundle.getString("UPDATE_UIN_SELECTION_ALERT");
	public static final String UPDATE_UIN_INDIVIDUAL_AND_PARENT_SAME_UIN_ALERT = bundle
			.getString("UPDATE_UIN_INDIVIDUAL_AND_PARENT_SAME_UIN_ALERT");
	public static final String UPDATE_UIN_NO_BIOMETRIC_CONFIG_ALERT = bundle
			.getString("UPDATE_UIN_NO_BIOMETRIC_CONFIG_ALERT");

	// Biometric Exception
	public static final String BIOMETRIC_EXCEPTION_ALERT = bundle.getString("BIOMETRIC_EXCEPTION_ALERT");

	// User Onboard
	public static final String UNABLE_LOAD_USERONBOARD_SCREEN = bundle.getString("UNABLE_LOAD_USERONBOARD_SCREEN");
	public static final String USER_ONBOARD_SUCCESS = bundle.getString("USER_ONBOARD_SUCCESS");
	public static final String USER_MACHINE_VALIDATION_MSG = bundle.getString("USER_MACHINE_VALIDATION_MSG");
	public static final String USER_ONBOARD_HI = bundle.getString("USER_ONBOARD_HI");
	public static final String USER_ONBOARD_NOTONBOARDED = bundle.getString("USER_ONBOARD_NOTONBOARDED");
	public static final String USER_ONBOARD_ERROR = bundle.getString("USER_ONBOARD_ERROR");

	// Supervisor Authentication configuration from global_param
	public static final String SUPERVISOR_AUTHENTICATION_CONFIGURATION = "mosip.registration.supervisor_authentication_configuration";

	// Registration Approval - EOD Process
	public static final String ERROR_IN_SYNC_AND_UPLOAD = bundle.getString("ERROR_IN_SYNC_AND_UPLOAD");
	public static final String UNABLE_TO_SYNC_AND_UPLOAD = bundle.getString("UNABLE_TO_SYNC_AND_UPLOAD");
	public static final String NETWORK_ERROR = bundle.getString("NETWORK_ERROR");
	public static final String SUPERVISOR_VERIFICATION = bundle.getString("SUPERVISOR_VERIFICATION");
	public static final String EOD_DETAILS_EXPORT_FAILURE = bundle.getString("EOD_DETAILS_EXPORT_FAILURE");
	public static final String EOD_DETAILS_EXPORT_SUCCESS = bundle.getString("EOD_DETAILS_EXPORT_SUCCESS");
	public static final String EOD_SLNO_LABEL = bundle.getString("EOD_SLNO_LABEL");
	public static final String EOD_REGISTRATIONID_LABEL = bundle.getString("EOD_REGISTRATIONID_LABEL");
	public static final String EOD_REGISTRATIONDATE_LABEL = bundle.getString("EOD_REGISTRATIONDATE_LABEL");
	public static final String EOD_STATUS_LABEL = bundle.getString("EOD_STATUS_LABEL");

	// Virus Scan
	public static final String VIRUS_SCAN_ERROR_FIRST_PART = bundle.getString("VIRUS_SCAN_ERROR_FIRST_PART");
	public static final String VIRUS_SCAN_ERROR_SECOND_PART = bundle.getString("VIRUS_SCAN_ERROR_SECOND_PART");
	public static final String VIRUS_SCAN_SUCCESS = bundle.getString("VIRUS_SCAN_SUCCESS");

	public static final String INVALID_KEY = bundle.getString("INVALID_KEY");

	public static final String RESTART_APPLICATION = bundle.getString("RESTART_APPLICATION");

	public static final String PRE_REG_TO_GET_ID_ERROR = bundle.getString("PRE_REG_TO_GET_ID_ERROR");
	public static final String PRE_REG_TO_GET_PACKET_ERROR = bundle.getString("PRE_REG_TO_GET_PACKET_ERROR");
	public static final String PRE_REG_PACKET_NETWORK_ERROR = bundle.getString("PRE_REG_PACKET_NETWORK_ERROR");
	public static final String PRE_REG_SUCCESS_MESSAGE = bundle.getString("PRE_REG_SUCCESS_MESSAGE");

	// PRE-REG DELETE JOB
	public static final String PRE_REG_DELETE_SUCCESS = bundle.getString("PRE_REG_DELETE_SUCCESS");
	public static final String PRE_REG_DELETE_FAILURE = bundle.getString("PRE_REG_DELETE_FAILURE");

	// PRE-REG DELETE JOB
	public static final String SYNC_CONFIG_DATA_FAILURE = bundle.getString("SYNC_CONFIG_DATA_FAILURE");

	// Packet Upload
	public static final String PACKET_UPLOAD_EMPTY_ERROR = bundle.getString("PACKET_UPLOAD_EMPTY_ERROR");
	public static final String PACKET_UPLOAD_DUPLICATE = bundle.getString("PACKET_UPLOAD_DUPLICATE");
	public static final String PACKET_NOT_AVAILABLE = bundle.getString("PACKET_NOT_AVAILABLE");
	public static final String PACKET_UPLOAD_SERVICE_ERROR = bundle.getString("PACKET_UPLOAD_SERVICE_ERROR");
	public static final String PACKET_UPLOAD_EMPTY = bundle.getString("PACKET_UPLOAD_EMPTY");
	public static final String PACKET_UPLOAD_ERROR = bundle.getString("PACKET_UPLOAD_ERROR");
	public static final String PACKET_PARTIAL_UPLOAD_ERROR = bundle.getString("PACKET_PARTIAL_UPLOAD_ERROR");
	public static final String PACKET_UPLOAD_HEADER_NAME = bundle.getString("PACKET_UPLOAD_HEADER_NAME");
	public static final String UPLOAD_COLUMN_HEADER_FILE = bundle.getString("UPLOAD_COLUMN_HEADER_FILE");
	public static final String UPLOAD_COLUMN_HEADER_STATUS = bundle.getString("UPLOAD_COLUMN_HEADER_STATUS");
	public static final String PACKET_UPLOAD_SUCCESS = bundle.getString("PACKET_UPLOAD_SUCCESS");
	public static final String UPLOAD_FAILED = bundle.getString("UPLOAD_FAILED");

	// Scheduler
	public static final String TIMEOUT_TITLE = bundle.getString("TIMEOUT_TITLE");
	public static final String TIMEOUT_INITIAL = bundle.getString("TIMEOUT_INITIAL");
	public static final String TIMEOUT_MIDDLE = bundle.getString("TIMEOUT_MIDDLE");
	public static final String TIMEOUT_END = bundle.getString("TIMEOUT_END");

	// Notification
	public static final String EMAIL_ERROR_MSG = bundle.getString("EMAIL_ERROR_MSG");
	public static final String SMS_ERROR_MSG = bundle.getString("SMS_ERROR_MSG");

	// Application Updates
	public static final String NO_UPDATES_FOUND = bundle.getString("NO_UPDATES_FOUND");
	public static final String UNABLE_FIND_UPDATES = bundle.getString("UNABLE_FIND_UPDATES");
	public static final String NO_INTERNET_CONNECTION = bundle.getString("NO_INTERNET_CONNECTION");
	public static final String UPDATE_AVAILABLE = bundle.getString("UPDATE_AVAILABLE");
	public static final String CONFIRM_UPDATE = bundle.getString("CONFIRM_UPDATE");
	public static final String UPDATE_COMPLETED = bundle.getString("UPDATE_COMPLETED");
	public static final String UNABLE_TO_UPDATE = bundle.getString("UNABLE_TO_UPDATE");
	public static final String UPDATE_LATER = bundle.getString("UPDATE_LATER");
	public static final String UPDATE_FREEZE_TIME_EXCEED = bundle.getString("UPDATE_FREEZE_TIME_EXCEED");
	public static final String SQL_EXECUTION_FAILED_AND_REPLACED = bundle
			.getString("SQL_EXECUTION_FAILED_AND_REPLACED");

	// AUTH TOKEN
	public static String UNABLE_TO_GET_AUTH_TOKEN = bundle.getString("UNABLE_TO_GET_AUTH_TOKEN");
}
