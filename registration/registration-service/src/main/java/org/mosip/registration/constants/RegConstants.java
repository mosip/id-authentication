package org.mosip.registration.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for storing the idisConstants
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RegConstants {
	
	/**
	 * The constructor
	 */
	private RegConstants() {
		
	}

	public static final String CONSTANTS_FILE_NAME = "constants.properties";

	public static final String AES_KEY_MANAGER_ALG = "AES_KEY_MANAGER_ALG";
	public static final String AES_CIPHER_ALG = "AES_CIPHER_ALG";
	public static final String AES_KEY_SEED_LENGTH = "AES_KEY_SEED_LENGTH";
	public static final String AES_SESSION_KEY_LENGTH = "AES_SESSION_KEY_LENGTH";
	public static final String AES_KEY_CIPHER_SPLITTER = "AES_KEY_CIPHER_SPLITTER";
	public static final String USER_NAME = "USER_NAME";
	public static final String PACKET_STORE_LOCATION = "PACKET_STORE_LOCATION";
	public static final String ZIP_FILE_EXTENSION = ".zip";
	public static final String DEMOGRPAHIC_JSON_NAME = "demographicJson";
	public static final String PACKET_META_JSON_NAME = "packetMetaJson";
	public static final String ENROLLMENT_META_JSON_NAME = "enrollmentMetaJson";
	public static final String HASHING_JSON_NAME = "hash";
	public static final String AUDIT_JSON_FILE = "audit";

	public static final String DBURL = "jdbc:derby:D:\\idsw;create=true";
	// Hash
	public static final String HASHING_ALG = "SHA-256";
	// RSA
	public static final String RSA_ALG = "RSA";
	public static final String RSA_PUBLIC_KEY_FILE = "D:/Key Store/public.key";
	public static final String RSA_PRIVATE_KEY_FILE = "D:/Key Store/private.key";
	public static final String RSA_CIPHER_ALG = "RSA/ECB/PKCS1Padding";
	public static final String PACKET_STORE_DATE_FORMAT = "PACKET_STORE_DATE_FORMAT";
	public static final String PACKET_ZIP_FILE_NAME = "PACKET_ZIP_FILE_NAME";
	public static final String ENROLLMENT_META_DATA_FILE_NAME = "ENROLLMENT_META_DATA_FILE_NAME";
	public static final String JSON_FILE_EXTENSION = ".json";
	public static final String TIME_STAMP_FORMAT = "TIME_STAMP_FORMAT";

	// Testing
	public static final String PACKET_UNZIP_LOCATION = "PACKET_UNZIP_LOCATION";

	// Constants for Registration Creation Zip
	/**
	 * Specifies the Image type for storing the images in zip file
	 */
	public final static String IMAGE_TYPE = ".jpg";

	/**
	 * Specifies the Document type for storing the documents in zip file
	 */
	public final static String DOC_TYPE = ".jpg";

	/**
	 * Specifies the names for storing the finger print images in zip file
	 */
	public final static Map<String, String> FINGERPRINT_IMAGE_NAMES_MAP = new HashMap<>();

	/**
	 * Specifies the names for storing the iris images in zip file
	 */
	public final static Map<String, String> IRIS_IMAGE_NAMES_MAP = new HashMap<>();;

	/**
	 * Specifies the names for storing the documents in zip file
	 */
	public final static Map<String, String> DOCUMENT_TYPES_MAP = new HashMap<>();

	/**
	 * Specifies the format for storing the Registration Acknowledgement
	 */
	public static final String IMAGE_FORMAT = "png";
	/**
	 * Specifies the path for storing the template in vm file
	 */
	public static final String TEMPLATE_PATH = "src/main/resources/templates/acktemplate.vm";

	static {
		FINGERPRINT_IMAGE_NAMES_MAP.put("leftthumb", "LeftThumb");
		FINGERPRINT_IMAGE_NAMES_MAP.put("rightthumb", "RightThumb");
		FINGERPRINT_IMAGE_NAMES_MAP.put("leftindexfinger", "LeftIndexFinger");
		FINGERPRINT_IMAGE_NAMES_MAP.put("leftmiddlefinger", "LeftMiddleFinger");
		FINGERPRINT_IMAGE_NAMES_MAP.put("leftringfinger", "LeftRingFinger");
		FINGERPRINT_IMAGE_NAMES_MAP.put("leftlittlefinger", "LeftLittleFinger");
		FINGERPRINT_IMAGE_NAMES_MAP.put("rightindexfinger", "RightIndexFinger");
		FINGERPRINT_IMAGE_NAMES_MAP.put("rightmiddlefinger", "RightMiddleFinger");
		FINGERPRINT_IMAGE_NAMES_MAP.put("rightringfinger", "RightRingFinger");
		FINGERPRINT_IMAGE_NAMES_MAP.put("rightlittlefinger", "RightLittleFinger");

		FINGERPRINT_IMAGE_NAMES_MAP.put("leftpalm", "LeftPalm");
		FINGERPRINT_IMAGE_NAMES_MAP.put("rightpalm", "RightPalm");

		IRIS_IMAGE_NAMES_MAP.put("lefteye", "LeftEye");
		IRIS_IMAGE_NAMES_MAP.put("righteye", "RightEye");

		DOCUMENT_TYPES_MAP.put("poa", "ProofOfAddress");
		DOCUMENT_TYPES_MAP.put("proofofaddress", "ProofOfAddress");
		DOCUMENT_TYPES_MAP.put("poi", "ProofOfIdentity");
		DOCUMENT_TYPES_MAP.put("proofofidentity", "ProofOfIdentity");
		DOCUMENT_TYPES_MAP.put("por", "ProofOfResident");
		DOCUMENT_TYPES_MAP.put("proofofresident", "ProofOfResident");
		DOCUMENT_TYPES_MAP.put("proofofresidenty", "ProofOfResident");
	}

	// Constants for Registration ID Generator - will be removed after Kernel
	// Integration
	public static String AGENCY_CODE = "2018";
	public static String STATION_NUMBER = "78213";
	public static String RID_DATE_FORMAT = "dd/MM/yyyy/HH/mm/ss";

	// Audit & Logger - Constants
	public static String APPLICATION_ID = "applicationId";
	public static String APPLICATION_NAME = "applicationName";

	// Default Host IP Address and Name for Audit Logs
	public static String HOST_IP = "host.hostIP";
	public static String HOST_NAME = "host.hostName";
	
	//OnlineConnectivity check
	public static String URL = "http://localhost:8080/getTokenId";
	
	public static String OTP_GENERATOR_SERVICE_NAME="otp_generator";
	public static String USERNAME_KEY="key";
	public static String OTP_GENERATED="otp";
	public static String OTP_VALIDATOR_SERVICE_NAME="otp_validator";
	public static String OTP_GENERATION_SUCCESS_MESSAGE="Generated OTP is : ";
	public static String OTP_VALIDATION_SUCCESS_MESSAGE="OTP validation Successful";
	public static String OTP_GENERATION_ERROR_MESSAGE="Please Enter Valid Username ";
	public static String OTP_VALIDATION_ERROR_MESSAGE="Please Enter Valid OTP";
	public static String OTP_GENERATOR_RESPONSE_DTO="otpGeneratorResponseDTO";
	public static String OTP_VALIDATOR_RESPONSE_DTO="otpValidatorResponseDTO";
	public static String OTP_INFO_MESSAGE="OTP Login Information";
	
	public static String ALERT_INFORMATION="INFORMATION";
	public static String ALERT_ERROR="ERROR";
	public static String ALERT_WARNING="WARNING";
	

	
	//api related constant values
	public static  String HTTPMETHOD="service.httpmethod";
	public static  String SERVICE_URL="service.url";
	public static String HEADERS="service.headers";
	public static  String RESPONSE_TYPE="service.responseType";
	public static  String REQUEST_TYPE="service.requestType";
	public static  String AUTH_HEADER="service.authheader";
	public static  String AUTH_REQUIRED="service.authrequired";
	public static String AUTH_TYPE = "BASIC";
	
	//Alert Related Details
	public static String LOGIN_ALERT_TITLE="LOGIN ALERT";
	public static String LOGIN_INVALID_USERNAME="Unable To Login";
	public static String LOGIN_INVALID_OTP="Unable To Login";
	public static String GENERATED_OTP="Generated OTP : ";

}
