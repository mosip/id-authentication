package io.mosip.registration.constants;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Class contains the constants used in Registration application
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RegistrationConstants {

	public static final String CENTER_ID = RandomStringUtils.randomNumeric(5);
	public static final String MACHINE_ID_GEN = RandomStringUtils.randomNumeric(5);

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
	public static final String UIN_UPDATE = "/fxml/UpdateUIN.fxml";
	public static final String SYNC_DATA = "/fxml/SyncDataProcess.fxml";

	// CSS file
	public static final String CSS_FILE_PATH = "application.css";
	
	//Images path
	public static final String CLOSE_IMAGE_PATH = "/images/close.jpg";
	public static final String DOC_STUB_PATH = "/images/PANStubbed.jpg";
	public static final String FP_IMG_PATH = "/images/fingerprint.jpg";
	public static final String IRIS_IMG_PATH = "/images/iris.jpg";
	public static final String FACE_IMG_PATH = "/images/face.jpg";
	
	// Authentication
	public static final String SUPERVISOR_VERIFICATION = "Supervisor Verification";
	public static final String SUPERVISOR_NAME = "Supervisor";
	public static final String SUPERVISOR_FINGERPRINT_LOGIN = "Supervisior Fingerprint Authentication";
	public static final String FINGER_PRINT_SINGLE = "single";
	public static final String FINGER_PRINT_MULTIPLE = "multiple";
	public static final String OTP_VALIDATION_SUCCESS = "success";
	public static final String SUCCESS = "Success";
	public static final String FAILURE = "Fail";
	public static final String SUPERVISOR = "SUPERVISOR";
	public static final String OFFICER = "OFFICER";
	public static final String IRIS_STUB = "leftIris";
	public static final String FACE_STUB = "face";

	// Login
	public static final String LOGIN_METHOD_PWORD = "PWD";
	public static final String LOGIN_METHOD_OTP = "OTP";
	public static final String LOGIN_METHOD_BIO = "BIO";
	public static final String LOGIN_METHOD_IRIS = "IRIS";
	public static final String LOGIN_METHOD_FACE = "FACE";
	public static final String USR_TEXT = "Enter your Username";
	public static final String PWORD_TEXT = "Enter your Password";
	public static final String OTP_TEXT = "Enter OTP";
	public static final String FINGERPRINT_TEXT = "Place your fingerprint on the device to scan it";
	public static final String IRIS_TEXT = "Scan your eye on the iris scanner";
	public static final String FACE_TEXT = "Bring your face near the camera";
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
	public static final String IS_CONSOLIDATED = "isConsolidated";
	public static final String CONSOLIDATED_VALIDATION = "Y";
	public static final String INDIVIDUAL_VALIDATION = "N";
	public static final String VALIDATION_SPLITTER="\\s,";
	public static final String ON_TYPE="_ontype";
	public static final String POR_DOCUMENTS="porDocuments";
	public static final String POR_BOX="porBox";
	public static final String VALIDATION_LOGGER="VALIDATIONS";
	public static final String REG_LGN_001="REG_LGN_001";
	public static final String REG_DDC_002_1="REG_DDC_002_1";
	public static final String REG_DDC_002_2="REG_DDC_002_2";
	public static final String REG_DDC_003_1="REG_DDC_003_1";
	public static final String REG_DDC_003_2="REG_DDC_003_2";
	public static final String REG_DDC_004_1="REG_DDC_004_1";
	public static final String REG_DDC_004_2="REG_DDC_004_2";
	public static final String UIN_UPDATE_ISUINUPDATE = "isUINUpdate";
	public static final String LOCAL_LANGUAGE="LocalLanguage";
	public static final String APPLICATION_LANGUAGE="application_language";
	public static final String REGISTRATION_LOCAL_LANGUAGE="local_language";
	public static final String PACKET_TYPE_NEW="New";
	public static final String FLAG_YES="Y";
	public static final String FLAG_NO="N";
	public static final String REGISTRATION_DTO="registrationDto";
	public static final String ADDRESS_LINE1="addressLine1";
	public static final String ADDRESS_LINE2="addressLine2";
	public static final String ADDRESS_LINE3="addressLine3";
	public static final String FULL_NAME="fullName";
	public static final String CHILD="Child";
	public static final String ADULT="Adult";
	public static final String AGE_DATEPICKER_CONTENT="ageDatePickerContent";


	public static final String MACHINE_MAPPING_ACTIVE = "ACTIVE";
	public static final String MACHINE_MAPPING_IN_ACTIVE = "IN-ACTIVE";
	public static final String TOGGLE_BIO_METRIC_EXCEPTION = "toggleBiometricException";

	//update UIN
	public static final String UIN_NAV_LABEL="/ UIN Update";
	public static final String UIN_LABEL="UIN";
	public static final String FIRST_TOGGLE_LABEL="toggleLabel1";
	public static final String SECOND_TOGGLE_LABEL="toggleLabel2";
	public static final String PACKET_TYPE_UPDATE="Update";



	
	// onBoard User
	public static final String ONBOARD_BIOMETRICS = "Biometrics - ";

	// RegistrationApproval
	public static final String PLACEHOLDER_LABEL = "No Packets for approval";
	public static final String REGISTRATIONID = "registrationID";
	public static final String STATUSCODE = "statusCode";
	public static final String STATUSCOMMENT = "statusComment";
	public static final String ONHOLD_COMMENTS = "ONHOLD_COMMENTS";
	public static final String REJECTION_COMMENTS = "REJECT_COMMENTS";
	public static final String EMPTY = "";
	public static final String CONSTANTS_FILE_NAME = "/constants.properties";
	public static final String APPROVED = "Approved";
	public static final String REJECTED = "Rejected";

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
	public static final String VALIDATION_TYPE_IRIS = "Iris";
	public static final String VALIDATION_TYPE_FACE = "Face";
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

	// Velocity Template Generator Constants
	public static final String TEMPLATE_REGISTRATION_ID = "RegId";
	public static final String TEMPLATE_DATE_FORMAT = "dd/MM/yyyy";
	public static final String TEMPLATE_DATE = "Date";
	public static final String TEMPLATE_FULL_NAME = "FullName";
	public static final String TEMPLATE_DOB = "DOB";
	public static final String TEMPLATE_GENDER = "Gender";
	public static final String TEMPLATE_ADDRESS_LINE1 = "AddressLine1";
	public static final String TEMPLATE_ADDRESS_LINE2 = "AddressLine2";
	public static final String TEMPLATE_ADDRESS_LINE3 = "AddressLine3";
	public static final String TEMPLATE_CITY = "City";
	public static final String TEMPLATE_POSTAL_CODE = "PostalCode";
	public static final String TEMPLATE_STATE = "State";
	public static final String TEMPLATE_COUNTRY = "Region";
	public static final String TEMPLATE_LOCAL_AUTHORITY = "LocalAuthority";
	public static final String TEMPLATE_MOBILE = "Mobile";
	public static final String TEMPLATE_EMAIL = "Email";
	public static final String TEMPLATE_CNIE_NUMBER = "CNIE";
	public static final String TEMPLATE_PARENT_NAME = "ParentName";
	public static final String TEMPLATE_PARENT_UIN = "ParentUIN";
	public static final String TEMPLATE_DOCUMENTS = "Documents";
	public static final String TEMPLATE_BIOMETRICS_CAPTURED = "Biometrics";
	public static final String TEMPLATE_RO_NAME = "ROName";
	public static final String TEMPLATE_IMAGE_ENCODING = "data:image/jpg;base64,";
	public static final String TEMPLATE_MISSING_FINGER = "&#10008;";
	public static final String TEMPLATE_HANDS_IMAGE_PATH = "/images/hands.jpg";
	public static final String TEMPLATE_QRCODE_IMAGE_PATH = "/images/QRCode.jpg";
	public static final String TEMPLATE_HANDS_IMAGE_SOURCE = "handsImageSource";
	public static final String TEMPLATE_QRCODE_SOURCE = "QRCodeSource";
	public static final String TEMPLATE_RESIDENT_NAME = "ResidentName";
	public static final String TEMPLATE_IMAGE_SOURCE = "ApplicantImageSource";
	public static final String TEMPLATE_EXCEPTION_IMAGE_SOURCE = "ExceptionImageSource";
	public static final String TEMPLATE_WITHOUT_EXCEPTION_IMAGE = "WithoutExceptionImage";
	public static final String TEMPLATE_WITH_EXCEPTION_IMAGE = "WithExceptionImage";
	public static final String TEMPLATE_STYLE_PROPERTY = "style='visibility:hidden;'";
	public static final String TEMPLATE_NAME = "Acknowledgement Template";
	public static final String TEMPLATE_DATE_LOCAL_LANG_LABEL = "DateLocalLangLabel";
	public static final String TEMPLATE_FULL_NAME_LOCAL_LANG_LABEL = "FullNameLocalLangLabel";
	public static final String TEMPLATE_DOB_LOCAL_LANG_LABEL = "DOBLocalLangLabel";
	public static final String TEMPLATE_GENDER_LOCAL_LANG_LABEL = "GenderLocalLangLabel";
	public static final String TEMPLATE_ADDRESS_LINE1_LOCAL_LANG_LABEL = "AddressLine1LocalLangLabel";
	public static final String TEMPLATE_ADDRESS_LINE2_LOCAL_LANG_LABEL = "AddressLine2LocalLangLabel";
	public static final String TEMPLATE_ADDRESS_LINE3_LOCAL_LANG_LABEL = "AddressLine3LocalLangLabel";
	public static final String TEMPLATE_CITY_LOCAL_LANG_LABEL = "CityLocalLangLabel";
	public static final String TEMPLATE_POSTAL_CODE_LOCAL_LANG_LABEL = "PostalCodeLocalLangLabel";
	public static final String TEMPLATE_PROVINCE_LOCAL_LANG_LABEL = "ProvinceLocalLangLabel";
	public static final String TEMPLATE_COUNTRY_LOCAL_LANG_LABEL = "RegionLocalLangLabel";
	public static final String TEMPLATE_LOCAL_AUTHORITY_LOCAL_LANG_LABEL = "LocalAuthorityLocalLangLabel";
	public static final String TEMPLATE_MOBILE_LOCAL_LANG_LABEL = "MobileLocalLangLabel";
	public static final String TEMPLATE_EMAIL_LOCAL_LANG_LABEL = "EmailLocalLangLabel";
	public static final String TEMPLATE_CNIE_LOCAL_LANG_LABEL = "CNIELocalLangLabel";
	public static final String TEMPLATE_PARENT_NAME_LOCAL_LANG_LABEL = "ParentNameLocalLangLabel";
	public static final String TEMPLATE_PARENT_UIN_LOCAL_LANG_LABEL = "ParentUINLocalLangLabel";
	public static final String TEMPLATE_DOCUMENTS_LOCAL_LANG_LABEL = "DocumentsLocalLangLabel";
	public static final String TEMPLATE_BIOMETRICS_LOCAL_LANG_LABEL = "BiometricsLocalLangLabel";
	public static final String TEMPLATE_RO_NAME_LOCAL_LANG_LABEL = "RONameLocalLangLabel";
	public static final String TEMPLATE_FULL_NAME_LOCAL_LANG = "FullNameLocalLang";
	public static final String TEMPLATE_GENDER_LOCAL_LANG = "GenderLocalLang";
	public static final String TEMPLATE_ADDRESS_LINE1_LOCAL_LANG = "AddressLine1LocalLang";
	public static final String TEMPLATE_ADDRESS_LINE2_LOCAL_LANG = "AddressLine2LocalLang";
	public static final String TEMPLATE_ADDRESS_LINE3_LOCAL_LANG = "AddressLine3LocalLang";
	public static final String TEMPLATE_CITY_LOCAL_LANG = "CityLocalLang";
	public static final String TEMPLATE_PROVINCE_LOCAL_LANG = "ProvinceLocalLang";
	public static final String TEMPLATE_COUNTRY_LOCAL_LANG = "RegionLocalLang";
	public static final String TEMPLATE_LOCAL_AUTHORITY_LOCAL_LANG = "LocalAuthorityLocalLang";
	public static final String TEMPLATE_PARENT_NAME_LOCAL_LANG = "ParentNameLocalLang";
	public static final String TEMPLATE_DOCUMENTS_LOCAL_LANG = "DocumentsLocalLang";
	public static final String TEMPLATE_BIOMETRICS_LOCAL_LANG = "BiometricsLocalLang";
	public static final String TEMPLATE_RO_NAME_LOCAL_LANG = "RONameLocalLang";
	public static final String TEMPLATE_WITH_PARENT_DETAILS = "WithParent";
	public static final String TEMPLATE_STYLE_HIDDEN_PROPERTY = "style='display:none;'";

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
	public static final String OPT_TO_REG_RDJ_J00010 = "RDJ_J00010";

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
	public static final String PACKET_STATUS_SYNC_TYPE = "NEW";
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

	public static List<String> DOCUMENT_LIST = Arrays.asList("Passport", "VoterId", "Licence", "PAN");
	
	public static final List<String> PACKET_STATUS = Arrays.asList("APPROVED", "REJECTED", "RE_REGISTER_APPROVED");

	// Pre Registration
	public static final String PRE_REGISTRATION_ID = "preId";
	public static final String GET_PRE_REGISTRATION_IDS = "get_pre_registration_Ids";
	public static final String GET_PRE_REGISTRATION = "get_pre_registration";
	public static final String REGISTRATION_CLIENT_ID = "59276903416082";
	public static final String PRE_REGISTRATION_DUMMY_ID = "mosip.pre-registration.datasync";
	public static final String VER = "1.0";
	public static final String PRE_REG_TO_GET_ID_ERROR = "Unable to get Pre registartion id's";
	public static final String PRE_REG_TO_GET_PACKET_ERROR = "Unable to get Pre registartion for given id";
	public static final String PRE_REG_SUCCESS_MESSAGE = "Got Pre registartion ";
	public static final String IS_PRE_REG_SYNC = "PreRegSync";
	public static final String PRE_REG_FILE_NAME = "fileName";
	public static final String PRE_REG_FILE_CONTENT = "fileContent";
	public static final String PRE_REG_APPOINMENT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	// UI Date Format
	public static final String DATE_FORMAT = "MM/dd/yyy hh:mm:ss";
	public static final String HH_MM_SS = "HH:mm:ss";

	// Iris & Fingerprint Capture for Individual Registration
	public static final String IRIS_THRESHOLD = "IRIS_THRESHOLD";
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
	public static final String DUPLICATE_FINGER = "DuplicateFinger";
	public static final String ISO_IMAGE_FILE = "ISOImage.iso";
	public static final String LEFTHAND_SLAP_FINGERPRINT_PATH = "/fingerprints/leftSlap.jpg";
	public static final String RIGHTHAND_SLAP_FINGERPRINT_PATH = "/fingerprints/rightSlap.jpg";
	public static final String BOTH_THUMBS_FINGERPRINT_PATH = "/fingerprints/thumbs.jpg";
	public static final String LEFTSLAP_FINGERPRINT_THRESHOLD = "LEFTSLAP_FINGERPRINT_THRESHOLD";
	public static final String RIGHTSLAP_FINGERPRINT_THRESHOLD = "RIGHTSLAP_FINGERPRINT_THRESHOLD";
	public static final String THUMBS_FINGERPRINT_THRESHOLD = "THUMBS_FINGERPRINT_THRESHOLD";
	public static final String FINGERPRINT_RETRIES_COUNT = "NUM_OF_FINGERPRINT_RETRIES";
	public static final String IRIS_RETRY_COUNT = "NUM_OF_IRIS_RETRIES";
	public static final String[] LEFTHAND_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/lefthand/leftIndex/",
			"/fingerprints/lefthand/leftLittle/", "/fingerprints/lefthand/leftMiddle/",
			"/fingerprints/lefthand/leftRing/" };
	public static final String[] RIGHTHAND_SEGMNTD_DUPLICATE_FILE_PATHS = new String[] {
			"/fingerprints/righthand/rightIndex/", "/fingerprints/righthand/rightLittle/",
			"/fingerprints/righthand/rightMiddle/", "/fingerprints/righthand/rightRing/" };
	public static final String[] RIGHTHAND_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/Srighthand/rightIndex/",
			"/fingerprints/Srighthand/rightLittle/", "/fingerprints/Srighthand/rightMiddle/",
			"/fingerprints/Srighthand/rightRing/" };
	public static final String[] THUMBS_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/thumb/leftThumb/",
			"/fingerprints/thumb/rightThumb/" };

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
	public static final String REGISTRATION_DELETION_JOB_LOGGER_TITLE = "REGISTRATION - Pre Registration Data Sync DAO";
	
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
	public static final String POSTAL_CODE_REGEX = "\\d{5}";
	public static final String POSTAL_CODE_REGEX_INITIAL = "\\d{1,5}";

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
	public static final String MASTER_VALIDATOR_SERVICE_NAME = "master_sync";
	// POLICY SYNC
	public static final String POLICY_SYNC_SUCCESS_CODE = "REG-MDS‌-001 ";
	public static final String POLICY_SYNC_SUCCESS_MESSAGE = "Sync Successful";
	public static final String POLICY_SYNC_ERROR_CODE = "REG-MDS‌-003 ";
	public static final String POLICY_SYNC_ERROR_MESSAGE = "Sync failure";
	public static final String POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_CODE = "REG-MDS‌-002";
	public static final String POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_MESSAGE = "You must be connected to the internet to sync data";
	public static final String POLICY_SYNC_THRESHOLD_VALUE = "name";
	public static final String KEY_POLICY_SYNC_JOB_TITLE = "REGISTRATION - key policy synch Job";

	// PRE-REG DELETE JOB
	public static final String PRE_REG_DELETE_SUCCESS = "Pre-Registration Records deleted";
	public static final String PRE_REG_DELETE_FAILURE = "Error While Deleting the records";

	// Connection Error
	public static final String CONNECTION_ERROR = "Unable to establish the connection";

	// Exceptions - Template Generator
	public static final String TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION = PACKET_CREATION_EXP_CODE + "TGE-002";
	public static final String TEMPLATE_GENERATOR_SMS_EXCEPTION = PACKET_CREATION_EXP_CODE + "TGE-002";

	// Jobs
	public static final String BATCH_JOB_START_SUCCESS_MESSAGE = "SYNC-DATA Process started successfully";
	public static final String START_SCHEDULER_ERROR_MESSAGE = "Unable to start SYNC-DATA process";
	public static final String BATCH_JOB_STOP_SUCCESS_MESSAGE = "Jobs stopped successfully";
	public static final String STOP_SCHEDULER_ERROR_MESSAGE = "Unable to stop SYNC-DATA Process";
	public static final String CURRENT_JOB_DETAILS_ERROR_MESSAGE = "Unable to fetch current running job details";
	public static final String EXECUTE_JOB_ERROR_MESSAGE = "Unable to execute job";
	public static final String SYNC_DATA_PROCESS_ALREADY_STARTED = "SYNC-DATA Process already running";
	public static final String SYNC_DATA_PROCESS_ALREADY_STOPPED = "SYNC-DATA Process not running to stop";
	public static final String SYNC_DATA_DTO = "SYNC-DATA DTO";
	public static final String JOB_RUNNING = "RUNNING";
	public static final String JOB_COMPLETED = "COMPLETED";
	public static final String NO_JOB_COMPLETED = "NO JOBS COMPLETED";
	public static final String NO_JOBS_TRANSACTION = "No Transaction History Available";
	public static final String NO_JOBS_RUNNING = "Currently No Jobs Running";
	// Machine Mapping
	public static final String MACHINE_MAPPING_SUCCESS_MESSAGE = "User Mapped Successfully";
	public static final String MACHINE_MAPPING_ERROR_MESSAGE = "Unable to map user";
	public static final String MACHINE_MAPPING_ENTITY_SUCCESS_MESSAGE = "User Data Fetched Successfully";
	public static final String MACHINE_MAPPING_ENTITY_ERROR_NO_RECORDS = "No Records Found";
	public static final String DEVICE_MAPPING_SUCCESS_MESSAGE = "On-boarding successful";
	public static final String DEVICE_MAPPING_ERROR_MESSAGE = "Unable to map the device";

	// PACKET
	public static final String PACKET_STATUS_SYNC_ERROR_RESPONSE = "No Status Available";
	public static final String PACKET_STATUS_SYNC_SUCCESS_MESSAGE = "Packet Status Sync Successful";

	// OTP
	public static final String OTP_GENERATION_SUCCESS_MESSAGE = "Generated OTP is :";
	public static final String OTP_GENERATION_ERROR_MESSAGE = "Please Enter Valid Username";

	// Sync Status
	public static final String OPT_TO_REG_TIME_EXPORT_EXCEED = "Time since last export of registration packets exceeded maximum limit. Please export or upload packets to server before proceeding with this registration";
	public static final String OPT_TO_REG_TIME_SYNC_EXCEED = "Time since last sync exceeded maximum limit. Please sync from server before proceeding with this registration";
	public static final String OPT_TO_REG_REACH_MAX_LIMIT = "Maximum limit for registration packets on client reached. Please export or upload packets to server before proceeding with this registration";
	public static final String OPT_TO_REG_OUTSIDE_LOCATION = "Your client machine location is outside the registration center. Please note that registration can be done only from within the registration centre";
	public static final String OPT_TO_REG_WEAK_GPS = "Unable to validate machine location due to weak GPS signal. Please try again";
	public static final String OPT_TO_REG_INSERT_GPS = "Unable to validate machine location. Please insert the GPS device and try again";
	public static final String OPT_TO_REG_GPS_PORT_MISMATCH = "Unable to validate machine location due to GPS port mismatch. Please insert into specific port and try again";

	public static final String POLICY_SYNC_SERVICE="policysync";
	public static final String KEY_NAME="KEY_POLICY_SYNC_THRESHOLD_VALUE";
	public static final String REFERENCE_ID="1001";
	
	//Reg Deletion
	public static final String REGISTRATION_DELETION_BATCH_JOBS_SUCCESS="Registartion Packets Deletion Successful ";
	public static final String REGISTRATION_DELETION_BATCH_JOBS_FAILURE="Registartion Packets Deletion Failure ";
	
	//Application Language
	public static final String APPLICATION_LANUAGE="application_language";
	
	
	public enum mappedCodeForLang{
		en("ENG");
		private String langCode;
		mappedCodeForLang(String langCode) {
			this.langCode=langCode;
		}
		public String getMappedCode() {
			return langCode;
		}
		
	}
	
}
