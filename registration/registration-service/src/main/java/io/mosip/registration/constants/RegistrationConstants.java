package io.mosip.registration.constants;

import java.util.Arrays;
import java.util.List;

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

	public static final String OTP_GENERATOR_SERVICE_NAME = "otp_generator";
	public static final String USERNAME_KEY = "key";
	public static final String OTP_GENERATED = "otp";
	public static final String OTP_VALIDATOR_SERVICE_NAME = "otp_validator";
	public static final String OTP_GENERATION_SUCCESS_MESSAGE = "Generated OTP is : ";
	public static final String OTP_VALIDATION_SUCCESS_MESSAGE = "OTP validation Successful";
	public static final String OTP_GENERATION_ERROR_MESSAGE = "Please Enter Valid Username ";
	public static final String OTP_GENERATOR_RESPONSE_DTO = "otpGeneratorResponseDTO";
	public static final String OTP_VALIDATOR_RESPONSE_DTO = "otpValidatorResponseDTO";

	public static final String ALERT_INFORMATION = "INFORMATION";
	public static final String ALERT_ERROR = "ERROR";
	public static final String ALERT_WARNING = "WARNING";

	// api related constant values
	public static final String HTTPMETHOD = "service.httpmethod";
	public static final String SERVICE_URL = "service.url";
	public static final String HEADERS = "service.headers";
	public static final String RESPONSE_TYPE = "service.responseType";
	public static final String REQUEST_TYPE = "service.requestType";
	public static final String AUTH_HEADER = "service.authheader";
	public static final String AUTH_REQUIRED = "service.authrequired";
	public static final String AUTH_TYPE = "BASIC";

	// Alert Related Details
	public static final String LOGIN_ALERT_TITLE = "LOGIN ALERT";
	public static final String LOGIN_INVALID_USERNAME = "Unable To Login";
	public static final String LOGIN_INVALID_OTP = "Unable To Login";
	public static final String GENERATED_OTP = "Generated OTP : ";

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
	
	//Web Camera Constants
	public static final String WEB_CAMERA_PAGE = "/fxml/WebCamera.fxml";
	public static final String WEB_CAMERA_PAGE_TITLE = "Applicant Biometrics";
	public static final String WEB_CAMERA_IMAGE_TYPE = "jpg";
	public static final String APPLICANT_PHOTOGRAPH_NAME = "Applicant Photograph";
	public static final String EXCEPTION_PHOTOGRAPH_NAME = "Exception Photograph";	
	public static final String WEBCAM_ALERT_TITLE = "Webcam Alert";
	public static final String WEBCAM_ALERT_HEADER = "Camera Not Found";
	public static final String WEBCAM_ALERT_CONTEXT = "Camera not found. Please connect an on-boarded camera and retry.";
	public static final String APPLICANT_IMAGE = "Applicant Image";
	public static final String EXCEPTION_IMAGE = "Exception Image";	
	public static final String APPLICANT_BIOMETRICS_ERROR = "Error in Applicant Biometrics";
	public static final String APPLICANT_IMAGE_ERROR = "Applicant Biometrics are required.";
	public static final String DEMOGRAPHIC_DETAILS_ERROR = "Error in Demographic Details";
	public static final String DEMOGRAPHIC_DETAILS_ERROR_CONTEXT = "Demographic details are required.";

	// Exception Code for Components
	public static final String PACKET_CREATION_EXP_CODE = "PCC-";
	public static final String PACKET_UPLOAD_EXP_CODE = "PAU-";
	public static final String REG_ACK_EXP_CODE = "ACK-";
	public static final String DEVICE_ONBOARD_EXP_CODE = "DVO-";
	public static final String SYNC_JOB_EXP_CODE = "SYN-";

	// Constants for Audits
	public static final String INTERNAL_SERVER_ERROR = "Internal error while creating packet";
	public static final String REGISTRATION_ID = "RID";

	// Login Sequence
	public static final String LOGIN_SEQUENCE = "sequence";
	public static final int INITIAL_LOGIN_SEQUENCE = 1;

	// USER CLIENT MACHINE MAPPING
	public static final String MACHINE_MAPPING_CODE = "USER CLIENT MACHINE MAPPING ALERT";
	public static final String MACHINE_MAPPING_SUCCESS_MESSAGE = "User Mapped Successfully";
	public static final String MACHINE_MAPPING_ERROR_MESSAGE = "Unable to map user";
	public static final String MACHINE_MAPPING_CREATED = "created";
	public static final String MACHINE_MAPPING_UPDATED = "updated";
	public static final String MACHINE_MAPPING_ENTITY_SUCCESS_MESSAGE = "User Data Fetched Successfully";
	public static final String MACHINE_MAPPING_ENTITY_ERROR_NO_RECORDS = "No Records Found";
	public static final String MACHINE_MAPPING_LOGGER_TITLE = "REGISTRATION - USER CLIENT MACHINE MAPPING";
	public static final String DEVICE_MAPPING_LOGGER_TITLE="REGISTRATION - CENTER MACHINE DEVICE MAPPING";
	//CENTER MACHINE DEVICE MAPPING
	public static final String DEVICE_MAPPING_SUCCESS_CODE="REG-DVO‌-001";
	public static final String DEVICE_MAPPING_SUCCESS_MESSAGE="On-boarding successful";
	public static final String DEVICE_MAPPING_ERROR_CODE="REG-DVO‌-002";
	public static final String DEVICE_MAPPING_ERROR_MESSAGE = "Unable to map the device";
	
	// BLOCKLISTED USER
	public static final String BLACKLISTED = "BLOCKED";

	// MAP ID
	public static final String USER_MACHINE_MAPID = "ListOfUserDTO";

	// ACTIVE INACTIVE USER
	public static final String USER_ACTIVE = "Active";
	public static final String USER_IN_ACTIVE = "In-Active";

	// opt to register constants
	public static final String OPT_TO_REG_GEO_CAP_FREQ = "GEO_CAP_FREQ";
	public static final String OPT_TO_REG_ICS‌_001 = "REG-ICS‌-001";
	public static final String OPT_TO_REG_ICS‌_001_MSG = "Time since last sync exceeded maximum limit. Please sync from server before proceeding with this registration";
	public static final String OPT_TO_REG_INFOTYPE = "ERROR";
	public static final String OPT_TO_REG_ICS‌_002 = "REG-ICS‌-002";
	public static final String OPT_TO_REG_ICS‌_002_MSG = "Time since last export of registration packets exceeded maximum limit. Please export or upload packets to server before proceeding with this registration";
	public static final String OPT_TO_REG_ICS‌_003 = "REG-ICS‌-003";
	public static final String OPT_TO_REG_ICS‌_003_MSG = "Maximum limit for registration packets on client reached. Please export or upload packets to server before proceeding with this registration";
	public static final String OPT_TO_REG_ICS‌_004 = "REG-ICS‌-004";
	public static final String OPT_TO_REG_ICS‌_004_MSG = "Your client machine’s location is outside the registration centre. Please note that registration can be done only from within the registration centre";
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
	public static final String OPT_TO_REG_ICS‌_005_MSG = "Unable to validate machine location. Please insert the GPS device and try again";
	public static final String OPT_TO_REG_ICS‌_006_MSG = "Unable to validate machine location due to weak GPS signal. Please try again.";
	public static final String OPT_TO_REG_ICS‌_007_MSG = "Unable to validate machine location due to GPS port mismatch. Please insert into specific port and try again.";
	public static final String OPT_TO_REG_SUCCESS = "success";
	public static final String OPT_TO_REG_LAST_CAPTURED_TIME = "lastCapturedTime";
	public static final String OPT_TO_REG_ERROR_MESSAGE = "errorMessage";
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
	public static final String PACKET_STATUS_SYNC_SUCCESS_MESSAGE = "Packet Status Sync Successful";
	public static final String PACKET_STATUS_SYNC_RESPONSE_ENTITY = "registrations";
	public static final String PACKET_STATUS_SYNC_SERVICE_NAME = "packet_status";
	public static final String PACKET_STATUS_SYNC_URL_PARAMETER = "registrationIds";
	public static final String PACKET_STATUS_SYNC_REGISTRATION_ID = "registrationId";
	public static final String PACKET_STATUS_SYNC_STATUS_CODE = "statusCode";
	public static final String PACKET_STATUS_SYNC_ERROR_RESPONSE = "No Status Available";
	public static final String PACKET_STATUS_CODE_PROCESSED = "processed";

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
	
	// CSS file
	public static final String CSS_FILE_PATH = "application.css";
	// Login
	public static final String LOGIN_METHOD_PWORD = "PWD";
	public static final String LOGIN_METHOD_OTP = "OTP";
	public static final String LOGIN_METHOD_BIO = "BIO";
	public static final String BLOCKED = "BLOCKED";
	public static final String MISSING_MANDATOTY_FIELDS = "Missing mandatory fields";
	public static final String USERNAME_FIELD_EMPTY = "UserName is required";
	public static final String PWORD_FIELD_EMPTY = "Password is required";
	public static final String CREDENTIALS_FIELD_EMPTY = "UserName and Password are required";
	public static final String USRNAME_PWORD_LENGTH = "Length of Username/Password should not be greater than 50";
	public static final String INCORRECT_PWORD = "Incorrect Username/Password";
	public static final String USER_NOT_ONBOARDED = "You have not been onboarded to use this client.";
	public static final String BLOCKED_USER_ERROR = "You are not authorized to perform registration.";
	public static final String LOGIN_INFO_MESSAGE = "Login Information";
	public static final String OTP_FIELD_EMPTY = "Please Enter OTP";
	public static final String LOGIN_FAILURE = "Unable To Login";
	public static final String OTP_INFO_MESSAGE = "OTP Login Information";
	public static final String OTP_VALIDATION_ERROR_MESSAGE = "Please Enter Valid OTP";
	public static final String USERNAME_FIELD_ERROR = "Please Enter Valid Username";
	public static final String LOGIN_INITIAL_SCREEN = "initialMode";
	public static final String LOGIN_INITIAL_VAL = "1";
	public static final String HH_MM_SS = "HH:mm:ss";
	// Login FingerPrint
	public static final String DEVICE_FP_NOT_FOUND = "Fingerprint device not found.";
	public static final String FINGER_PRINT_CAPTURE = "Please place your finger on device.";
	public static final String FINGER_PRINT_MATCH = "Fingerprint match not found.";
	public static final String DEVICE_INFO_MESSAGE = "Device Information";

	// Authorization Info
	public static final String AUTHORIZATION_ALERT_TITLE = "Authorization Alert";
	public static final String AUTHORIZATION_INFO_MESSAGE = "Authorization Information";
	public static final String ADMIN_ROLE = "SUPERADMIN";
	public static final String AUTHORIZATION_ERROR = "Please contact Admin.";
	public static final String ROLES_EMPTY = "RolesEmpty";
	public static final String ROLES_EMPTY_ERROR = "You are not authorized to perform registration.";
	public static final String MACHINE_MAPPING = "MachineMapping";
	public static final String MACHINE_MAPPING_ERROR = "You are not Authorized to login as you are not mapped to this machine. Please contact Admin.";
	public static final String SUCCESS_MSG = "Success";

	// Acknowledement Form
	public static final String ACKNOWLEDGEMENT_FORM_TITLE = "Registration Acknowledgement";

	// Date Format
	public static final String DATE_FORMAT = "MM/dd/yyy hh:mm:ss";

	// UI Registration Validations
	public static final String FULL_NAME_EMPTY = "Please Provide First Name";
	public static final String MIDDLE_NAME_EMPTY = "Please Provide Middle Name";
	public static final String LAST_NAME_EMPTY = "Please Provide Last Name";
	public static final String AGE_EMPTY = "Please Provide Age";
	public static final String AGE_WARNING = "Please Provide Date Of Birth";
	public static final String DATE_OF_BIRTH_EMPTY = "Please Provide Date Of Birth";
	public static final String GENDER_EMPTY = "Please Provide Gender";
	public static final String ADDRESS_LINE_1_EMPTY = "Please Provide Adress Line 1";
	public static final String ADDRESS_LINE_2_EMPTY = "Please Provide Adress Line 2";
	public static final String COUNTRY_EMPTY = "Please Provide Country";
	public static final String CITY_EMPTY = "Please Provide City";
	public static final String PROVINCE_EMPTY = "Please Provide Province";
	public static final String REGION_EMPTY = "Please Provide Region";
	public static final String POSTAL_CODE_EMPTY = "Please Provide Postal Code";
	public static final String MOBILE_NUMBER_EMPTY = "Please Provide Mobile Number";
	public static final String MOBILE_NUMBER_EXAMPLE = "Please provide 10 digit mobile no, Example : 9908542409";
	public static final String EMAIL_ID_EMPTY = "Please Provide Email Id";
	public static final String EMAIL_ID_EXAMPLE = "Example : xyz@xyz.com";
	public static final String PARENT_NAME_EMPTY = "Please Provide Parent Name";
	public static final String UIN_ID_EMPTY = "Please Provide Uin Id Of The Parent";
	public static final String ADDRESS_LINE_WARNING = "Address should be between 6 and 50 characters";
	public static final String LOCAL_ADMIN_AUTHORITY_EMPTY = "Please Provide Local Admin Authority";
	public static final String CNIE_OR_PIN_NUMBER_EMPTY = "Please Provide CNIE OR PIN NUMBER";
	public static final String POA_DOCUMENT_EMPTY = "Please Provide POA Document";
	public static final String POI_DOCUMENT_EMPTY = "Please Provide POI Document";
	public static final String POR_DOCUMENT_EMPTY = "Please Provide POR Document";
	public static final String ONLY_ALPHABETS="Only alphabets are allowed";
	public static final String TEN_LETTER_INPUT_LIMT="Please provide in the range of 6 to 10 letters";
	public static final String FIVE_DIGIT_INPUT_LIMT="Please provide 5 digit detail";

	public static final String MACHINE_MAPPING_ACTIVE = "ACTIVE";
	public static final String MACHINE_MAPPING_IN_ACTIVE = "IN-ACTIVE";

	// PacketStatusSync
	public static final String PACKET_STATUS_SYNC_ALERT_TITLE = "PACKET STATUS SYNC ALERT";
	public static final String PACKET_STATUS_SYNC_INFO_MESSAGE = "Packet Status Sync Information";

	// onBoard User
	public static final String ONBOARD_BIOMETRICS = "Biometrics - ";

	// RegistrationApproval
	public static final String STATUS = "STATUS";
	public static final String APPROVED_STATUS_MESSAGE = "Registration Approved successfully";
	public static final String APPROVED_STATUS_FAILURE_MESSAGE = "Registration Approval Failed";
	public static final String REJECTED_STATUS_MESSAGE = "Registration rejected successfully";
	public static final String REJECTED_STATUS_FAILURE_MESSAGE = "Registration rejection failed";
	public static final String ONHOLD_STATUS_MESSAGE = "Registration onHolded successfully";
	public static final String ONHOLD_STATUS_FAILURE_MESSAGE = "Registration onHold failed";
	public static final String ITEMS_PER_PAGE = "Items_per_page";
	public static final String PLACEHOLDER_LABEL = "No Packets for approval";
	public static final String REJECTION_BEAN_NAME = "rejectionController";
	public static final String ONHOLD_BEAN_NAME = "onHoldController";
	public static final String ERROR = "ERROR";
	public static final String REGISTRATIONID = "registrationID";
	public static final String STATUSCODE = "statusCode";
	public static final String STATUSCOMMENT = "statusComment";
	public static final String PENDING_ACTION_PAGE = "/fxml/RegistrationPendingAction.fxml";
	public static final String PENDING_APPROVAL_PAGE = "/fxml/RegistrationPendingApproval.fxml";
	public static final String REREGISTRATION_PAGE = "/fxml/ReRegistration.fxml";
	public static final String ONHOLD_COMMENTS = "ONHOLD_COMMENTS";
	public static final String REJECTION_COMMENTS = "REJECT_COMMENTS";
	

	// Upload Packet

	public static List getStatus() {
		String[] packetStatus = { "S", "resend", "E" };
		return Arrays.asList(packetStatus);
	}
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

	public static final String BIOMETRIC_IMAGE = "Image";
	public static final String BIOMETRIC_TYPE = "Type";
	// Packet Upload
	public static final String PACKET_TYPE = "file";
	public static final String PACKET_STATUS_PRE_SYNC = "PRE_SYNC";
	public static final String PACKET_STATUS_SYNC_TYPE = "NEW_REGISTRATION";
	public static final String ACKNOWLEDGEMENT_FILE = "_Ack";
	public static final String PACKET_SYNC_ERROR = "Error";
	public static final String RE_REGISTRATION_STATUS = "reregister";
	public static final String PACKET_SYNC_REF_ID = "packetsync";
	public static final String PACKET_UPLOAD_REF_ID = "packetUpload";

	// Device On-boarding
	public static final String DEVICE_MANUFACTURER_NAME = "manufacturerName";
	public static final String DEVICE_MODEL_NAME = "modelName";
	public static final String DEVICE_SERIAL_NO = "serialNo";
	public static final String ONBOARD_AVAILABLE_DEVICES = "availableDevices";
	public static final String ONBOARD_MAPPED_DEVICES = "mappedDevices";
	public static final String DEVICE_ONBOARD_EXCEPTION_ALERT = "Device Onboarding";
	public static final String DEVICE_ONBOARD_ERROR_MSG = "Internal error in Device Onboarding.";
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
	public static final String NOTIFICATION_SERVICE="REGISTRATION - NOTIFICATION SERVICE ";
	public static final List<String> MODE_OF_COMMUNICATION=Arrays.asList("MODE_OF_COMMUNICATION");
	public static final String NOTIFICATION_CODE = "NOTIFICATION ALERT";

	// Global configuration parameters
	public static final List<String> INVALID_LOGIN_PARAMS = Arrays.asList("INVALID_LOGIN_COUNT", "INVALID_LOGIN_TIME");
	public static final String INVALID_LOGIN_COUNT = "INVALID_LOGIN_COUNT";
	public static final String INVALID_LOGIN_TIME = "INVALID_LOGIN_TIME";

	// Lock User account
	public static final String USER_ACCOUNT_LOCK_MESSAGE = "Your account has been temporarily locked as you have made 5 unsuccessful login attempts. Please try logging in after 30 minutes.";
	public static final int INVALID_LOGIN_COUNT_RESET = 0;
	public static final int INVALID_LOGIN_COUNT_INCREMENT = 1;

	// Spring Batch-Jobs
	public static final String JOB_TRIGGER_STARTED = "Trigger started";
	public static final String JOB_TRIGGER_COMPLETED = "Trigger completed";
	public static final String JOB_EXECUTION_STARTED = "Execution started";
	public static final String JOB_EXECUTION_COMPLETED = "Execution completed";
	public static final String JOB_EXECUTION_SUCCESS = "Executed with success";
	public static final String JOB_EXECUTION_FAILURE = "Executed with failure";
	public static final String JOB_TRIGGER_MIS_FIRED = "Trigger Mis-Fired";
	public static final String JOB_EXECUTION_REJECTED = "Execution Rejected";
	public static final String JOB_TRIGGER_POINT_SYSTEM = "System";
	public static final String JOB_TRIGGER_POINT_USER = "User";
	public static final String BATCH_JOB_CODE = "Sync Job Alert";
	public static final String BATCH_JOB_START_SUCCESS_MESSAGE = "Jobs started successfully";
	public static final String BATCH_JOB_STOP_SUCCESS_MESSAGE = "Jobs stopped successfully";

	public static final String BATCH_JOBS_CONFIG_LOGGER_TITLE = "REGISTRATION - Job Configuration Service";
	public static final String BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE = "REGISTRATION - Sync Transaction Manager";
	public static final String BATCH_JOBS_TRIGGER_LOGGER_TITLE = "REGISTRATION - Job Trigger Listener";
	public static final String BATCH_JOBS_PROCESS_LOGGER_TITLE = "REGISTRATION - Job Process Listener";
	public static final String PACKET_SYNC_STATUS_JOB_TITLE = "REGISTRATION - Packet Sync Status Job";
	public static final String BASE_JOB_TITLE = "REGISTRATION - Base Job";

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
	
	public static List getPoaDocumentList() {
		return Arrays.asList(new String[] { "Document1", "Document2", "Document3", "Document4" });
	}

	public static List getPoiDocumentList() {
		return Arrays.asList(new String[] { "Document1", "Document2", "Document3", "Document4" });
	}

	public static List getPorDocumentList() {
		return Arrays.asList(new String[] { "Document1", "Document2", "Document3", "Document4" });
	}

}
