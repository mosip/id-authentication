package io.mosip.authentication.core.constant;

/**
 * The Class IdAuthCommonConstants has contants used throughout the modules
 *
 * @author Arun Bose S
 * @author Nagarjuna K
 */
public final class IdAuthCommonConstants {

	private IdAuthCommonConstants() {

	}

	public static final String INTERNAL = "INTERNAL";

	/** The Constant SESSION_ID. */
	public static final String SESSION_ID = "sessionId";

	public static final String IDVID = "idvid";

	/** The Constant IDV_ID_TYPE. */
	public static final String IDV_ID_TYPE = "individualIdType";

	/** The Constant IDV_ID. */
	public static final String IDV_ID = "individualId";
	
	public static final String REQUEST = "request";
	
	public static final String HASH = "hash";
	
	public static final String UTF_8 = "utf-8";

	public static final String REQUESTEDAUTH = "requestedAuth";

	public static final String UNKNOWN_BIO = "UNKNOWN";
	
	public static final String UNKNOWN_COUNT_PLACEHOLDER = "%s";

	public static final String TRANSACTION_ID = "transactionID";

	public static final String REQ_TIME = "requestTime";

	public static final String MISSING_INPUT_PARAMETER = "MISSING_INPUT_PARAMETER - ";

	public static final String INVALID_INPUT_PARAMETER = "INVALID_INPUT_PARAMETER - ";

	public static final String VALIDATE = "VALIDATE";

	public static final String STATUS = "status";

	public static final String ID = "id";

	public static final String RESPONSE = "response";

	public static final String ERROR_CODE = "errorCode";

	public static final String ERRORS = "errors";

	/** The Constant KER_OTP_KEY_NOT_EXISTS_CODE. */
	public static final String KER_OTP_KEY_NOT_EXISTS_CODE = "KER-OTV-005";

	/** The Constant KER_PUBLIC_KEY_EXPIRED. */
	public static final String KER_PUBLIC_KEY_EXPIRED = "KER-KMS-003";

	/** The Constant KER_DECRYPTION_FAILURE. */
	public static final String KER_DECRYPTION_FAILURE = "KER-FSE-003";

	public static final String KER_USER_ID_NOTEXIST_ERRORCODE = "KER-ATH-401";

	public static final String KER_USER_ID_NOTEXIST_ERRORMSG = "User not found";

	public static final String DATA = "data";

	public static final String NAME_SEC = "nameSec";

	public static final String NAME_PRI = "namePri";

	public static final String SECONDAY_LANG = "secondayLang";

	public static final String PRIMARY_LANG = "primaryLang";

	public static final String OTP = "otp";

	public static final String INTERNAL_URL = "/internal";

	public static final String AUTH_TRANSACTIONS = "authTransactions";

	public static final String AUTH_TYPE = "authtypes";
	
	public static final String BIOMETRICS ="biometrics";
	
	public static final String IDENTITY ="identity";
	
	public static final String UIN_CAPS ="UIN";
	
	public static final String UIN = "uin";

	public static final String VID = "vid";
	
	public static final String UIN_MODULO_SPLITTER = "_";
	
	public static final String DOB_PATTERN = "yyyy/MM/dd";
	
	public static final String DEVICE_DOES_NOT_EXIST = "ADM-DPM-001";
	
	public static final String DEVICE_REVOKED_OR_RETIRED = "ADM-DPM-002";
	
	public static final String DEVICE_PROVIDER_NOT_EXIST = "ADM-DPM-003";
	
	public static final String DEVICE_PROVIDER_INACTIVE = "ADM-DPM-004";
	
	public static final String MDS_DOES_NOT_EXIST = "ADM-DPM-005";
	
	public static final String MDS_INACTIVE_STATE = "ADM-DPM-006";
	
	public static final String SW_ID_VERIFICATION_FAILED = "ADM-DPM-007";
	
	public static final String FIELD_VALIDATION_FAILED = "ADM-DPM-008";
	
	public static final String BIO_PATH = "request/biometrics/%s/%s";
	
	public static final String DIGITAL_ID = "digitalId";
	
	public static final String DIGITAL_ID_TYPE = "digitalId/type";

	/** The Constant DEFAULT_AAD_LAST_BYTES_NUM. */
	public static final int DEFAULT_AAD_LAST_BYTES_NUM = 16;

	/** The Constant DEFAULT_SALT_LAST_BYTES_NUM. */
	public static final int DEFAULT_SALT_LAST_BYTES_NUM = 12;

	/** The Constant TIMESTAMP. */
	public static final String TIMESTAMP = "timestamp";

