package io.mosip.authentication.core.constant;

/**
 * The Class IdAuthCommonConstants has contants used throughout the modules
 *
 * @author Arun Bose S
 * @author Nagarjuna K
 */
public final class IdAuthCommonConstants {
	
	/** The Constant DEFAULT_EXACT_MATCH_VALUE. */
	public static final int DEFAULT_EXACT_MATCH_VALUE = 100;
	
	/** The Constant DEFAULT_PARTIAL_MATCH_VALUE. */
	public static final int DEFAULT_PARTIAL_MATCH_VALUE = DEFAULT_EXACT_MATCH_VALUE;

	public static final String DEFAULT_HOST_NAME = "localhost";

	public static final String DEFAULT_HOST_ADDRESS = "127.0.0.1";

	public static final String DEVICE_PURPOSE_AUTH = "AUTH";

	public static final String DIGITAL_ID_PREFIX = "digitalId/";

	public static final String DEVICE_PROVIDER_ID = "deviceProviderId";

	public static final String DP_ID = "dpId";

	public static final String DEVICE_PROVIDER = "deviceProvider";

	public static final String DP = "dp";

	public static final String PURPOSE = "purpose";
	
	public static final String PHOTO = "photo";

	public static final String PARTNER_CERTIFICATE = "PARTNER_CERTIFICATE";

	public static final boolean CONSUME_VID_DEFAULT = true;

	public static final String IDENTITY_DATA = "IDENTITY_DATA";
	
	public static final String IDENTITY_INFO = "IDENTITY_INFO";

	public static final String BDB_DEAULT_PROCESSED_LEVEL = "Raw";
	
	public static final String KYC_LANGUAGES = "KYC_LANGUAGES";
  
	public static final String TOKEN = "TOKEN";

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

	public static final String UNKNOWN = "UNKNOWN";
	
	public static final String UNKNOWN_BIO = "UNKNOWN";

	public static final String UNKNOWN_COUNT_PLACEHOLDER = "%s";

	public static final String TRANSACTION_ID = "transactionID";

	public static final String REQ_TIME = "requestTime";

	public static final String MISSING_INPUT_PARAMETER = "MISSING_INPUT_PARAMETER - ";

	public static final String INVALID_INPUT_PARAMETER = "INVALID_INPUT_PARAMETER - ";

	public static final String VALIDATE = "VALIDATE";

	public static final String STATUS = "status";

	public static final String ID = "id";
	
	/** The Constant VERSION. */
	public static final String VERSION = "version";

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

	public static final String BIO_TXN_ID_PATH = "data/transactionId";

	public static final String NAME_SEC = "nameSec";

	public static final String NAME_PRI = "namePri";

	public static final String SECONDAY_LANG = "secondayLang";

	public static final String PRIMARY_LANG = "primaryLang";

	/** The Constant OTP. */
	public static final String OTP = "otp";

	/** The Constant DEMO. */
	public static final String DEMO = "demo";

	/** The Constant BIO. */
	public static final String BIO = "bio";

	public static final String INTERNAL_URL = "/internal";

	public static final String AUTH_TRANSACTIONS = "authTransactions";

	public static final String AUTH_TYPE = "authtypes";
	
	public static final String HOTLIST = "hotlist";

	public static final String BIOMETRICS = "biometrics";

	public static final String KEY_BINDED_TOKEN = "keyBindedTokens";
	
	public static final String DEMOGRAPHICS = "demographics";

	public static final String IDENTITY = "identity";

	public static final String UIN_CAPS = "UIN";

	public static final String UIN = "uin";

	public static final String VID = "vid";

	public static final String UIN_MODULO_SPLITTER = "_";

	public static final String DEFAULT_DOB_PATTERN = "yyyy/MM/dd";
	
	public static final String DEFAULT_DATE_OF_BIRTH_ATTRIBUTE = "dateOfBirth";

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
	public static final String KYC = "kyc";

	/** The Constant REQUEST_HMAC. */
	public static final String REQUEST_HMAC = "requestHMAC";

	/** The Constant MISPLICENSE_KEY. */
	public static final String MISPLICENSE_KEY = "misplicenseKey";

	/** The constant api_key */
	public static final String API_KEY = "apiKey";

	/** The Constant PARTNER_ID. */
	public static final String PARTNER_ID = "partnerId";

	/** The Constant MISP_ID. */
	public static final String MISP_ID = "mispId";

	/** The Constant POLICY_ID. */
	public static final String POLICY_ID = "policyId";

	/** The Constant ACTIVE_STATUS. */
	public static final String ACTIVE_STATUS = "active";

	/** The Constant USED_STATUS. */
	public static final String USED_STATUS = "used";

	/** The Constant EXPIRY_DT. */
	public static final String EXPIRY_DT = "expiryDt";

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

	public static final String CALLBACK = "credentialIssueanceCallback";

	public static final String PUBLICKEY = "publickey";

	public static final String ENCRYPT = "encrypt";

	public static final String DECRYPT = "decrypt";

	public static final String VERIFY = "verify";

	public static final String VALIDATESIGN = "validate";

	public static final String PHONE_NUMBER = "phone";

	public static final String EMAIL = "email";

