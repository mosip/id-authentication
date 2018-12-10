package io.mosip.registration.constants;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import io.mosip.registration.context.ApplicationContext;

/**
 * Class contains the constants used in Registration application
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RegistrationConstants {

	/**
	 * private constructor
	 */
	private RegistrationConstants() {

	}

	/*********** UI Constants **********/
	// paths of FXML pages to be loaded
	public static final String ERROR_PAGE = "/fxml/ErrorPage.fxml";
	public static final String INITIAL_PAGE = "/fxml/RegistrationLogin.fxml";
	public static final String HOME_PAGE = "/fxml/RegistrationOfficerLayout.fxml";
	public static final String HEADER_PAGE = "/fxml/Header.fxml";
	public static final String UPDATE_PAGE = "/fxml/UpdateLayout.fxml";
	public static final String OFFICER_PACKET_PAGE = "/fxml/RegistrationOfficerPacketLayout.fxml";
	public static final String CREATE_PACKET_PAGE = "/fxml/Registration.fxml";
	public static final String ACK_RECEIPT_PATH = "/fxml/AckReceipt.fxml";
	public static final String APPROVAL_PAGE = "/fxml/RegistrationApproval.fxml";
	public static final String FTP_UPLOAD_PAGE = "/fxml/PacketUpload.fxml";
	public static final String USER_MACHINE_MAPPING = "/fxml/UserClientMachineMapping.fxml";
	public static final String SYNC_STATUS = "/fxml/RegPacketStatus.fxml";
	public static final String ONHOLD_PAGE = "/fxml/OnholdComment.fxml";
	public static final String REJECTION_PAGE = "/fxml/RejectionComment.fxml";
	public static final String DEVICE_ONBOARDING_PAGE = "/fxml/DeviceMachineMapping.fxml";
	public static final String USER_AUTHENTICATION = "/fxml/Authentication.fxml";
	public static final String DEMOGRAPHIC_PREVIEW = "/fxml/DemographicPreview.fxml";
	public static final String BIOMETRIC_PREVIEW = "/fxml/BiometricPreview.fxml";
	public static final String WEB_CAMERA_PAGE = "/fxml/WebCamera.fxml";
	public static final String PENDING_ACTION_PAGE = "/fxml/RegistrationPendingAction.fxml";
	public static final String PENDING_APPROVAL_PAGE = "/fxml/RegistrationPendingApproval.fxml";
	public static final String REREGISTRATION_PAGE = "/fxml/ReRegistration.fxml";
	public static final String SCAN_PAGE = "/fxml/Scan.fxml";

	// CSS file
	public static final String CSS_FILE_PATH = "application.css";
	public static final String CLOSE_IMAGE_PATH = "/images/close.jpg";
	public static final String DOC_STUB_PATH = "/images/PANStubbed.jpg";

	// Key values to read value from messages.properties file

	public static final ResourceBundle bundle = ApplicationContext.getInstance().getApplicationMessagesBundle();

	// LOGIN
	public static final String UNABLE_LOAD_LOGIN_SCREEN = bundle.getString("UNABLE_LOAD_LOGIN_SCREEN");
	public static final String MISSING_MANDATOTY_FIELDS = bundle.getString("MISSING_MANDATOTY_FIELDS");
	public static final String CREDENTIALS_FIELD_EMPTY = bundle.getString("CREDENTIALS_FIELD_EMPTY");
	public static final String USERNAME_FIELD_EMPTY = bundle.getString("USERNAME_FIELD_EMPTY");
	public static final String PWORD_FIELD_EMPTY = bundle.getString("PWORD_FIELD_EMPTY");
	public static final String USRNAME_PWORD_LENGTH = bundle.getString("USRNAME_PWORD_LENGTH");
	public static final String USER_NOT_ONBOARDED = bundle.getString("USER_NOT_ONBOARDED");
	public static final String INCORRECT_PWORD = bundle.getString("INCORRECT_PWORD");
	public static final String BLOCKED_USER_ERROR = bundle.getString("BLOCKED_USER_ERROR");
	public static final String USERNAME_FIELD_ERROR = bundle.getString("USERNAME_FIELD_ERROR");
	public static final String OTP_FIELD_EMPTY = bundle.getString("OTP_FIELD_EMPTY");
	public static final String OTP_GENERATION_SUCCESS_MESSAGE = bundle.getString("OTP_GENERATION_SUCCESS_MESSAGE");
	public static final String OTP_GENERATION_ERROR_MESSAGE = bundle.getString("OTP_GENERATION_ERROR_MESSAGE");
	public static final String OTP_VALIDATION_SUCCESS_MESSAGE = bundle.getString("OTP_VALIDATION_SUCCESS_MESSAGE");
	public static final String OTP_VALIDATION_ERROR_MESSAGE = bundle.getString("OTP_VALIDATION_ERROR_MESSAGE");
	public static final String FINGER_PRINT_MATCH = bundle.getString("FINGER_PRINT_MATCH");

	// Authentication
	public static final String SUPERVISOR_VERIFICATION = "Supervisor Verification";
	public static final String SUPERVISOR_NAME = "Supervisor";
	public static final String SUPERVISOR_FINGERPRINT_LOGIN = "Supervisior Fingerprint Authentication";

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

	// DEVICE MAPPING

	public static final String DEVICE_ONBOARD_ERROR_MSG = bundle.getString("DEVICE_ONBOARD_ERROR_MSG");
	public static final String DEVICE_MAPPING_SUCCESS_MESSAGE = bundle.getString("DEVICE_MAPPING_SUCCESS_MESSAGE");
	public static final String DEVICE_MAPPING_ERROR_MESSAGE = bundle.getString("DEVICE_MAPPING_ERROR_MESSAGE");

	// AUTHENTICATION
	public static final String AUTHENTICATION_FAILURE = bundle.getString("AUTHENTICATION_FAILURE");
	public static final String AUTH_APPROVAL_SUCCESS_MSG = bundle.getString("AUTH_APPROVAL_SUCCESS_MSG");
	public static final String AUTH_PENDING_ACTION_SUCCESS_MSG = bundle.getString("AUTH_PENDING_ACTION_SUCCESS_MSG");

	// CAMERA
	public static final String APPLICANT_IMAGE_ERROR = bundle.getString("APPLICANT_IMAGE_ERROR");
	public static final String DEMOGRAPHIC_DETAILS_ERROR_CONTEXT = bundle
			.getString("DEMOGRAPHIC_DETAILS_ERROR_CONTEXT");

	// REGISTRATION
	public static final String ONLY_ALPHABETS = bundle.getString("ONLY_ALPHABETS");
	public static final String FULL_NAME_EMPTY = bundle.getString("FULL_NAME_EMPTY");
	public static final String MIDDLE_NAME_EMPTY = bundle.getString("MIDDLE_NAME_EMPTY");
	public static final String LAST_NAME_EMPTY = bundle.getString("LAST_NAME_EMPTY");
	public static final String AGE_EMPTY = bundle.getString("AGE_EMPTY");
	public static final String AGE_WARNING = bundle.getString("AGE_WARNING");
	public static final String DATE_OF_BIRTH_EMPTY = bundle.getString("DATE_OF_BIRTH_EMPTY");
	public static final String GENDER_EMPTY = bundle.getString("GENDER_EMPTY");
	public static final String ADDRESS_LINE_1_EMPTY = bundle.getString("ADDRESS_LINE_1_EMPTY");
	public static final String ADDRESS_LINE_2_EMPTY = bundle.getString("ADDRESS_LINE_2_EMPTY");
	public static final String COUNTRY_EMPTY = bundle.getString("COUNTRY_EMPTY");
	public static final String CITY_EMPTY = bundle.getString("CITY_EMPTY");
	public static final String PROVINCE_EMPTY = bundle.getString("PROVINCE_EMPTY");
	public static final String REGION_EMPTY = bundle.getString("REGION_EMPTY");
	public static final String POSTAL_CODE_EMPTY = bundle.getString("POSTAL_CODE_EMPTY");
	public static final String MOBILE_NUMBER_EMPTY = bundle.getString("MOBILE_NUMBER_EMPTY");
	public static final String MOBILE_NUMBER_EXAMPLE = bundle.getString("MOBILE_NUMBER_EXAMPLE");
	public static final String EMAIL_ID_EMPTY = bundle.getString("EMAIL_ID_EMPTY");
	public static final String EMAIL_ID_EXAMPLE = bundle.getString("EMAIL_ID_EXAMPLE");
	public static final String PARENT_NAME_EMPTY = bundle.getString("PARENT_NAME_EMPTY");
	public static final String UIN_ID_EMPTY = bundle.getString("UIN_ID_EMPTY");
	public static final String UIN_ID_WARNING = bundle.getString("UIN_ID_WARNING");
	public static final String ADDRESS_LINE_WARNING = bundle.getString("ADDRESS_LINE_WARNING");
	public static final String LOCAL_ADMIN_AUTHORITY_EMPTY = bundle.getString("LOCAL_ADMIN_AUTHORITY_EMPTY");
	public static final String CNIE_OR_PIN_NUMBER_EMPTY = bundle.getString("CNIE_OR_PIN_NUMBER_EMPTY");
	public static final String CNIE_OR_PIN_NUMBER_WARNING = bundle.getString("CNIE_OR_PIN_NUMBER_WARNING");
	public static final String POA_DOCUMENT_EMPTY = bundle.getString("POA_DOCUMENT_EMPTY");
	public static final String POI_DOCUMENT_EMPTY = bundle.getString("POI_DOCUMENT_EMPTY");
	public static final String POR_DOCUMENT_EMPTY = bundle.getString("POR_DOCUMENT_EMPTY");
	public static final String DOB_DOCUMENT_EMPTY = bundle.getString("DOB_DOCUMENT_EMPTY");
	public static final String TEN_LETTER_INPUT_LIMT = bundle.getString("TEN_LETTER_INPUT_LIMT");
	public static final String SIX_DIGIT_INPUT_LIMT = bundle.getString("SIX_DIGIT_INPUT_LIMT");
	public static final String THIRTY_DIGIT_INPUT_LIMT = bundle.getString("THIRTY_DIGIT_INPUT_LIMT");
	public static final String SCAN_DOCUMENT_ERROR = bundle.getString("SCAN_DOCUMENT_ERROR");
	public static final String UNABLE_LOAD_SCAN_POPUP = bundle.getString("UNABLE_LOAD_SCAN_POPUP");
	public static final String SCAN_DOC_TITLE = bundle.getString("SCAN_DOC_TITLE");
	public static final String SCAN_DOC_CATEGORY_MULTIPLE = bundle.getString("SCAN_DOC_CATEGORY_MULTIPLE");
	public static final String SCAN_DOC_SUCCESS = bundle.getString("SCAN_DOC_SUCCESS");
	public static final String SCAN_DOC_SIZE = bundle.getString("SCAN_DOC_SIZE");
	public static final String PRE_REG_ID_EMPTY = bundle.getString("PRE_REG_ID_EMPTY");

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
	public static final String FINGERPRINT_NAVIGATE_NEXT_SECTION_ERROR = bundle
			.getString("FINGERPRINT_NAVIGATE_NEXT_SECTION_ERROR");
	public static final String FINGERPRINT_NAVIGATE_PREVIOUS_SECTION_ERROR = bundle
			.getString("FINGERPRINT_NAVIGATE_PREVIOUS_SECTION_ERROR");
	public static final String UNABLE_LOAD_FINGERPRINT_SCAN_POPUP = bundle
			.getString("UNABLE_LOAD_FINGERPRINT_SCAN_POPUP");
	public static final String IRIS_SCAN_RETRIES_EXCEEDED = bundle.getString("IRIS_SCAN_RETRIES_EXCEEDED");

	// Login
	public static final String LOGIN_METHOD_PWORD = "PWD";
	public static final String LOGIN_METHOD_OTP = "OTP";
	public static final String LOGIN_METHOD_BIO = "BIO";
	public static final String BLOCKED = "BLOCKED";
	public static final String LOGIN_INITIAL_SCREEN = "initialMode";
	public static final String LOGIN_SEQUENCE = "sequence";
	public static final String BLACKLISTED = "BLOCKED";
	public static final String REGISTRATION_LOGIN_PWORD_LOGIN_CONTROLLER = "REGISTRATION - LOGIN_PWORD - LOGIN_CONTROLLER";
	public static final String REGISTRATION_LOGIN_MODE_LOGIN_CONTROLLER = "REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER";

	// FingerPrint
	public static final String FINGER_PRINT_CAPTURE = "Please place your finger on device.";
	public static final String FP_DEVICE = "Mantra";
	public static final String FINGER_TYPE_MINUTIA = "minutia";

	// Authorization Info
	public static final String ADMIN_ROLE = "SUPERADMIN";
	public static final String ROLES_EMPTY = "RolesEmpty";
	public static final String MACHINE_MAPPING = "MachineMapping";

	// Generic
	public static final String ERROR = "ERROR";
	public static final String SUCCESS_MSG = "Success";
	public static final int PARAM_ONE = 1;
	public static final int PARAM_ZERO = 0;

	// UI Registration Validations
	public static final String ADDRESS_KEY = "PrevAddress";
	public static final String REGISTRATION_CONTROLLER = "REGISTRATION_CONTROLLER";
	public static final String REGISTRATION_DATA = "registrationDTOContent";
	public static final String REGISTRATION_AGE_DATA = "ageDatePickerContent";
	public static final String REGISTRATION_PANE1_DATA = "demoGraphicPane1Content";
	public static final String REGISTRATION_PANE2_DATA = "demoGraphicPane2Content";
	public static final String REGISTRATION_ISEDIT = "isEditPage";

	public static final String MACHINE_MAPPING_ACTIVE = "ACTIVE";
	public static final String MACHINE_MAPPING_IN_ACTIVE = "IN-ACTIVE";
	public static final String TOGGLE_BIO_METRIC_EXCEPTION = "toggleBiometricException";

	// onBoard User
	public static final String ONBOARD_BIOMETRICS = "Biometrics - ";

	// RegistrationApproval
	public static final String PLACEHOLDER_LABEL = "No Packets for approval";
	public static final String REJECTION_BEAN_NAME = "rejectionController";
	public static final String ONHOLD_BEAN_NAME = "onHoldController";
	public static final String REGISTRATIONID = "registrationID";
	public static final String STATUSCODE = "statusCode";
	public static final String STATUSCOMMENT = "statusComment";
	public static final String ONHOLD_COMMENTS = "ONHOLD_COMMENTS";
	public static final String REJECTION_COMMENTS = "REJECT_COMMENTS";
	public static final String EMPTY = "";
	public static final String CONSTANTS_FILE_NAME = "/constants.properties";

	// AES Encryption Constants
	public static final String AES_KEY_MANAGER_ALG = "aes.algorithm";
	public static final String AES_KEY_SEED_LENGTH = "aes.keyLengthInBytes";
	public static final String AES_SESSION_KEY_LENGTH = "aes.keyLengthInBits";
	public static final String AES_KEY_CIPHER_SPLITTER = "aes.keySplitter";

	// Packet Store Location Constants
	public static final String PACKET_STORE_LOCATION = "packet.storageLocation";
	public static final String PACKET_STORE_DATE_FORMAT = "packet.location.dateFormat";

	// Packet Creation Constants
	public static final String ZIP_FILE_EXTENSION = ".zip";
	public static final String DEMOGRPAHIC_JSON_NAME = "demographicJson";
	public static final String PACKET_META_JSON_NAME = "packetMetaJson";
	public static final String ENROLLMENT_META_JSON_NAME = "enrollmentMetaJson";
	public static final String HASHING_JSON_NAME = "hash";
	public static final String AUDIT_JSON_FILE = "audit";
	public static final String JSON_FILE_EXTENSION = ".json";
	public static final String ACK_RECEIPT = "RegistrationAcknowledgement";

	// Validation Types
	public static final String VALIDATION_TYPE_FP = "Fingerprint";
	public static final String VALIDATION_TYPE_FP_SINGLE = "single";

	// Supervisor Authentication
	public static final String PWD_MATCH = "Username and Password Match";
	public static final String PWD_MISMATCH = "Username and Password Not Match";

	// RSA
	public static final String RSA_ALG = "RSA";
	public static final String RSA_PUBLIC_KEY_FILE = "../Key_Store/public.key";
	public static final String RSA_PRIVATE_KEY_FILE = "../Key_Store/private.key";
	public static final String LOCALHOST = "localhost";

	// Constants for Registration Creation Zip
	/**
	 * Specifies the Image type for storing the images in zip file
	 */
	public static final String IMAGE_TYPE = ".jpg";

	/**
	 * Specifies the Document type for storing the documents in zip file
	 */
	public static final String DOC_TYPE = ".jpg";

	/**
	 * Specifies the format for storing the Registration Acknowledgement
	 */
	public static final String IMAGE_FORMAT = "png";
	/**
	 * Specifies the path for storing the template in vm file
	 */
	public static final String TEMPLATE_PATH = "src/main/resources/templates/acktemplate.vm";

	// Constants for Registration ID Generator - will be removed after Kernel
	// Integration
	public static final String AGENCY_CODE = "2018";
	public static final String STATION_NUMBER = "78213";
	public static final String RID_DATE_FORMAT = "ddMMyyyyHHmmss";

	// Logger - Constants
	public static final String APPLICATION_ID = "REG";
	public static final String APPLICATION_NAME = "REGISTRATION";

	// Audit - Constants
	public static final String AUDIT_APPLICATION_ID = "audit.applicationId";
	public static final String AUDIT_APPLICATION_NAME = "audit.applicationName";

	// Default Host IP Address and Name for Audit Logs
	public static final String HOST_IP = "audit.hostIP";
	public static final String HOST_NAME = "audit.hostName";

	// OnlineConnectivity check
	public static final String URL = "http://localhost:8080/getTokenId";

	// ALert related constants
	public static final String ALERT_INFORMATION = "INFORMATION";
	public static final String ALERT_ERROR = "ERROR";
	public static final String ALERT_WARNING = "WARNING";
	public static final String ALERT = "ALERT";

	// api related constant values
	public static final String HTTPMETHOD = "service.httpmethod";
	public static final String SERVICE_URL = "service.url";
	public static final String HEADERS = "service.headers";
	public static final String RESPONSE_TYPE = "service.responseType";
	public static final String REQUEST_TYPE = "service.requestType";
	public static final String AUTH_HEADER = "service.authheader";
	public static final String AUTH_REQUIRED = "service.authrequired";
	public static final String AUTH_TYPE = "BASIC";

	// OTP Related Details
	public static final String OTP_GENERATOR_SERVICE_NAME = "otp_generator";
	public static final String USERNAME_KEY = "key";
	public static final String OTP_GENERATED = "otp";
	public static final String OTP_VALIDATOR_SERVICE_NAME = "otp_validator";
	public static final String OTP_GENERATOR_RESPONSE_DTO = "otpGeneratorResponseDTO";
	public static final String OTP_VALIDATOR_RESPONSE_DTO = "otpValidatorResponseDTO";

	/** Velocity Template Generator Constants */
	public static final String TEMPLATE_REGISTRATION_ID = "RegId";
	public static final String TEMPLATE_DATE_FORMAT = "dd/MM/yyyy";
	public static final String TEMPLATE_DATE = "Date";
	public static final String TEMPLATE_FULL_NAME = "FullName";
	public static final String TEMPLATE_DOB = "DOB";
	public static final String TEMPLATE_GENDER = "Gender";
	public static final String TEMPLATE_ADDRESS_LINE1 = "AddressLine1";
	public static final String TEMPLATE_ADDRESS_LINE2 = "AddressLine2";
	public static final String TEMPLATE_CITY = "City";
	public static final String TEMPLATE_STATE = "State";
	public static final String TEMPLATE_COUNTRY = "Country";
	public static final String TEMPLATE_MOBILE = "Mobile";
	public static final String TEMPLATE_EMAIL = "Email";
	public static final String TEMPLATE_DOCUMENTS = "Documents";
	public static final String TEMPLATE_OPERATOR_NAME = "OperatorName";
	public static final String TEMPLATE_IMAGE_ENCODING = "data:image/jpg;base64,";
	public static final String TEMPLATE_MISSING_FINGER = "&#10008;";
	public static final String TEMPLATE_BIOMETRICS_CAPTURED = "BiometricsCaptured";
	public static final String TEMPLATE_HANDS_IMAGE_PATH = "src/main/resources/images/hands.jpg";
	public static final String TEMPLATE_RESIDENT_NAME = "ResidentName";
	public static final String TEMPLATE_POSTAL_CODE = "PostalCode";
	public static final String TEMPLATE_ADDRESS_LINE3 = "AddressLine3";
	public static final String TEMPLATE_IMAGE_SOURCE = "ApplicantImageSource";
	public static final String TEMPLATE_EXCEPTION_IMAGE_SOURCE = "ExceptionImageSource";
	public static final String TEMPLATE_WITHOUT_EXCEPTION_IMAGE = "WithoutExceptionImage";
	public static final String TEMPLATE_WITH_EXCEPTION_IMAGE = "WithExceptionImage";
	public static final String TEMPLATE_STYLE_PROPERTY = "style='visibility:hidden;'";
	public static final String TEMPLATE_LOGTAG = "Acknowledgement Template";

	// Web Camera Constants
	public static final String WEB_CAMERA_IMAGE_TYPE = "jpg";
	public static final String APPLICANT_PHOTOGRAPH_NAME = "Applicant Photograph";
	public static final String EXCEPTION_PHOTOGRAPH_NAME = "Exception Photograph";
	public static final String APPLICANT_IMAGE = "Applicant Image";
	public static final String EXCEPTION_IMAGE = "Exception Image";
	public static final String WEB_CAMERA_PAGE_TITLE = "Applicant Biometrics";

	// Acknowledement Form
	public static final String ACKNOWLEDGEMENT_FORM_TITLE = "Registration Acknowledgement";

	// logos for new registration
	public static final String DEMOGRAPHIC_DETAILS_LOGO = "file:src/main/resources/images/Pre-Registration.png";
	public static final String APPLICANT_BIOMETRICS_LOGO = "file:src/main/resources/images/ApplicantBiometrics.PNG";
	public static final String OPERATOR_AUTHENTICATION_LOGO = "file:src/main/resources/images/OperatorAuthentication.PNG";

	// Exception Code for Components
	public static final String PACKET_CREATION_EXP_CODE = "PCC-";
	public static final String PACKET_UPLOAD_EXP_CODE = "PAU-";
	public static final String REG_ACK_EXP_CODE = "ACK-";
	public static final String DEVICE_ONBOARD_EXP_CODE = "DVO-";
	public static final String SYNC_JOB_EXP_CODE = "SYN-";
	public static final String USER_REG_IRIS_CAPTURE_EXP_CODE = "IRC-";
	public static final String USER_REG_FINGERPRINT_CAPTURE_EXP_CODE = "FPC-";
	public static final String USER_REGISTRATION_EXP_CODE = "REG-";
	public static final String USER_REG_SCAN_EXP_CODE = "SCN-";

	// Constants for Audits
	public static final String INTERNAL_SERVER_ERROR = "Internal error while creating packet";
	public static final String REGISTRATION_ID = "RID";

	// USER CLIENT MACHINE MAPPING
	public static final String MACHINE_MAPPING_CREATED = "created";
	public static final String MACHINE_MAPPING_UPDATED = "updated";
	public static final String MACHINE_MAPPING_LOGGER_TITLE = "REGISTRATION - USER CLIENT MACHINE MAPPING";
	public static final String DEVICE_MAPPING_LOGGER_TITLE = "REGISTRATION - CENTER MACHINE DEVICE MAPPING";

	// CENTER MACHINE DEVICE MAPPING
	public static final String DEVICE_MAPPING_SUCCESS_CODE = "REG-DVO‌-001";
	public static final String DEVICE_MAPPING_ERROR_CODE = "REG-DVO‌-002";

	// MAP ID
	public static final String USER_MACHINE_MAPID = "ListOfUserDTO";

	// ACTIVE INACTIVE USER
	public static final String USER_ACTIVE = "Active";
	public static final String USER_IN_ACTIVE = "In-Active";

	// Upload Packet

	public static List getStatus() {
		String[] packetStatus = { "SYNCED", "resend", "E" };
		return Arrays.asList(packetStatus);
	}

	// opt to register constants
	public static final String OPT_TO_REG_GEO_CAP_FREQ = "GEO_CAP_FREQ";
	public static final String OPT_TO_REG_ICS‌_001 = "REG-ICS‌-001";
	public static final String OPT_TO_REG_ICS‌_002 = "REG-ICS‌-002";
	public static final String OPT_TO_REG_ICS‌_003 = "REG-ICS‌-003";
	public static final String OPT_TO_REG_ICS‌_004 = "REG-ICS‌-004";
	public static final String OPT_TO_REG_PAK_MAX_CNT_OFFLINE_FREQ = "REG_PAK_MAX_CNT_OFFLINE_FREQ";
	public static final double OPT_TO_REG_EARTH_RADIUS = 6371000;
	public static final double OPT_TO_REG_METER_CONVERSN = 1609.00;
	public static final String OPT_TO_REG_LOGGER_SESSION_ID = "REGISTRATION - SYNC - VALIDATION";
	public static final String OPT_TO_REG_DIST_FRM_MACHN_TO_CENTER = "DIST_FRM_MACHN_TO_CENTER";
	public static final String OPT_TO_REG_GEO_FLAG_SINGLETIME = "Y";
	public static final String OPT_TO_REG_GEO_FLAG_MULTIPLETIME = "N";
	public static final String OPT_TO_REG_ICS‌_005 = "REG-ICS‌-005";
	public static final String OPT_TO_REG_ICS‌_006 = "REG-ICS‌-006";
	public static final String OPT_TO_REG_ICS‌_007 = "REG-ICS‌-007";
	public static final String OPT_TO_REG_LAST_CAPTURED_TIME = "lastCapturedTime";
	public static final String OPT_TO_REG_LATITUDE = "latitude";
	public static final String OPT_TO_REG_LONGITUDE = "longitude";
	public static final String OPT_TO_REG_MDS_J00001 = "MDS_J00001";
	public static final String OPT_TO_REG_LCS_J00002 = "LCS_J00002";
	public static final String OPT_TO_REG_PDS_J00003 = "PDS_J00003";
	public static final String OPT_TO_REG_RSS_J00004 = "RSS_J00004";
	public static final String OPT_TO_REG_RCS_J00005 = "RCS_J00005";
	public static final String OPT_TO_REG_RPS_J00006 = "RPS_J00006";
	public static final String OPT_TO_REG_URS_J00007 = "URS_J00007";
	public static final String OPT_TO_REG_POS_J00008 = "POS_J00008";
	public static final String OPT_TO_REG_LER_J00009 = "LER_J00009";

	/** Packet Status Sync Constants */
	public static final String PACKET_STATUS_SYNC_RESPONSE_ENTITY = "registrations";
	public static final String PACKET_STATUS_SYNC_SERVICE_NAME = "packet_status";
	public static final String PACKET_STATUS_SYNC_URL_PARAMETER = "registrationIds";
	public static final String PACKET_STATUS_SYNC_REGISTRATION_ID = "registrationId";
	public static final String PACKET_STATUS_SYNC_STATUS_CODE = "statusCode";
	public static final String PACKET_STATUS_CODE_PROCESSED = "processed";

	public static final String BIOMETRIC_IMAGE = "Image";
	public static final String BIOMETRIC_TYPE = "Type";
	// Packet Upload
	public static final String PACKET_TYPE = "file";
	public static final String PACKET_STATUS_PRE_SYNC = "PRE_SYNC";
	public static final String PACKET_STATUS_SYNC_TYPE = "NEW_REGISTRATION";
	public static final String ACKNOWLEDGEMENT_FILE = "_Ack";
	public static final String PACKET_SYNC_ERROR = "Error";
	public static final String RE_REGISTRATION_STATUS = "Re-Register";
	public static final String PACKET_SYNC_REF_ID = "packetsync";
	public static final String PACKET_UPLOAD_REF_ID = "packetUpload";

	// Device On-boarding
	public static final String DEVICE_MANUFACTURER_NAME = "manufacturerName";
	public static final String DEVICE_MODEL_NAME = "modelName";
	public static final String DEVICE_SERIAL_NO = "serialNo";
	public static final String ONBOARD_AVAILABLE_DEVICES = "availableDevices";
	public static final String ONBOARD_MAPPED_DEVICES = "mappedDevices";
	public static final String ONBOARD_DEVICES_MAP = "onBoardDevicesMap";
	public static final String ONBOARD_DEVICES_REF_ID_TYPE = "UserID";
	public static final String MACHINE_ID = "machineId";
	public static final String ONBOARD_DEVICES_MAP_UPDATED = "updatedDevicesMap";
	public static final String DEVICE_TYPES_ALL_OPTION = "All";
	public static final String DEVICE_TYPE = "deviceType";

	// Template Name
	public static final String ACKNOWLEDGEMENT_TEMPLATE = "Ack Template";
	public static final String NOTIFICATION_TEMPLATE = "Notification Template";

	// Notification Service
	public static final String EMAIL_SUBJECT = "MOSIP REGISTRATION NOTIFICATION";
	public static final String EMAIL_SERVICE = "email";
	public static final String SMS_SERVICE = "sms";
	public static final String NOTIFICATION_SERVICE = "REGISTRATION - NOTIFICATION SERVICE ";
	public static final String MODE_OF_COMMUNICATION = "MODE_OF_COMMUNICATION";

	// Global configuration parameters
	public static final String INVALID_LOGIN_COUNT = "INVALID_LOGIN_COUNT";
	public static final String INVALID_LOGIN_TIME = "INVALID_LOGIN_TIME";

	// Spring Batch-Jobs
	public static final String JOB_TRIGGER_STARTED = "Trigger started";
	public static final String JOB_TRIGGER_COMPLETED = "Trigger completed";
	public static final String JOB_EXECUTION_STARTED = "Execution started";
	public static final String JOB_EXECUTION_COMPLETED = "Execution completed";
	public static final String JOB_EXECUTION_SUCCESS = "Executed with success";
	public static final String JOB_EXECUTION_FAILURE = "Executed with failure";
	public static final String JOB_TRIGGER_MIS_FIRED = "Trigger Mis-Fired";
	public static final String JOB_EXECUTION_REJECTED = "Execution Rejected";
	public static final String RETRIEVED_PRE_REG_ID = "Retrieved Pre Registration";
	public static final String UNABLE_TO_RETRIEVE_PRE_REG_ID = "Unable to retrieve pre registration";

	public static final String JOB_TRIGGER_POINT_SYSTEM = "System";
	public static final String JOB_TRIGGER_POINT_USER = "User";
	public static final String JOB_SYNC_TO_SERVER = "Server";

	// GPS Device
	public static final String GPS_LOGGER = "GPS-Device-Information";
	public static final String GPS_LATITUDE = "latitude";
	public static final String GPS_LONGITUDE = "longitude";
	public static final String GPS_DISTANCE = "distance";
	public static final String GPS_CAPTURE_ERROR_MSG = "gpsErrorMessage";
	public static final String GPS_CAPTURE_SUCCESS = "gpsCaptureSuccess";
	public static final String GPS_CAPTURE_FAILURE = "gpsCaptureFailure";
	public static final String GPS_CAPTURE_FAILURE_MSG = "GPS signal is weak please capture again";
	public static final String GPS_CAPTURE_SUCCESS_MSG = "GPS signal Captured Sucessfullty";
	public static final String GPS_CAPTURE_PORT_FAILURE_MSG = "Please insert the GPS device in the Specified Port";
	public static final String GPS_DEVICE_CONNECTION_FAILURE = "Please connect the GPS Device";
	public static final String GPS_DEVICE_CONNECTION_FAILURE_ERRO_MSG = "GPS device not found. Please connect an on-boarded GPS device.";
	public static final String GPS_REG_LGE‌_002 = "REG-LGE‌-002";
	public static final String GPS_SERIAL_PORT = "COM4";
	public static final String GPS_PORT_TIMEOUT = "1000";
	public static final String GPS_DEVICE_MODEL = "BU343";
	public static final String GPS_ERROR_CODE = "errorCode";
	public static final String GPS_CAPTURING_EXCEPTION = "GPS_EXCEPTION";
	public static final String GPS_SIGNAL = "$GP";

	public static final List<String> ONBOARD_DEVICE_TYPES = Arrays.asList("Fingerprint");

	// Documents
	public static final String POA_DOCUMENT = "PoA";
	public static final String POI_DOCUMENT = "PoI";
	public static final String POR_DOCUMENT = "PoR";
	public static final String DOB_DOCUMENT = "DoB";

	public static List<String> getPoaDocumentList() {
		return Arrays.asList("Aadhar", "Passport", "VoterId", "Licence");
	}

	public static List<String> getPoiDocumentList() {
		return Arrays.asList("PAN", "Aadhar", "Passport", "VoterId", "Licence");
	}

	public static List<String> getPorDocumentList() {
		return Arrays.asList("Document1", "Document2", "Document3", "Document4");
	}

	public static List<String> getDobDocumentList() {
		return Arrays.asList("Document1", "Document2", "Document3", "Document4");
	}

	public static List<String> getPacketStatus() {
		return Arrays.asList("APPROVED", "REJECTED", "RE_REGISTER_APPROVED");
	}

	// Pre Registration
	public static final String PRE_REGISTRATION_ID = "preId";
	public static final String GET_PRE_REGISTRATION_IDS = "get_pre_registration_Ids";
	public static final String GET_PRE_REGISTRATION = "get_pre_registration";
	public static final String REGISTRATION_CLIENT_ID = "59276903416082";
	public static final String PRE_REGISTRATION_DUMMY_ID = "mosip.pre-registration.datasync";
	public static final List<String> VER = Arrays.asList("1.0", "1.1", "1.2");
	public static final String PRE_REG_TO_GET_ID_ERROR = "Unable to get Pre registartion id's";
	public static final String PRE_REG_TO_GET_PACKET_ERROR = "Unable to get Pre registartion for given id";
	public static final String PRE_REG_SUCCESS_MESSAGE = "Got Pre registartion ";

	// UI Date Format
	public static final String DEMOGRAPHIC_DOB_FORMAT = "dd-MM-yyyy";
	public static final String DEMOGRAPHIC_DOB = "01-01-";
	public static final String DATE_FORMAT = "MM/dd/yyy hh:mm:ss";
	public static final String HH_MM_SS = "HH:mm:ss";

	// Iris & Fingerprint Capture for Individual Registration
	public static final String IRIS_THRESHOLD = "iris_threshold";
	public static final String IMAGE_FORMAT_KEY = "imageFormat";
	public static final String IMAGE_BYTE_ARRAY_KEY = "imageBytes";
	public static final String IMAGE_SCORE_KEY = "imageScore";
	public static final String LEFT = "Left";
	public static final String RIGHT = "Right";
	public static final String EYE = "Eye";
	public static final String DOT = ".";
	public static final String FINGERPRINT = "Fingerprint";
	public static final String LEFTPALM = "leftSlap";
	public static final String RIGHTPALM = "rightSlap";
	public static final String THUMBS = "thumbs";
	public static final String PERCENTAGE = "%";
	public static final String ISO_FILE_NAME = "ISOTemplate";
	public static final String ISO_IMAGE_FILE_NAME = "ISOImage";
	public static final String ISO_FILE = "ISOTemplate.iso";
	public static final String ISO_IMAGE_FILE = "ISOImage.iso";
	public static final String LEFTHAND_SEGMENTED_FINGERPRINT_PATH = "src/main/resources/FINGER PRINTS/LEFT HAND";
	public static final String LEFTHAND_SLAP_FINGERPRINT_PATH = "/FINGER PRINTS/leftSlap.jpg";
	public static final String RIGHTHAND_SEGMENTED_FINGERPRINT_PATH = "src/main/resources/FINGER PRINTS/RIGHT HAND";
	public static final String RIGHTHAND_SLAP_FINGERPRINT_PATH = "/FINGER PRINTS/rightSlap.jpg";
	public static final String THUMB_SEGMENTED_FINGERPRINT_PATH = "src/main/resources/FINGER PRINTS/THUMB";
	public static final String BOTH_THUMBS_FINGERPRINT_PATH = "/FINGER PRINTS/thumbs.jpg";
	public static final String IRIS_RETRY_COUNT = "num_of_iris_retries";

	/** Exception codes **/
	private static final String REG_SERVICE_CODE = "REG-SER-";

	public static final String REG_FRAMEWORK_PACKET_HANDLING_EXCEPTION = REG_SERVICE_CODE + "PHA-201";
	public static final String PACKET_CREATION_EXCEPTION = REG_SERVICE_CODE + "PHA-202";
	public static final String PACKET_ZIP_CREATION = REG_SERVICE_CODE + "ZCM-203";
	public static final String ENROLLMENT_ZIP_CREATION = REG_SERVICE_CODE + "ZCM-204";
	public static final String PACKET_ENCRYPTION_MANAGER = REG_SERVICE_CODE + "PEM-205";
	public static final String AES_ENCRYPTION_MANAGER = REG_SERVICE_CODE + "AEM-206";
	public static final String AES_SEED_GENERATION = REG_SERVICE_CODE + "ASG-207";
	public static final String AES_KEY_MANAGER = REG_SERVICE_CODE + "EKM-208";
	public static final String AES_ENCRYPTION = REG_SERVICE_CODE + "AEI-209";
	public static final String CONCAT_ENCRYPTED_DATA = REG_SERVICE_CODE + "AEI-210";
	public static final String ENCRYPTED_PACKET_STORAGE = REG_SERVICE_CODE + "STM-211";
	public static final String PACKET_INSERTION = REG_SERVICE_CODE + "IPD-212";
	public static final String CREATE_PACKET_ENTITY = REG_SERVICE_CODE + "IPD-213";
	public static final String LOGIN_SERVICE = REG_SERVICE_CODE + "IPD-214";
	public static final String SERVICE_DELEGATE_UTIL = REG_SERVICE_CODE + "IPD-215";
	public static final String SERVICE_DATA_PROVIDER_UTIL = REG_SERVICE_CODE + "DPU-216";
	public static final String RSA_ENCRYPTION_MANAGER = REG_SERVICE_CODE + "REM-219";
	public static final String UPDATE_SYNC_AUDIT = REG_SERVICE_CODE + "ADI-220";
	public static final String FETCH_UNSYNC_AUDIT = REG_SERVICE_CODE + "ADI-221";
	public static final String READ_PROPERTY_FILE_ERROR = REG_SERVICE_CODE + "PFR-222";
	public static final String PACKET_UPDATE_STATUS = REG_SERVICE_CODE + "UPS-217";
	public static final String PACKET_RETRIVE_STATUS = REG_SERVICE_CODE + "RPS-218";
	public static final String MACHINE_MAPPING_RUN_TIME_EXCEPTION = REG_SERVICE_CODE + "RDI-219";
	public static final String MACHINE_MAPPING_STATIONID_RUN_TIME_EXCEPTION = REG_SERVICE_CODE + "UMM-220";
	public static final String MACHINE_MAPPING_CENTERID_RUN_TIME_EXCEPTION = REG_SERVICE_CODE + "UMM-221";
	public static final String MACHINE_MAPPING_USERLIST_RUN_TIME_EXCEPTION = REG_SERVICE_CODE + "UMM-222";
	public static final String SYNC_STATUS_VALIDATE = REG_SERVICE_CODE + "SSV-223";
	public static final String MACHINE_MASTER_RECORD_NOT_FOUND = REG_SERVICE_CODE + "MMD-224";
	public static final String PACKET_META_CONVERTOR = REG_SERVICE_CODE + "PMC-225";

	// #Exceptions SyncJobs
	public static final String SYNC_TRANSACTION_NULL_POINTER_EXCEPTION = REG_SERVICE_CODE + "RPS-BTM-226";
	public static final String SYNC_JOB_RUN_TIME_EXCEPTION = REG_SERVICE_CODE + "RPS-JTD-227";
	public static final String PACKET_SYNC__STATUS_READER_NULL_POINTER_EXCEPTION = REG_SERVICE_CODE + "RPS-PSJ-228";
	public static final String BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION = REG_SERVICE_CODE + "RPS-BJ-229";
	public static final String BASE_JOB_NULL_POINTER_EXCEPTION = REG_SERVICE_CODE + "RPS-BJ-229";

	// Device Onboarding Service
	private static final String DEVICE_ONBOARDING_SERVICE = REG_SERVICE_CODE + "DVO-";
	public static final String FETCH_DEVICE_TYPES_EXCEPTION = DEVICE_ONBOARDING_SERVICE + "MMS-230";
	public static final String FETCH_DEVICE_MAPPING_EXCEPTION = DEVICE_ONBOARDING_SERVICE + "MMS-231";
	public static final String UPDATE_DEVICE_MAPPING_EXCEPTION = DEVICE_ONBOARDING_SERVICE + "MMS-232";

	// ID JSON Conversion
	private static final String ID_JSON_UTIL = REG_SERVICE_CODE + "IDJ-";
	public static final String ID_JSON_CONVERSION_EXCEPTION = ID_JSON_UTIL + "JSC-233";
	public static final String ID_JSON_PARSER_EXCEPTION = ID_JSON_UTIL + "JSC-234";
	public static final String ID_JSON_FIELD_ACCESS_EXCEPTION = ID_JSON_UTIL + "JSC-235";

	// Exceptions

	private static final String REG_UI_CODE = "REG-UI";

	public static final String REG_UI_LOGIN_LOADER_EXCEPTION = REG_UI_CODE + "RAI-001";
	public static final String REG_UI_LOGIN_SCREEN_LOADER_EXCEPTION = REG_UI_CODE + "LC-002";
	public static final String REG_UI_HOMEPAGE_LOADER_EXCEPTION = REG_UI_CODE + "ROC-003";
	public static final String REG_UI_SHEDULER_RUNTIME_EXCEPTION = REG_UI_CODE + "SHE-004";
	public static final String REG_UI_BASE_CNTRLR_IO_EXCEPTION = REG_UI_CODE + "BAS-005";
	public static final String REG_UI_VIEW_ACK_FORM_IO_EXCEPTION = REG_UI_CODE + "VAF-006";

	// Exceptions for Device Onboarding
	public static final String DEVICE_ONBOARD_PAGE_NAVIGATION_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "ROD-001";
	public static final String DEVICE_ONBOARD_INITIALIZATION_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-001";
	public static final String DEVICE_ONBOARD_LOADING_DEVICES_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-002";
	public static final String DEVICE_ONBOARD_MAPPING_DEVICES_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-003";
	public static final String DEVICE_ONBOARD_UNMAPPING_DEVICES_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-004";
	public static final String DEVICE_ONBOARD_HOME_NAVIGATION_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-005";
	public static final String DEVICE_ONBOARD_DEVICE_GROUPING_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-006";
	public static final String DEVICE_ONBOARD_DEVICE_UPDATING_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-007";
	public static final String DEVICE_ONBOARD_DEVICE_FILTERING_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-008";
	public static final String DEVICE_ONBOARD_DEVICE_POPULATION_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-009";
	public static final String DEVICE_ONBOARD_DEVICE_FETCHING_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-010";
	public static final String DEVICE_ONBOARD_CLEAR_CONTEXT_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-011";
	public static final String DEVICE_ONBOARD_FILTER_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-012";
	public static final String DEVICE_ONBOARD_FILTER_LIST_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-013";
	public static final String DEVICE_ONBOARD_SEARCH_DEVICE_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-014";

