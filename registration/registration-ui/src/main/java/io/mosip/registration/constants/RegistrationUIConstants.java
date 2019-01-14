package io.mosip.registration.constants;

import java.util.ResourceBundle;

import io.mosip.registration.context.ApplicationContext;

public class RegistrationUIConstants {

	// Key values to read value from messages.properties file

	public static final ResourceBundle bundle = ApplicationContext.getInstance().getApplicationMessagesBundle();

	// LOGIN
	public static final String UNABLE_LOAD_LOGIN_SCREEN = bundle.getString("UNABLE_LOAD_LOGIN_SCREEN");
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

	// AUTHORIZATION
	public static final String ROLES_EMPTY_ERROR = bundle.getString("ROLES_EMPTY_ERROR");
	public static final String MACHINE_MAPPING_ERROR = bundle.getString("MACHINE_MAPPING_ERROR");
	public static final String AUTHORIZATION_ERROR = bundle.getString("AUTHORIZATION_ERROR");

	// DEVICE
	public static final String DEVICE_FP_NOT_FOUND = bundle.getString("DEVICE_FP_NOT_FOUND");
	public static final String FP_DEVICE_TIMEOUT = bundle.getString("FP_DEVICE_TIMEOUT");
	public static final String FP_DEVICE_ERROR = bundle.getString("FP_DEVICE_ERROR");
	public static final String FP_CAPTURE_SUCCESS = bundle.getString("FP_CAPTURE_SUCCESS");
	public static final String WEBCAM_ALERT_CONTEXT = bundle.getString("WEBCAM_ALERT_CONTEXT");
	public static final String DEVICE_ONBOARD_NOTIFICATION = bundle.getString("DEVICE_ONBOARD_NOTIFICATION");

	// LOCK ACCOUNT
	public static final String USER_ACCOUNT_LOCK_MESSAGE_NUMBER = bundle.getString("USER_ACCOUNT_LOCK_MESSAGE_NUMBER");
	public static final String USER_ACCOUNT_LOCK_MESSAGE = bundle.getString("USER_ACCOUNT_LOCK_MESSAGE");
	public static final String USER_ACCOUNT_LOCK_MESSAGE_MINUTES = bundle
			.getString("USER_ACCOUNT_LOCK_MESSAGE_MINUTES");

	// NOTIFICATIONS
	public static final String NOTIFICATION_FAIL = bundle.getString("NOTIFICATION_FAIL");
	public static final String NOTIFICATION_SMS_FAIL = bundle.getString("NOTIFICATION_SMS_FAIL");
	public static final String NOTIFICATION_EMAIL_FAIL = bundle.getString("NOTIFICATION_EMAIL_FAIL");

	// SUCCESS
	public static final String PACKET_CREATED_SUCCESS = bundle.getString("PACKET_CREATED_SUCCESS");
	public static final String REREGISTRATION_APPROVE_SUCCESS = bundle.getString("REREGISTRATION_APPROVE_SUCCESS");
	public static final String REREGISTER_TITLEPANE=bundle.getString("REREGISTER_TITLEPANE");
	public static final String PENDING_APPROVAL=bundle.getString("REREGISTER_TITLEPANE");


	// DEVICE MAPPING

	public static final String DEVICE_ONBOARD_ERROR_MSG = bundle.getString("DEVICE_ONBOARD_ERROR_MSG");
	public static final String DEVICE_MAPPING_SUCCESS_MESSAGE = bundle.getString("DEVICE_MAPPING_SUCCESS_MESSAGE");
	public static final String DEVICE_MAPPING_ERROR_MESSAGE = bundle.getString("DEVICE_MAPPING_ERROR_MESSAGE");

	// AUTHENTICATION
	public static final String AUTHENTICATION_FAILURE = bundle.getString("AUTHENTICATION_FAILURE");
	public static final String AUTH_APPROVAL_SUCCESS_MSG = bundle.getString("AUTH_APPROVAL_SUCCESS_MSG");
	public static final String AUTH_PENDING_ACTION_SUCCESS_MSG = bundle.getString("AUTH_PENDING_ACTION_SUCCESS_MSG");
	public static final String AUTHENTICATION_ERROR_MSG = bundle.getString("AUTHENTICATION_ERROR_MSG");

