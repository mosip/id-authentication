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
	
	public static final String DBURL = "jdbc:derby:D:\\idsw;create=true";
	//Hash
	public static final String HASHING_ALG="SHA-256";
	//RSA
	public static final String RSA_ALG="RSA";
	public static final String RSA_PUBLIC_KEY_FILE= "D:/Key Store/public.key";
	public static final String RSA_PRIVATE_KEY_FILE= "D:/Key Store/private.key";
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
	public final static Map<String, String> IRIS_IMAGE_NAMES_MAP  = new HashMap<>();;

	/**
	 * Specifies the names for storing the documents in zip file
	 */
	public final static Map<String, String> DOCUMENT_TYPES_MAP = new HashMap<>();

	static {
		FINGERPRINT_IMAGE_NAMES_MAP.put("leftthumb", "LeftThumb");
		FINGERPRINT_IMAGE_NAMES_MAP.put("rightthumb", "RightThumb");
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
	
	public static String CONFIG_FILE_LOCATION = "D:/MOSIP/Client/Configuration/LoggerConfiguration.properties";
	public static String LOGGER_APPENDER_NAME = "appenderName";
	public static String LOGGER_APPEND ="append";
	public static String LOGGER_FILE_NAME = "fileName";
	public static String LOGGER_IMMEDIATE_FLUSH = "immediateFlush";
	public static String LOGGER_PRUDENT = "prudent";
	public static String LOGGER_FILE_NAME_PATTERN = "fileNamePattern";
	public static String LOGGER_MAX_FILE_SIZE ="maxFileSize";
	public static String LOGGER_MAX_HISTORY = "maxHistory";
	public static String LOGGER_TOTAL_CAP = "totalCap";
}