// Exceptions for User Registration - Iris & FingerPrint Capture	
	public static final String USER_REG_IRIS_CAPTURE_PAGE_LOAD_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-001";
	public static final String USER_REG_IRIS_CAPTURE_NEXT_SECTION_LOAD_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-002";
	public static final String USER_REG_IRIS_CAPTURE_PREV_SECTION_LOAD_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-003";
	public static final String USER_REG_IRIS_CAPTURE_POPUP_LOAD_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-004";
	public static final String USER_REG_IRIS_VALIDATION_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-005";
	public static final String USER_REG_IRIS_SCORE_VALIDATION_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-006";
	public static final String USER_REG_IRIS_SCAN_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "IFC-001";
	public static final String USER_REG_FINGERPRINT_SCAN_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE + "FSC-003";
	public static final String USER_REG_FINGERPRINT_PAGE_LOAD_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE + "FCC-001";
	public static final String USER_REG_FINGERPRINT_CAPTURE_NEXT_SECTION_LOAD_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE
			+ "FCC-002";
	public static final String USER_REG_FINGERPRINT_CAPTURE_PREV_SECTION_LOAD_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE
			+ "FCC-003";
	public static final String USER_REG_FINGERPRINT_CAPTURE_POPUP_LOAD_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE
			+ "FCC-004";
	public static final String USER_REG_FINGERPRINT_VALIDATION_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE + "FCC-005";
	public static final String USER_REG_FINGERPRINT_SCORE_VALIDATION_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE
			+ "FCC-006";
	public static final String USER_REG_IRIS_SAVE_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-008";
	public static final String USER_REG_GET_IRIS_QUALITY_SCORE_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-009";
	public static final String USER_REG_IRIS_STUB_IMAGE_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "IFC-002";

	// Exception for Registration - Document Scan and Upload
	public static final String USER_REG_DOC_SCAN_UPLOAD_EXP = USER_REGISTRATION_EXP_CODE + "SCN-001";

	// Scan
	public static final String USER_REG_SCAN_EXP = USER_REG_SCAN_EXP_CODE + "DOC-001";

	// To be moved to Logger Constants
	public static final String BASE_JOB_TITLE = "REGISTRATION - Base Job";
	public static final String BATCH_JOBS_PROCESS_LOGGER_TITLE = "REGISTRATION - Job Process Listener";
	public static final String BATCH_JOBS_TRIGGER_LOGGER_TITLE = "REGISTRATION - Job Trigger Listener";
	public static final String PACKET_SYNC_STATUS_JOB_TITLE = "REGISTRATION - Packet Sync Status Job";
	public static final String MASTER_SYNC_STATUS_JOB_TITLE = "REGISTRATION - Master Sync Status Job";
	public static final String BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE = "REGISTRATION - Sync Transaction Manager";
	public static final String BATCH_JOBS_CONFIG_LOGGER_TITLE = "REGISTRATION - Job Configuration Service";
	public static final String PRE_REG_DATA_SYNC_JOB_LOGGER_TITLE = "REGISTRATION - Pre Registration Data Sync Job";
	public static final String PRE_REG_DATA_SYNC_SERVICE_LOGGER_TITLE = "REGISTRATION - Pre Registration Data Sync Service";
	public static final String PRE_REG_DATA_SYNC_DAO_LOGGER_TITLE = "REGISTRATION - Pre Registration Data Sync DAO";
	// Regex Constants
	public static final String FULL_NAME_REGEX = "([A-z]+\\s?\\.?)+";
	public static final int FULL_NAME_LENGTH = 50;
	public static final String ADDRESS_LINE1_REGEX = "^.{1,50}$";
	public static final String MOBILE_NUMBER_REGEX = "\\d++";
	public static final int MOBILE_NUMBER_LENGTH = 9;
	public static final String EMAIL_ID_REGEX = "^([\\w\\-\\.]+)@((\\[([0-9]{1,3}\\.){3}[0-9]{1,3}\\])|(([\\w\\-]+\\.)+)([a-zA-Z]{2,4}))$";
	public static final String EMAIL_ID_REGEX_INITIAL = "([a-zA-Z]+\\.?\\-?\\@?(\\d+)?)+";
	public static final String CNI_OR_PIN_NUMBER_REGEX = "\\d{0,30}";
	public static final String AGE_REGEX = "\\d{1,2}";
	public static final String UIN_REGEX = "\\d{1,30}";

	// master sync
	public static final String MASTER_SYNC_SUCESS_MSG_CODE = "REG-MDS‌-001";
	public static final String MASTER_SYNC_OFFLINE_FAILURE_MSG_CODE = "REG-MDS‌-002";
	public static final String MASTER_SYNC_FAILURE_MSG_CODE = "REG-MDS‌-003";
	public static final String MASTER_SYNC_FAILURE_MSG_INFO = "Error in sync";
	public static final String MASTER_SYNC_FAILURE_MSG = "Sync failure";
	public static final String MASTER_SYNC_OFFLINE_FAILURE_MSG_INFO = "Client not online";
	public static final String MASTER_SYNC_OFFLINE_FAILURE_MSG = "You must be connected to the internet to sync data";
	public static final String MASTER_SYNC_EXCEPTION = "MASTER_SYNC_EXCEPTION";
	public static final String MASTER_SYNC_JOD_DETAILS = "MASTER_SYNC_JOB_DETAILS";
	public static final String MASTER_SYNC_SUCCESS = "Sync successful";
	public static final String MASTER_SYNC = "MASTER_SYNC";
}
