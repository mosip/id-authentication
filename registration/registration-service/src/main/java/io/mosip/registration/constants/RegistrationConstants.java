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
	public static final String RESOURCE_LOADER = "file";
	public static final String FILE_RESOURCE_LOADER_CLASS = "file.resource.loader.class";
	public static final String FILE_RESOURCE_LOADER_PATH = "file.resource.loader.path";
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
	public static final String TEMPLATE_IMAGE_SOURCE = "imagesource";
	public static final String TEMPLATE_IMAGE_ENCODING = "data:image/jpg;base64,";
	public static final String TEMPLATE_MISSING_FINGER = "&#10008;";
	public static final String TEMPLATE_BIOMETRICS_CAPTURED = "BiometricsCaptured";
	public static final String TEMPLATE_HANDS_IMAGE_PATH = "src/main/resources/images/hands.jpg";

	// Exception Code for Components
	public static final String PACKET_CREATION_EXP_CODE = "PCC-";
	public static final String PACKET_UPLOAD_EXP_CODE = "PAU-";
	public static final String REG_ACK_EXP_CODE = "ACK-";
	
	// Constants for Audits
	public static final String INTERNAL_SERVER_ERROR = "Internal error while creating packet";
	public static final String REGISTRATION_ID = "RID";
	
	//Login Sequence
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
	// BLOCKLISTED USER
	public static final String BLACKLISTED = "BLOCKED";

	// MAP ID
	public static final String USER_MACHINE_MAPID = "ListOfUserDTO";

	
	// ACTIVE INACTIVE USER
	public static final String USER_ACTIVE = "Active";
	public static final String USER_IN_ACTIVE = "In-Active";

	//opt to register constants
	public static final String OPT_TO_REG_GEO_CAP_FREQ = "GEO_CAP_FREQ";
	public static final String OPT_TO_REG_LER_J00009 = "LER_J00009"; 
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
	public static final String OPT_TO_REG_ICS‌_005_MSG = "Unable to validate machine location. Please insert the GPS device and try again";
	public static final String OPT_TO_REG_SUCCESS = "success";

	/** Packet Status Sync Constants */
	public static final String PACKET_STATUS_SYNC_SUCCESS_MESSAGE = "Packet Status Sync Successful";
	public static final String PACKET_STATUS_SYNC_RESPONSE_ENTITY = "registrations";
	public static final String PACKET_STATUS_SYNC_SERVICE_NAME = "packet_status";
	public static final String PACKET_STATUS_SYNC_URL_PARAMETER = "registrationIds";
	public static final String PACKET_STATUS_SYNC_REGISTRATION_ID = "registrationId";
	public static final String PACKET_STATUS_SYNC_STATUS_CODE = "statusCode";
	public static final String PACKET_STATUS_SYNC_ERROR_RESPONSE = "No Status Available";
	
	/**Exception codes**/
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
	public static final String PACKET_UPDATE_STATUS=REG_SERVICE_CODE+"UPS-217";
	public static final String PACKET_RETRIVE_STATUS=REG_SERVICE_CODE+"RPS-218";
	public static final String MACHINE_MAPPING_RUN_TIME_EXCEPTION=REG_SERVICE_CODE+"RDI-219";
	public static final String MACHINE_MAPPING_STATIONID_RUN_TIME_EXCEPTION=REG_SERVICE_CODE+"UMM-220";
	public static final String MACHINE_MAPPING_CENTERID_RUN_TIME_EXCEPTION=REG_SERVICE_CODE+"UMM-221";
	public static final String MACHINE_MAPPING_USERLIST_RUN_TIME_EXCEPTION=REG_SERVICE_CODE+"UMM-222";	
	public static final String SYNC_STATUS_VALIDATE=REG_SERVICE_CODE+"SSV-223";
	public static final String MACHINE_MASTER_RECORD_NOT_FOUND=REG_SERVICE_CODE+"MMD-224";

	
	/***********UI Constants**********/
	//paths of FXML pages to be loaded
		public static final String ERROR_PAGE = "/fxml/ErrorPage.fxml";
		public static final String INITIAL_PAGE = "/fxml/RegistrationLogin.fxml";
		public static final String LOGIN_PWORD_PAGE = "/fxml/LoginWithCredentials.fxml";
		public static final String LOGIN_OTP_PAGE = "/fxml/LoginWithOTP.fxml";
		public static final String HOME_PAGE = "/fxml/RegistrationOfficerLayout.fxml";
		public static final String HEADER_PAGE = "/fxml/Header.fxml";
		public static final String UPDATE_PAGE = "/fxml/UpdateLayout.fxml";
		public static final String OFFICER_PACKET_PAGE = "/fxml/RegistrationOfficerPacketLayout.fxml";
		public static final String CREATE_PACKET_PAGE = "/fxml/Registration.fxml";
		public static final String ACK_RECEIPT_PATH = "/fxml/AckReceipt.fxml";
		public static final String APPROVAL_PAGE = "/fxml/RegistrationApproval.fxml";
		public static final String FTP_UPLOAD_PAGE =  "/fxml/PacketUpload.fxml";
		public static final String USER_MACHINE_MAPPING =  "/fxml/UserClientMachineMapping.fxml";
		public static final String SYNC_STATUS =  "/fxml/RegPacketStatus.fxml";
		
		//CSS file
		public static final String CSS_FILE_PATH = "application.css";
		// Login
		public static final String LOGIN_METHOD_PWORD = "PWD";
		public static final String OTP = "OTP";
		public static final String BLOCKED = "BLOCKED";
		public static final String MISSING_MANDATOTY_FIELDS = "Missing mandatory fields";
		public static final String USERNAME_FIELD_EMPTY = "UserName is required";
		public static final String PWORD_FIELD_EMPTY = "Password is required";
		public static final String CREDENTIALS_FIELD_EMPTY = "UserName and Password are required";
		public static final String INCORRECT_PWORD = "Incorrect Password";
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
		
		//Acknowledement Form
		public static final String ACKNOWLEDGEMENT_FORM_TITLE = "Registration Acknowledgement";
		
		//Date Format
		public static final String DATE_FORMAT = "MM/dd/yyy hh:mm:ss";
		
		//UI Registration Validations
		public static final String FIRST_NAME_EMPTY = "Please Provide First Name";
		public static final String MIDDLE_NAME_EMPTY = "Please Provide Middle Name";
		public static final String LAST_NAME_EMPTY = "Please Provide Last Name";
		public static final String AGE_EMPTY = "Please Provide Age";
		public static final String AGE_WARNING = "Please Provide Date Of Birth";
		public static final String DATE_OF_BIRTH_EMPTY = "Please Provide Date Of Birth";
		public static final String GENDER_EMPTY = "Please Provide Gender";
		public static final String ADDRESS_LINE_1_EMPTY = "Please Provide Adress Line 1";
		public static final String ADDRESS_LINE_2_EMPTY = "Please Provide Adress Line 2";
		public static final String COUNTRY_EMPTY = "Please Provide Country";
		public static final String STATE_EMPTY = "Please Provide State";
		public static final String DISTRICT_EMPTY = "Please Provide District";
		public static final String REGION_EMPTY = "Please Provide Region";
		public static final String PIN_EMPTY = "Please Provide Adress Pin";
		public static final String MOBILE_NUMBER_EMPTY = "Please Provide Mobile Number";
		public static final String MOBILE_NUMBER_EXAMPLE = "Example : 99-9854-2496";
		public static final String LAND_LINE_NUMBER_EMPTY = "Please Provide Land Line Number";
		public static final String LAND_LINE_NUMBER_EXAMPLE = "Example : 44-9854";
		public static final String PARENT_NAME_EMPTY = "Please Provide Parent Name";
		public static final String UIN_ID_EMPTY = "Please Provide Uin Id Of The Parent";
		public static final String ADDRESS_LINE_WARNING = "Address should be between 6 and 20 characters";

		public static final String MACHINE_MAPPING_ACTIVE = "ACTIVE";
		public static final String MACHINE_MAPPING_IN_ACTIVE = "IN-ACTIVE";
		
		//PacketStatusSync
		public static final String PACKET_STATUS_SYNC_ALERT_TITLE="PACKET STATUS SYNC ALERT";
		public static final String PACKET_STATUS_SYNC_INFO_MESSAGE="Packet Status Sync Information";
		
		//onBoard User
		public static final String ONBOARD_BIOMETRICS="Biometrics - ";
		
		public static List getCountries(){
			
			String countries[] = new String[] {"Morocco","Zimbabwe","Zambia","Yemen","Vietnam","Venezuela","Vatican City (Holy See)","Vanuatu","Uzbekistan","Uruguay"};
			
			return Arrays.asList(countries);
		}
		
		public static List getStates(){
			
			String states[] = new String[] {"Tanger-Tetouan-Al Hoceima","Oriental","Fès-Meknès","Rabat-Salé-Kénitra","Béni Mellal-Khénifra"};
			
			return Arrays.asList(states);
		}
		
		public static List getDistricts(){
			
			String districts[] = new String[] {"Casablanca-Settat","Marrakesh-Safi","Drâa-Tafilalet","Souss-Massa","Guelmim-Oued"};
			
			return Arrays.asList(districts);
		}
		
		public static List getRegions(){
			
			String regions[] = new String[] {"Tangier","Oujda","Fès","Rabat","Béni Mellal"};
			
			return Arrays.asList(regions);
		}
		
		public static List getPins(){
			
			String pins[] = new String[] {"80851","80852","80853","80854","80855"};
			
			return Arrays.asList(pins);
		}
		
		//Exceptions
		
		private static final String REG_UI_CODE = "REG-UI";
		
		public static final String REG_UI_LOGIN_LOADER_EXCEPTION = REG_UI_CODE + "RAI-001";
		public static final String REG_UI_LOGIN_SCREEN_LOADER_EXCEPTION = REG_UI_CODE +"LC-002";
		public static final String REG_UI_HOMEPAGE_LOADER_EXCEPTION = REG_UI_CODE + "ROC-003";
		public static final String REG_UI_SHEDULER_RUNTIME_EXCEPTION = REG_UI_CODE + "SHE-004";
		public static final String REG_UI_BASE_CNTRLR_IO_EXCEPTION = REG_UI_CODE+"BAS-005";
		public static final String REG_UI_VIEW_ACK_FORM_IO_EXCEPTION = REG_UI_CODE+"VAF-006";

	
}