	// CAMERA
	public static final String APPLICANT_IMAGE_ERROR = bundle.getString("APPLICANT_IMAGE_ERROR");
	public static final String DEMOGRAPHIC_DETAILS_ERROR_CONTEXT = bundle
			.getString("DEMOGRAPHIC_DETAILS_ERROR_CONTEXT");

	// REGISTRATION
	public static final String MAX_AGE_WARNING = bundle.getString("MAX_AGE_WARNING");
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
	public static final String SCAN_DOCUMENT_CONNECTION_ERR = bundle.getString("SCAN_DOCUMENT_CONNECTION_ERR");
	public static final String SCAN_DOCUMENT_EMPTY = bundle.getString("SCAN_DOCUMENT_EMPTY");
	public static final String SCAN_DOCUMENT_CONVERTION_ERR = bundle.getString("SCAN_DOCUMENT_CONVERTION_ERR");
	public static final String PRE_REG_ID_EMPTY = bundle.getString("PRE_REG_ID_EMPTY");
	public static final String PRE_REG_ID_NOT_VALID = bundle.getString("PRE_REG_ID_NOT_VALID");

	// OPT TO REGISTER
	public static final String OPT_TO_REG_TIME_SYNC_EXCEED = bundle.getString("OPT_TO_REG_TIME_SYNC_EXCEED");
	public static final String OPT_TO_REG_TIME_EXPORT_EXCEED = bundle.getString("OPT_TO_REG_TIME_EXPORT_EXCEED");
	public static final String OPT_TO_REG_REACH_MAX_LIMIT = bundle.getString("OPT_TO_REG_REACH_MAX_LIMIT");
	public static final String OPT_TO_REG_OUTSIDE_LOCATION = bundle.getString("OPT_TO_REG_OUTSIDE_LOCATION");
	public static final String OPT_TO_REG_WEAK_GPS = bundle.getString("OPT_TO_REG_WEAK_GPS");
	public static final String OPT_TO_REG_INSERT_GPS = bundle.getString("OPT_TO_REG_INSERT_GPS");
	public static final String OPT_TO_REG_GPS_PORT_MISMATCH = bundle.getString("OPT_TO_REG_GPS_PORT_MISMATCH");

	// JOBS
	public static final String EXECUTE_JOB_ERROR_MESSAGE = bundle.getString("EXECUTE_JOB_ERROR_MESSAGE");
	public static final String BATCH_JOB_START_SUCCESS_MESSAGE = bundle.getString("BATCH_JOB_START_SUCCESS_MESSAGE");
	public static final String BATCH_JOB_STOP_SUCCESS_MESSAGE = bundle.getString("BATCH_JOB_STOP_SUCCESS_MESSAGE");
	public static final String START_SCHEDULER_ERROR_MESSAGE = bundle.getString("START_SCHEDULER_ERROR_MESSAGE");
	public static final String STOP_SCHEDULER_ERROR_MESSAGE = bundle.getString("STOP_SCHEDULER_ERROR_MESSAGE");
	public static final String CURRENT_JOB_DETAILS_ERROR_MESSAGE = bundle
			.getString("CURRENT_JOB_DETAILS_ERROR_MESSAGE");

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

	// Individual Registartion - Iris Capture
	public static final String UNABLE_LOAD_IRIS_SCAN_POPUP = bundle.getString("UNABLE_LOAD_IRIS_SCAN_POPUP");
	public static final String IRIS_NAVIGATE_NEXT_SECTION_ERROR = bundle.getString("IRIS_NAVIGATE_NEXT_SECTION_ERROR");
	public static final String IRIS_NAVIGATE_PREVIOUS_SECTION_ERROR = bundle
			.getString("IRIS_NAVIGATE_PREVIOUS_SECTION_ERROR");
	public static final String BIOMETRIC_SCANNING_ERROR = bundle.getString("BIOMETRIC_SCANNING_ERROR");
	public static final String IRIS_SCANNING_ERROR = bundle.getString("IRIS_SCANNING_ERROR");
	public static final String FINGERPRINT_SCANNING_ERROR = bundle.getString("FINGERPRINT_SCANNING_ERROR");
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
	
	// Biometric Exception
	public static final String BIOMETRIC_EXCEPTION_ALERT = bundle.getString("BIOMETRIC_EXCEPTION_ALERT");

	// User Onboard
	public static final String UNABLE_LOAD_USERONBOARD_SCREEN = bundle.getString("UNABLE_LOAD_USERONBOARD_SCREEN");
}