	/** The Constant BIO_VALUE. */
	public static final String BIO_VALUE = "bioValue";
	
	/** The Constant BIO_TYPE. */
	public static final String BIO_TYPE = "bioType";
	
	public static final String BIO_SUB_TYPE = "bioSubType";
	
	/** The Constant SESSION_KEY. */
	public static final String SESSION_KEY = "sessionKey";
	
	public static final String REQUEST_BIOMETRICS_PARAM = REQUEST + "/" + BIOMETRICS;

	/** The Constant BIO_DATA_INPUT_PARAM. */
	public static final String BIO_DATA_INPUT_PARAM = REQUEST_BIOMETRICS_PARAM + "/%s/" + DATA;
	
	public static final String BIO_SESSIONKEY_INPUT_PARAM = REQUEST_BIOMETRICS_PARAM + "/%s/" + SESSION_KEY;

	/** The Constant HASH_INPUT_PARAM. */
	public static final String HASH_INPUT_PARAM = REQUEST_BIOMETRICS_PARAM + "/%s/" + HASH;
	
	public static final String BIO_VALUE_INPUT_PARAM = BIO_DATA_INPUT_PARAM + "/" + BIO_VALUE;
	
	public static final String BIO_TIMESTAMP_INPUT_PARAM = BIO_DATA_INPUT_PARAM + "/" + TIMESTAMP;
	
	public static final String BIO_DIGITALID_INPUT_PARAM = BIO_DATA_INPUT_PARAM + "/" + DIGITAL_ID;
	
	public static final String BIO_TYPE_INPUT_PARAM = BIO_DATA_INPUT_PARAM + "/" + BIO_TYPE;
	
	public static final String BIO_SUB_TYPE_INPUT_PARAM = BIO_DATA_INPUT_PARAM + "/" + BIO_SUB_TYPE;
	
	public static final String BIO_DIGITALID_INPUT_PARAM_TYPE = BIO_DATA_INPUT_PARAM + "/" + DIGITAL_ID_TYPE;
	
	
	/** The Constant EKYC. */
	public static final String EKYC = "ekyc";

	/** The Constant REQUEST_HMAC. */
	public static final String REQUEST_HMAC = "requestHMAC";

	/** The Constant MISPLICENSE_KEY. */
	public static final String MISPLICENSE_KEY = "misplicenseKey";

	/** The constant api_key */
	public static final String API_KEY="apiKey";

	/** The Constant PARTNER_ID. */
	public static final String PARTNER_ID = "partnerId";

	/** The Constant MISP_ID. */
	public static final String MISP_ID = "mispId";

	/** The Constant POLICY_ID. */
	public static final String POLICY_ID = "policyId";

	/** The Constant ACTIVE_STATUS. */
	public static final String ACTIVE_STATUS = "active";

	/** The Constant EXPIRY_DT. */
	public static final String EXPIRY_DT = "expiryDt";

	/** The Constant KYC. */
	public static final String KYC = "kyc";

	/** The Constant SESSION_KEY. */
	public static final String REQUEST_SESSION_KEY = "requestSessionKey";

	

	/** The Constant METHOD_REQUEST_SYNC. */
	public static final String METHOD_REQUEST_SYNC = "requestSync";

	/** The Constant METHOD_HANDLE_STATUS_ERROR. */
	public static final String METHOD_HANDLE_STATUS_ERROR = "handleStatusError";

	/** The Constant PREFIX_RESPONSE. */
	public static final String PREFIX_RESPONSE = "Response : ";

	/** The Constant PREFIX_REQUEST. */
	public static final String PREFIX_REQUEST = "Request : ";

	/** The Constant METHOD_REQUEST_ASYNC. */
	public static final String METHOD_REQUEST_ASYNC = "requestAsync";

	/** The Constant CLASS_REST_HELPER. */
	public static final String CLASS_REST_HELPER = "RestHelper";

	/** The Constant THROWING_REST_SERVICE_EXCEPTION. */
	public static final String THROWING_REST_SERVICE_EXCEPTION = "Throwing RestServiceException";

	/** The Constant REQUEST_SYNC_RUNTIME_EXCEPTION. */
	public static final String REQUEST_SYNC_RUNTIME_EXCEPTION = "requestSync-RuntimeException";
	
	public static final String NOTIFY = "notify";
	
	public static final String PUBLICKEY = "publickey";
	
	public static final String ENCRYPT = "encrypt";
	
	public static final String DECRYPT = "decrypt";
	
	public static final String VERIFY = "verify";
	
	public static final String VALIDATESIGN = "validate";
}