	public static final String CREDENTIAL_SUBJECT = "credentialSubject";

	public static final long DEFAULT_REQUEST_TIME_ADJUSTMENT_SECONDS = 30L;

	public static final String PARTNER_UPDATED_EVENT_NAME = "partner_updated";

	public static final String PARTNER_API_KEY_UPDATED_EVENT_NAME = "partner_api_key_updated";

	public static final String POLICY_UPDATED_EVENT_NAME = "policy_updated";

	public static final String CA_CERT_EVENT = "ca_certificate";

	public static final String METADATA = "metadata";

	public static final String SIGNATURE = "signature";

	public static final String SERVICE_ACCOUNT = "service-account-";
	
	public static final String APIKEY_APPROVED = "apikey_approved";
	
	public static final String MISP_LICENSE_GENERATED = "misp_license_generated";
	
	public static final String MISP_LICENSE_UPDATED = "misp_license_updated";
	
	public static final String INDIVIDUAL_ID = "individualId";

	public static final String IDA = "IDA";

	public static final String NA ="NA";

	public static final String AUTH_STATUS = "authStatus";
	
	public static final String KYC_STATUS = "kycStatus";

	public static final String SUCCESS = "success";

	public static final String FAILURE = "failure";
	
	public static final String QUALITY_SCORE = "qualityScore";
	
	public static final String UIN_HASH_SALT = "uin_hash_salt";

	public static final String UTC_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public static final String DEFAULT_ID_ATTRIBUTE_SEPARATOR_VALUE = " ";

	public static final String BIO_SUBTYPE_SEPARATOR = " ";
	
	public static final String BIO_TYPE_SEPARATOR="_";
	
	public static final String LANG_CODE_SEPARATOR="_";
	
	public static final String MAPPING_CONFIG = "mappingConfig";

	public static final String ID_NAME = "idName";

	public static final String OIDC_CLIENT_CREATED = "oidc_client_created";

	public static final String OIDC_CLIENT_UPDATED = "oidc_client_updated";

	public static final String KYC_TOKEN = "kycToken";

	public static final String CONSENT_OBTAINED = "consentObtained";

	public static final String SUBJECT = "sub";
	
	public static final String CLAIMS_LANG_SEPERATOR = "#";

	public static final long DEFAULT_KYC_TOKEN_EXPIRE_TIME_ADJUSTMENT_IN_SECONDS = 300L;

	public static final String DEFAULT_KYC_EXCHANGE_DEFAULT_LANGUAGE = "eng"; 

	public static final String FACE_ISO_NUMBER = "ISO19794_5_2011";

	public static final String ACR_AMR = "acr_amr";
	
	public static final String AMR = "amr";

	public static final String ADDRESS_FORMATTED = "formatted";

	public static final String NO_TRANSACTION_ID = "NO_TRANSACTION_ID";

	public static final String  KYC_EXCHANGE_SUCCESS = "KycExchange status : true";

	public static final boolean KYC_AUTH_CONSUME_VID_DEFAULT = false;

	public static final boolean KYC_EXCHANGE_CONSUME_VID_DEFAULT = true;

	public static final boolean KEY_BINDING_CONSUME_VID_DEFAULT = false;

	public static final String IDENTITY_KEY_BINDING_OBJECT = "identityKeyBinding";

	public static final String BINDING_PUBLIC_KEY = "publicKeyJWK";

	public static final String PUBLIC_KEY_EXPONENT_KEY = "e";

	public static final String PUBLIC_KEY_MODULUS_KEY = "n";

	public static final String AUTH_FACTOR_TYPE = "authFactorType";

	public static final String ALGORITHM_RSA = "RSA";

	public static final String NAME = "name";
	
	public static final String EMPTY = "";

	public static final String CERT_TP_AF_SEPERATOR = "-";

	public static final String CREDENTIAL_SUBJECT_ID = "credSubjectId";

	public static final String VC_FORMAT = "vcFormat";

	public static final String VC_AUTH_TOKEN = "vcAuthToken";

	public static final String VC_CREDENTIAL_TYPE = "credentialType";

	public static final boolean VCI_EXCHANGE_CONSUME_VID_DEFAULT = true;

	public static final Character COLON = ':';

	public static final String JWK_KEY_TYPE = "kty";

	public static final String VC_ID = "id";

	public static final String LANGUAGE_STRING = "language";

	public static final String VALUE_STRING = "value";

	public static final String VC_AT_CONTEXT = "@context";

	public static final String VC_TYPE = "type";

	public static final String VC_ISSUER = "issuer";

	public static final String VC_ISSUANCE_DATE = "issuanceDate";

	public static final String VC_PROOF_CREATED = "created";

	public static final String VC_PROOF_PURPOSE = "proofPurpose";

	public static final String VC_PROOF_TYPE = "type";

	public static final String VC_PROOF_VERIFICATION_METHOD = "verificationMethod";

	public static final String CREDENTIALSUBJECT = "credentialSubject";

	public static final String  VCI_EXCHANGE_SUCCESS = "VciExchange status : true";

	public static final String VC_CREDENTIAL_DEF = "credentialsDefinition";

	private IdAuthCommonConstants() {
	}
}
