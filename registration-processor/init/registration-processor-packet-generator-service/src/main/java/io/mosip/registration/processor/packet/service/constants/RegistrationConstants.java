package io.mosip.registration.processor.packet.service.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class contains the constants used in Registration application.
 *
 * @author Sowmya
 * @since 1.0.0
 */
public class RegistrationConstants {

	/**
	 * private constructor.
	 */
	private RegistrationConstants() {

	}

	/** The Constant LOCAL_LANGUAGE. */
	public static final String LOCAL_LANGUAGE = "LocalLanguage";

	/** The Constant PRIMARY_LANGUAGE. */
	public static final String PRIMARY_LANGUAGE = "mosip.primary-language";

	/** The Constant SECONDARY_LANGUAGE. */
	public static final String SECONDARY_LANGUAGE = "mosip.secondary-language";

	/** The Constant LANGUAGE_ENGLISH. */
	public static final String LANGUAGE_ENGLISH = "english";

	/** The Constant LANGUAGE_ARABIC. */
	public static final String LANGUAGE_ARABIC = "arabic";

	/** The Constant CHILD. */
	public static final String CHILD = "Child";

	/** The Constant ADULT. */
	public static final String ADULT = "Adult";

	/** The Constant AGE_DATEPICKER_CONTENT. */
	public static final String AGE_DATEPICKER_CONTENT = "ageDatePickerContent";

	/** The Constant AES_KEY_CIPHER_SPLITTER. */
	// AES Encryption Constants
	public static final String AES_KEY_CIPHER_SPLITTER = "aes.keySplitter";

	/** The Constant PACKET_STORE_LOCATION. */
	// Packet Store Location Constants
	public static final String PACKET_STORE_LOCATION = "PACKET_STORE_LOCATION";

	/** The Constant ZIP_FILE_EXTENSION. */
	// Packet Creation Constants
	public static final String ZIP_FILE_EXTENSION = ".zip";

	/** The Constant DEMOGRPAHIC_JSON_NAME. */
	public static final String DEMOGRPAHIC_JSON_NAME = "ID.json";

	/** The Constant PACKET_META_JSON_NAME. */
	public static final String PACKET_META_JSON_NAME = "packet_meta_info.json";

	/** The Constant PACKET_DATA_HASH_FILE_NAME. */
	public static final String PACKET_DATA_HASH_FILE_NAME = "packet_data_hash.txt";

	/** The Constant PACKET_OSI_HASH_FILE_NAME. */
	public static final String PACKET_OSI_HASH_FILE_NAME = "packet_osi_hash.txt";

	/** The Constant AUDIT_JSON_FILE. */
	public static final String AUDIT_JSON_FILE = "audit";

	/** The Constant JSON_FILE_EXTENSION. */
	public static final String JSON_FILE_EXTENSION = ".json";

	/** The Constant ACK_RECEIPT. */
	public static final String ACK_RECEIPT = "RegistrationAcknowledgement";

	/** The Constant APPLICANT_BIO_CBEFF_FILE_NAME. */
	public static final String APPLICANT_BIO_CBEFF_FILE_NAME = "applicant_bio_CBEFF.xml";

	/** The Constant OFFICER_BIO_CBEFF_FILE_NAME. */
	public static final String OFFICER_BIO_CBEFF_FILE_NAME = "officer_bio_CBEFF.xml";

	/** The Constant SUPERVISOR_BIO_CBEFF_FILE_NAME. */
	public static final String SUPERVISOR_BIO_CBEFF_FILE_NAME = "supervisor_bio_CBEFF.xml";

	/** The Constant INTRODUCER_BIO_CBEFF_FILE_NAME. */
	public static final String INTRODUCER_BIO_CBEFF_FILE_NAME = "introducer_bio_CBEFF.xml";

	/** The Constant MAX_REG_PACKET_SIZE_IN_MB. */
	public static final String MAX_REG_PACKET_SIZE_IN_MB = "MAX_REG_PACKET_SIZE";

	/** The Constant INDIVIDUAL. */
	public static final String INDIVIDUAL = "INDIVIDUAL";

	/** The Constant INTRODUCER. */
	public static final String INTRODUCER = "INTRODUCER";

	/** The Constant CBEFF_BIR_UUIDS_MAP_NAME. */
	public static final String CBEFF_BIR_UUIDS_MAP_NAME = "CBEFF_BIR_UUIDS";

	/** The Constant RSA. */
	public static final String RSA = "mosip.kernel.keygenerator.asymmetric-algorithm-name";

	/** The Constant XML_FILE_FORMAT. */
	public static final String XML_FILE_FORMAT = ".xml";

	/** The Constant CBEFF_FILE_FORMAT. */
	public static final String CBEFF_FILE_FORMAT = "cbeff";

	/** The Constant APPLICATION_ID. */
	// Logger - Constants
	public static final String APPLICATION_ID = "REG";

	/** The Constant APPLICATION_NAME. */
	public static final String APPLICATION_NAME = "REGISTRATION";

	/** The Constant AUDIT_APPLICATION_ID. */
	// Audit - Constants
	public static final String AUDIT_APPLICATION_ID = "registration.processor.audit.applicationId";

	/** The Constant AUDIT_APPLICATION_NAME. */
	public static final String AUDIT_APPLICATION_NAME = "registration.processor.audit.applicationName";

	/** The Constant AUDIT_DEFAULT_USER. */
	public static final String AUDIT_DEFAULT_USER = "NA";

	/** The Constant HOST_IP. */
	// Default Host IP Address and Name for Audit Logs
	public static final String HOST_IP = "registration.processor.audit.hostIP";

	/** The Constant HOST_NAME. */
	public static final String HOST_NAME = "registration.processor.audit.hostName";

	/** The Constant URL. */
	// OnlineConnectivity check
	public static final String URL = "http://localhost:8080/getTokenId";

	/** The Constant ALERT_INFORMATION. */
	// ALert related constants
	public static final String ALERT_INFORMATION = "INFORMATION";

	/** The Constant ALERT_WARNING. */
	public static final String ALERT_WARNING = "WARNING";

	/** The Constant ALERT. */
	public static final String ALERT = "ALERT";

	/** The Constant HTTPMETHOD. */
	// api related constant values
	public static final String HTTPMETHOD = "service.httpmethod";

	/** The Constant SERVICE_URL. */
	public static final String SERVICE_URL = "service.url";

	/** The Constant HEADERS. */
	public static final String HEADERS = "service.headers";

	/** The Constant RESPONSE_TYPE. */
	public static final String RESPONSE_TYPE = "service.responseType";

	/** The Constant REQUEST_TYPE. */
	public static final String REQUEST_TYPE = "service.requestType";

	/** The Constant AUTH_HEADER. */
	public static final String AUTH_HEADER = "service.authheader";

	/** The Constant AUTH_REQUIRED. */
	public static final String AUTH_REQUIRED = "service.authrequired";

	/** The Constant AUTH_TYPE. */
	public static final String AUTH_TYPE = "BASIC";

	/** The Constant OTP_GENERATOR_SERVICE_NAME. */
	// OTP Related Details
	public static final String OTP_GENERATOR_SERVICE_NAME = "otp_generator";

	/** The Constant USERNAME_KEY. */
	public static final String USERNAME_KEY = "key";

	/** The Constant OTP_GENERATED. */
	public static final String OTP_GENERATED = "otp";

	/** The Constant OTP_VALIDATOR_SERVICE_NAME. */
	public static final String OTP_VALIDATOR_SERVICE_NAME = "otp_validator";

	/** The Constant OTP_GENERATOR_RESPONSE_DTO. */
	public static final String OTP_GENERATOR_RESPONSE_DTO = "otpGeneratorResponseDTO";

	/** The Constant OTP_VALIDATOR_RESPONSE_DTO. */
	public static final String OTP_VALIDATOR_RESPONSE_DTO = "otpValidatorResponseDTO";

	/** The Constant TEMPLATE_ACKNOWLEDGEMENT. */
	// Velocity Template Generator Constants
	public static final String TEMPLATE_ACKNOWLEDGEMENT = "AckReceipt";

	/** The Constant TEMPLATE_PREVIEW. */
	public static final String TEMPLATE_PREVIEW = "Preview";

	/** The Constant TEMPLATE_QRCODE_SOURCE. */
	public static final String TEMPLATE_QRCODE_SOURCE = "QRCodeSource";

	/** The Constant TEMPLATE_UIN_UPDATE. */
	public static final String TEMPLATE_UIN_UPDATE = "UINUpdate";

	/** The Constant TEMPLATE_HEADER_TABLE. */
	public static final String TEMPLATE_HEADER_TABLE = "headerTable";

	/** The Constant TEMPLATE_UIN_HEADER_TABLE. */
	public static final String TEMPLATE_UIN_HEADER_TABLE = "uinHeaderTable";

	/** The Constant TEMPLATE_UIN_USER_LANG_LABEL. */
	public static final String TEMPLATE_UIN_USER_LANG_LABEL = "UINUserLangLabel";

	/** The Constant TEMPLATE_UIN_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_UIN_LOCAL_LANG_LABEL = "UINLocalLangLabel";

	/** The Constant TEMPLATE_UIN. */
	public static final String TEMPLATE_UIN = "UIN";

	/** The Constant TEMPLATE_RID_USER_LANG_LABEL. */
	public static final String TEMPLATE_RID_USER_LANG_LABEL = "RIDUserLangLabel";

	/** The Constant TEMPLATE_RID_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_RID_LOCAL_LANG_LABEL = "RIDLocalLangLabel";

	/** The Constant TEMPLATE_RID. */
	public static final String TEMPLATE_RID = "RID";

	/** The Constant TEMPLATE_DATE_USER_LANG_LABEL. */
	public static final String TEMPLATE_DATE_USER_LANG_LABEL = "DateUserLangLabel";

	/** The Constant TEMPLATE_DATE_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_DATE_LOCAL_LANG_LABEL = "DateLocalLangLabel";

	/** The Constant TEMPLATE_DATE. */
	public static final String TEMPLATE_DATE = "Date";

	/** The Constant TEMPLATE_PRE_REG_ID_USER_LANG_LABEL. */
	public static final String TEMPLATE_PRE_REG_ID_USER_LANG_LABEL = "PreRegIDUserLangLabel";

	/** The Constant TEMPLATE_PRE_REG_ID_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_PRE_REG_ID_LOCAL_LANG_LABEL = "PreRegIDLocalLangLabel";

	/** The Constant TEMPLATE_PRE_REG_ID. */
	public static final String TEMPLATE_PRE_REG_ID = "PreRegID";

	/** The Constant TEMPLATE_DEMO_INFO. */
	public static final String TEMPLATE_DEMO_INFO = "DemographicInfo";

	/** The Constant TEMPLATE_FULL_NAME_USER_LANG_LABEL. */
	public static final String TEMPLATE_FULL_NAME_USER_LANG_LABEL = "FullNameUserLangLabel";

	/** The Constant TEMPLATE_FULL_NAME_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_FULL_NAME_LOCAL_LANG_LABEL = "FullNameLocalLangLabel";

	/** The Constant TEMPLATE_FULL_NAME. */
	public static final String TEMPLATE_FULL_NAME = "FullName";

	/** The Constant TEMPLATE_FULL_NAME_LOCAL_LANG. */
	public static final String TEMPLATE_FULL_NAME_LOCAL_LANG = "FullNameLocalLang";

	/** The Constant TEMPLATE_GENDER_USER_LANG_LABEL. */
	public static final String TEMPLATE_GENDER_USER_LANG_LABEL = "GenderUserLangLabel";

	/** The Constant TEMPLATE_GENDER_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_GENDER_LOCAL_LANG_LABEL = "GenderLocalLangLabel";

	/** The Constant TEMPLATE_GENDER. */
	public static final String TEMPLATE_GENDER = "Gender";

	/** The Constant TEMPLATE_GENDER_LOCAL_LANG. */
	public static final String TEMPLATE_GENDER_LOCAL_LANG = "GenderLocalLang";

	/** The Constant TEMPLATE_DOB_USER_LANG_LABEL. */
	public static final String TEMPLATE_DOB_USER_LANG_LABEL = "DOBUserLangLabel";

	/** The Constant TEMPLATE_DOB_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_DOB_LOCAL_LANG_LABEL = "DOBLocalLangLabel";

	/** The Constant TEMPLATE_DOB. */
	public static final String TEMPLATE_DOB = "DOB";

	/** The Constant TEMPLATE_AGE_USER_LANG_LABEL. */
	public static final String TEMPLATE_AGE_USER_LANG_LABEL = "AgeUserLangLabel";

	/** The Constant TEMPLATE_AGE_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_AGE_LOCAL_LANG_LABEL = "AgeLocalLangLabel";

	/** The Constant TEMPLATE_AGE. */
	public static final String TEMPLATE_AGE = "Age";

	/** The Constant TEMPLATE_YEARS_USER_LANG. */
	public static final String TEMPLATE_YEARS_USER_LANG = "YearsUserLang";

	/** The Constant TEMPLATE_YEARS_LOCAL_LANG. */
	public static final String TEMPLATE_YEARS_LOCAL_LANG = "YearsLocalLang";

	/** The Constant TEMPLATE_FOREIGNER_USER_LANG_LABEL. */
	public static final String TEMPLATE_FOREIGNER_USER_LANG_LABEL = "ForiegnerUserLangLabel";

	/** The Constant TEMPLATE_FOREIGNER_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_FOREIGNER_LOCAL_LANG_LABEL = "ForiegnerLocalLangLabel";

	/** The Constant TEMPLATE_RESIDENCE_STATUS. */
	public static final String TEMPLATE_RESIDENCE_STATUS = "ResidenceStatus";

	/** The Constant TEMPLATE_RESIDENCE_STATUS_LOCAL_LANG. */
	public static final String TEMPLATE_RESIDENCE_STATUS_LOCAL_LANG = "ResidenceStatusLocalLang";

	/** The Constant TEMPLATE_ADDRESS_LINE1_USER_LANG_LABEL. */
	public static final String TEMPLATE_ADDRESS_LINE1_USER_LANG_LABEL = "AddressLine1UserLangLabel";

	/** The Constant TEMPLATE_ADDRESS_LINE1_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_ADDRESS_LINE1_LOCAL_LANG_LABEL = "AddressLine1LocalLangLabel";

	/** The Constant TEMPLATE_ADDRESS_LINE1. */
	public static final String TEMPLATE_ADDRESS_LINE1 = "AddressLine1";

	/** The Constant TEMPLATE_ADDRESS_LINE1_LOCAL_LANG. */
	public static final String TEMPLATE_ADDRESS_LINE1_LOCAL_LANG = "AddressLine1LocalLang";

	/** The Constant TEMPLATE_ADDRESS_LINE2_USER_LANG_LABEL. */
	public static final String TEMPLATE_ADDRESS_LINE2_USER_LANG_LABEL = "AddressLine2UserLangLabel";

	/** The Constant TEMPLATE_ADDRESS_LINE2_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_ADDRESS_LINE2_LOCAL_LANG_LABEL = "AddressLine2LocalLangLabel";

	/** The Constant TEMPLATE_ADDRESS_LINE2. */
	public static final String TEMPLATE_ADDRESS_LINE2 = "AddressLine2";

	/** The Constant TEMPLATE_ADDRESS_LINE2_LOCAL_LANG. */
	public static final String TEMPLATE_ADDRESS_LINE2_LOCAL_LANG = "AddressLine2LocalLang";

	/** The Constant TEMPLATE_REGION_USER_LANG_LABEL. */
	public static final String TEMPLATE_REGION_USER_LANG_LABEL = "RegionUserLangLabel";

	/** The Constant TEMPLATE_REGION_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_REGION_LOCAL_LANG_LABEL = "RegionLocalLangLabel";

	/** The Constant TEMPLATE_REGION. */
	public static final String TEMPLATE_REGION = "Region";

	/** The Constant TEMPLATE_REGION_LOCAL_LANG. */
	public static final String TEMPLATE_REGION_LOCAL_LANG = "RegionLocalLang";

	/** The Constant TEMPLATE_PROVINCE_USER_LANG_LABEL. */
	public static final String TEMPLATE_PROVINCE_USER_LANG_LABEL = "ProvinceUserLangLabel";

	/** The Constant TEMPLATE_PROVINCE_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_PROVINCE_LOCAL_LANG_LABEL = "ProvinceLocalLangLabel";

	/** The Constant TEMPLATE_PROVINCE. */
	public static final String TEMPLATE_PROVINCE = "Province";

	/** The Constant TEMPLATE_PROVINCE_LOCAL_LANG. */
	public static final String TEMPLATE_PROVINCE_LOCAL_LANG = "ProvinceLocalLang";

	/** The Constant TEMPLATE_LOCAL_AUTHORITY_USER_LANG_LABEL. */
	public static final String TEMPLATE_LOCAL_AUTHORITY_USER_LANG_LABEL = "LocalAuthorityUserLangLabel";

	/** The Constant TEMPLATE_LOCAL_AUTHORITY_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_LOCAL_AUTHORITY_LOCAL_LANG_LABEL = "LocalAuthorityLocalLangLabel";

	/** The Constant TEMPLATE_LOCAL_AUTHORITY. */
	public static final String TEMPLATE_LOCAL_AUTHORITY = "LocalAuthority";

	/** The Constant TEMPLATE_LOCAL_AUTHORITY_LOCAL_LANG. */
	public static final String TEMPLATE_LOCAL_AUTHORITY_LOCAL_LANG = "LocalAuthorityLocalLang";

	/** The Constant TEMPLATE_MOBILE_USER_LANG_LABEL. */
	public static final String TEMPLATE_MOBILE_USER_LANG_LABEL = "MobileUserLangLabel";

	/** The Constant TEMPLATE_MOBILE_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_MOBILE_LOCAL_LANG_LABEL = "MobileLocalLangLabel";

	/** The Constant TEMPLATE_MOBILE. */
	public static final String TEMPLATE_MOBILE = "Mobile";

	/** The Constant TEMPLATE_POSTAL_CODE_USER_LANG_LABEL. */
	public static final String TEMPLATE_POSTAL_CODE_USER_LANG_LABEL = "PostalCodeUserLangLabel";

	/** The Constant TEMPLATE_POSTAL_CODE_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_POSTAL_CODE_LOCAL_LANG_LABEL = "PostalCodeLocalLangLabel";

	/** The Constant TEMPLATE_POSTAL_CODE. */
	public static final String TEMPLATE_POSTAL_CODE = "PostalCode";

	/** The Constant TEMPLATE_EMAIL_USER_LANG_LABEL. */
	public static final String TEMPLATE_EMAIL_USER_LANG_LABEL = "EmailUserLangLabel";

	/** The Constant TEMPLATE_EMAIL_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_EMAIL_LOCAL_LANG_LABEL = "EmailLocalLangLabel";

	/** The Constant TEMPLATE_EMAIL. */
	public static final String TEMPLATE_EMAIL = "Email";

	/** The Constant TEMPLATE_CNIE_NUMBER_USER_LANG_LABEL. */
	public static final String TEMPLATE_CNIE_NUMBER_USER_LANG_LABEL = "CNIEUserLangLabel";

	/** The Constant TEMPLATE_CNIE_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_CNIE_LOCAL_LANG_LABEL = "CNIELocalLangLabel";

	/** The Constant TEMPLATE_CNIE_NUMBER. */
	public static final String TEMPLATE_CNIE_NUMBER = "CNIE";

	/** The Constant TEMPLATE_WITH_PARENT. */
	public static final String TEMPLATE_WITH_PARENT = "WithParent";

	/** The Constant TEMPLATE_PARENT_NAME_USER_LANG_LABEL. */
	public static final String TEMPLATE_PARENT_NAME_USER_LANG_LABEL = "ParentNameUserLangLabel";

	/** The Constant TEMPLATE_PARENT_NAME_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_PARENT_NAME_LOCAL_LANG_LABEL = "ParentNameLocalLangLabel";

	/** The Constant TEMPLATE_PARENT_NAME. */
	public static final String TEMPLATE_PARENT_NAME = "ParentName";

	/** The Constant TEMPLATE_PARENT_UIN_USER_LANG_LABEL. */
	public static final String TEMPLATE_PARENT_UIN_USER_LANG_LABEL = "ParentUINUserLangLabel";

	/** The Constant TEMPLATE_PARENT_UIN_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_PARENT_UIN_LOCAL_LANG_LABEL = "ParentUINLocalLangLabel";

	/** The Constant TEMPLATE_PARENT_UIN. */
	public static final String TEMPLATE_PARENT_UIN = "ParentUIN";

	/** The Constant TEMPLATE_PARENT_NAME_LOCAL_LANG. */
	public static final String TEMPLATE_PARENT_NAME_LOCAL_LANG = "ParentNameLocalLang";

	/** The Constant TEMPLATE_DOCUMENTS_USER_LANG_LABEL. */
	public static final String TEMPLATE_DOCUMENTS_USER_LANG_LABEL = "DocumentsUserLangLabel";

	/** The Constant TEMPLATE_DOCUMENTS_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_DOCUMENTS_LOCAL_LANG_LABEL = "DocumentsLocalLangLabel";

	/** The Constant TEMPLATE_DOCUMENTS. */
	public static final String TEMPLATE_DOCUMENTS = "Documents";

	/** The Constant TEMPLATE_DOCUMENTS_LOCAL_LANG. */
	public static final String TEMPLATE_DOCUMENTS_LOCAL_LANG = "DocumentsLocalLang";

	/** The Constant TEMPLATE_BIOMETRICS_USER_LANG_LABEL. */
	public static final String TEMPLATE_BIOMETRICS_USER_LANG_LABEL = "BiometricsUserLangLabel";

	/** The Constant TEMPLATE_BIOMETRICS_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_BIOMETRICS_LOCAL_LANG_LABEL = "BiometricsLocalLangLabel";

	/** The Constant TEMPLATE_BIOMETRICS_CAPTURED_USER_LANG_LABEL. */
	public static final String TEMPLATE_BIOMETRICS_CAPTURED_USER_LANG_LABEL = "BiometricsCapturedUserLangLabel";

	/** The Constant TEMPLATE_BIOMETRICS_CAPTURED_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_BIOMETRICS_CAPTURED_LOCAL_LANG_LABEL = "BiometricsCapturedLocalLangLabel";

	/** The Constant TEMPLATE_BIOMETRICS_CAPTURED. */
	public static final String TEMPLATE_BIOMETRICS_CAPTURED = "Biometrics";

	/** The Constant TEMPLATE_BIOMETRICS_CAPTURED_LOCAL_LANG. */
	public static final String TEMPLATE_BIOMETRICS_CAPTURED_LOCAL_LANG = "BiometricsLocalLang";

	/** The Constant TEMPLATE_WITHOUT_EXCEPTION. */
	public static final String TEMPLATE_WITHOUT_EXCEPTION = "WithoutException";

	/** The Constant TEMPLATE_WITH_EXCEPTION. */
	public static final String TEMPLATE_WITH_EXCEPTION = "WithException";

	/** The Constant TEMPLATE_EXCEPTION_PHOTO_USER_LANG_LABEL. */
	public static final String TEMPLATE_EXCEPTION_PHOTO_USER_LANG_LABEL = "ExceptionPhotoUserLangLabel";

	/** The Constant TEMPLATE_EXCEPTION_PHOTO_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_EXCEPTION_PHOTO_LOCAL_LANG_LABEL = "ExceptionPhotoLocalLangLabel";

	/** The Constant TEMPLATE_LEFT_EYE_USER_LANG_LABEL. */
	public static final String TEMPLATE_LEFT_EYE_USER_LANG_LABEL = "LeftEyeUserLangLabel";

	/** The Constant TEMPLATE_LEFT_EYE_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_LEFT_EYE_LOCAL_LANG_LABEL = "LeftEyeLocalLangLabel";

	/** The Constant TEMPLATE_RIGHT_EYE_USER_LANG_LABEL. */
	public static final String TEMPLATE_RIGHT_EYE_USER_LANG_LABEL = "RightEyeUserLangLabel";

	/** The Constant TEMPLATE_RIGHT_EYE_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_RIGHT_EYE_LOCAL_LANG_LABEL = "RightEyeLocalLangLabel";

	/** The Constant TEMPLATE_EXCEPTION_IMAGE_SOURCE. */
	public static final String TEMPLATE_EXCEPTION_IMAGE_SOURCE = "ExceptionImageSource";

	/** The Constant TEMPLATE_LEFT_EYE. */
	public static final String TEMPLATE_LEFT_EYE = "LeftEye";

	/** The Constant TEMPLATE_EYE_IMAGE_SOURCE. */
	public static final String TEMPLATE_EYE_IMAGE_SOURCE = "EyeImageSource";

	/** The Constant TEMPLATE_RIGHT_EYE. */
	public static final String TEMPLATE_RIGHT_EYE = "RightEye";

	/** The Constant TEMPLATE_CAPTURED_LEFT_EYE. */
	public static final String TEMPLATE_CAPTURED_LEFT_EYE = "CapturedLeftEye";

	/** The Constant TEMPLATE_CAPTURED_RIGHT_EYE. */
	public static final String TEMPLATE_CAPTURED_RIGHT_EYE = "CapturedRightEye";

	/** The Constant TEMPLATE_LEFT_PALM_USER_LANG_LABEL. */
	public static final String TEMPLATE_LEFT_PALM_USER_LANG_LABEL = "LeftPalmUserLangLabel";

	/** The Constant TEMPLATE_LEFT_PALM_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_LEFT_PALM_LOCAL_LANG_LABEL = "LeftPalmLocalLangLabel";

	/** The Constant TEMPLATE_RIGHT_PALM_USER_LANG_LABEL. */
	public static final String TEMPLATE_RIGHT_PALM_USER_LANG_LABEL = "RightPalmUserLangLabel";

	/** The Constant TEMPLATE_RIGHT_PALM_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_RIGHT_PALM_LOCAL_LANG_LABEL = "RightPalmLocalLangLabel";

	/** The Constant TEMPLATE_THUMBS_USER_LANG_LABEL. */
	public static final String TEMPLATE_THUMBS_USER_LANG_LABEL = "ThumbsUserLangLabel";

	/** The Constant TEMPLATE_THUMBS_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_THUMBS_LOCAL_LANG_LABEL = "ThumbsLocalLangLabel";

	/** The Constant TEMPLATE_LEFT_PALM_IMAGE_SOURCE. */
	public static final String TEMPLATE_LEFT_PALM_IMAGE_SOURCE = "LeftPalmImageSource";

	/** The Constant TEMPLATE_RIGHT_PALM_IMAGE_SOURCE. */
	public static final String TEMPLATE_RIGHT_PALM_IMAGE_SOURCE = "RightPalmImageSource";

	/** The Constant TEMPLATE_THUMBS_IMAGE_SOURCE. */
	public static final String TEMPLATE_THUMBS_IMAGE_SOURCE = "ThumbsImageSource";

	/** The Constant TEMPLATE_LEFT_LITTLE_FINGER. */
	public static final String TEMPLATE_LEFT_LITTLE_FINGER = "leftLittle";

	/** The Constant TEMPLATE_LEFT_RING_FINGER. */
	public static final String TEMPLATE_LEFT_RING_FINGER = "leftRing";

	/** The Constant TEMPLATE_LEFT_MIDDLE_FINGER. */
	public static final String TEMPLATE_LEFT_MIDDLE_FINGER = "leftMiddle";

	/** The Constant TEMPLATE_LEFT_INDEX_FINGER. */
	public static final String TEMPLATE_LEFT_INDEX_FINGER = "leftIndex";

	/** The Constant TEMPLATE_LEFT_THUMB_FINGER. */
	public static final String TEMPLATE_LEFT_THUMB_FINGER = "leftThumb";

	/** The Constant TEMPLATE_RIGHT_LITTLE_FINGER. */
	public static final String TEMPLATE_RIGHT_LITTLE_FINGER = "rightLittle";

	/** The Constant TEMPLATE_RIGHT_RING_FINGER. */
	public static final String TEMPLATE_RIGHT_RING_FINGER = "rightRing";

	/** The Constant TEMPLATE_RIGHT_MIDDLE_FINGER. */
	public static final String TEMPLATE_RIGHT_MIDDLE_FINGER = "rightMiddle";

	/** The Constant TEMPLATE_RIGHT_INDEX_FINGER. */
	public static final String TEMPLATE_RIGHT_INDEX_FINGER = "rightIndex";

	/** The Constant TEMPLATE_RIGHT_THUMB_FINGER. */
	public static final String TEMPLATE_RIGHT_THUMB_FINGER = "rightThumb";

	/** The Constant TEMPLATE_CAPTURED_LEFT_SLAP. */
	public static final String TEMPLATE_CAPTURED_LEFT_SLAP = "CapturedLeftSlap";

	/** The Constant TEMPLATE_CAPTURED_RIGHT_SLAP. */
	public static final String TEMPLATE_CAPTURED_RIGHT_SLAP = "CapturedRightSlap";

	/** The Constant TEMPLATE_CAPTURED_THUMBS. */
	public static final String TEMPLATE_CAPTURED_THUMBS = "CapturedThumbs";

	/** The Constant TEMPLATE_MISSING_LEFT_FINGERS. */
	public static final String TEMPLATE_MISSING_LEFT_FINGERS = "MissingLeftFingers";

	/** The Constant TEMPLATE_LEFT_SLAP_EXCEPTION_USER_LANG. */
	public static final String TEMPLATE_LEFT_SLAP_EXCEPTION_USER_LANG = "LeftSlapExceptionUserLang";

	/** The Constant TEMPLATE_LEFT_SLAP_EXCEPTION_LOCAL_LANG. */
	public static final String TEMPLATE_LEFT_SLAP_EXCEPTION_LOCAL_LANG = "LeftSlapExceptionLocalLang";

	/** The Constant TEMPLATE_MISSING_RIGHT_FINGERS. */
	public static final String TEMPLATE_MISSING_RIGHT_FINGERS = "MissingRightFingers";

	/** The Constant TEMPLATE_RIGHT_SLAP_EXCEPTION_USER_LANG. */
	public static final String TEMPLATE_RIGHT_SLAP_EXCEPTION_USER_LANG = "RightSlapExceptionUserLang";

	/** The Constant TEMPLATE_RIGHT_SLAP_EXCEPTION_LOCAL_LANG. */
	public static final String TEMPLATE_RIGHT_SLAP_EXCEPTION_LOCAL_LANG = "RightSlapExceptionLocalLang";

	/** The Constant TEMPLATE_MISSING_THUMBS. */
	public static final String TEMPLATE_MISSING_THUMBS = "MissingThumbs";

	/** The Constant TEMPLATE_THUMBS_EXCEPTION_USER_LANG. */
	public static final String TEMPLATE_THUMBS_EXCEPTION_USER_LANG = "ThumbsExceptionUserLang";

	/** The Constant TEMPLATE_THUMBS_EXCEPTION_LOCAL_LANG. */
	public static final String TEMPLATE_THUMBS_EXCEPTION_LOCAL_LANG = "ThumbsExceptionLocalLang";

	/** The Constant TEMPLATE_RO_IMAGE_SOURCE. */
	public static final String TEMPLATE_RO_IMAGE_SOURCE = "ROImageSource";

	/** The Constant TEMPLATE_RO_NAME_USER_LANG_LABEL. */
	public static final String TEMPLATE_RO_NAME_USER_LANG_LABEL = "RONameUserLangLabel";

	/** The Constant TEMPLATE_RO_NAME_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_RO_NAME_LOCAL_LANG_LABEL = "RONameLocalLangLabel";

	/** The Constant TEMPLATE_RO_NAME. */
	public static final String TEMPLATE_RO_NAME = "ROName";

	/** The Constant TEMPLATE_RO_NAME_LOCAL_LANG. */
	public static final String TEMPLATE_RO_NAME_LOCAL_LANG = "RONameLocalLang";

	/** The Constant TEMPLATE_REG_CENTER_USER_LANG_LABEL. */
	public static final String TEMPLATE_REG_CENTER_USER_LANG_LABEL = "RegCenterUserLangLabel";

	/** The Constant TEMPLATE_REG_CENTER_LOCAL_LANG_LABEL. */
	public static final String TEMPLATE_REG_CENTER_LOCAL_LANG_LABEL = "RegCenterLocalLangLabel";

	/** The Constant TEMPLATE_REG_CENTER. */
	public static final String TEMPLATE_REG_CENTER = "RegCenter";

	/** The Constant TEMPLATE_REG_CENTER_LOCAL_LANG. */
	public static final String TEMPLATE_REG_CENTER_LOCAL_LANG = "RegCenterLocalLang";

	/** The Constant TEMPLATE_PHOTO_USER_LANG. */
	public static final String TEMPLATE_PHOTO_USER_LANG = "PhotoUserLang";

	/** The Constant TEMPLATE_PHOTO_LOCAL_LANG. */
	public static final String TEMPLATE_PHOTO_LOCAL_LANG = "PhotoLocalLang";

	/** The Constant TEMPLATE_APPLICANT_IMAGE_SOURCE. */
	public static final String TEMPLATE_APPLICANT_IMAGE_SOURCE = "ApplicantImageSource";

	/** The Constant TEMPLATE_DATE_FORMAT. */
	public static final String TEMPLATE_DATE_FORMAT = "dd/MM/yyyy";

	/** The Constant TEMPLATE_JPG_IMAGE_ENCODING. */
	public static final String TEMPLATE_JPG_IMAGE_ENCODING = "data:image/jpg;base64,";

	/** The Constant TEMPLATE_PNG_IMAGE_ENCODING. */
	public static final String TEMPLATE_PNG_IMAGE_ENCODING = "data:image/png;base64,";

	/** The Constant TEMPLATE_CROSS_MARK. */
	public static final String TEMPLATE_CROSS_MARK = "&#10008;";

	/** The Constant TEMPLATE_EYE_IMAGE_PATH. */
	public static final String TEMPLATE_EYE_IMAGE_PATH = "/images/Eye.png";

	/** The Constant TEMPLATE_LEFT_SLAP_IMAGE_PATH. */
	public static final String TEMPLATE_LEFT_SLAP_IMAGE_PATH = "/images/leftHand.png";

	/** The Constant TEMPLATE_RIGHT_SLAP_IMAGE_PATH. */
	public static final String TEMPLATE_RIGHT_SLAP_IMAGE_PATH = "/images/rightHand.png";

	/** The Constant TEMPLATE_THUMBS_IMAGE_PATH. */
	public static final String TEMPLATE_THUMBS_IMAGE_PATH = "/images/thumbs.png";

	/** The Constant TEMPLATE_STYLE_HIDE_PROPERTY. */
	public static final String TEMPLATE_STYLE_HIDE_PROPERTY = "style='display:none;'";

	/** The Constant TEMPLATE_RIGHT_MARK. */
	public static final String TEMPLATE_RIGHT_MARK = "&#10003;";

	/** The Constant TEMPLATE_FINGERPRINTS_CAPTURED. */
	public static final String TEMPLATE_FINGERPRINTS_CAPTURED = "FingerprintsCaptured";

	/** The Constant TEMPLATE_IMPORTANT_GUIDELINES. */
	public static final String TEMPLATE_IMPORTANT_GUIDELINES = "ImportantGuidelines";

	/** The Constant TEMPLATE_NAME. */
	public static final String TEMPLATE_NAME = "Acknowledgement Template";

	/** The Constant TEMPLATE_RESIDENT_NAME. */
	public static final String TEMPLATE_RESIDENT_NAME = "ResidentName";

	/** The Constant TEMPLATE_RO_IMAGE. */
	public static final String TEMPLATE_RO_IMAGE = "ROImage";

	/** The Constant TEMPLATE_MODIFY_IMAGE_PATH. */
	public static final String TEMPLATE_MODIFY_IMAGE_PATH = "/images/Modify.png";

	/** The Constant TEMPLATE_MODIFY_IMAGE_SOURCE. */
	public static final String TEMPLATE_MODIFY_IMAGE_SOURCE = "ModifyImageSource";

	/** The Constant TEMPLATE_MODIFY. */
	public static final String TEMPLATE_MODIFY = "Modify";

	/** The Constant TEMPLATE_ENCODING. */
	public static final String TEMPLATE_ENCODING = "UTF-8";

	/** The Constant TEMPLATE_FACE_CAPTURE_ENABLED. */
	public static final String TEMPLATE_FACE_CAPTURE_ENABLED = "FaceCaptureEnabled";

	/** The Constant TEMPLATE_DOCUMENTS_ENABLED. */
	public static final String TEMPLATE_DOCUMENTS_ENABLED = "DocumentsEnabled";

	/** The Constant TEMPLATE_BIOMETRICS_ENABLED. */
	public static final String TEMPLATE_BIOMETRICS_ENABLED = "BiometricsEnabled";

	/** The Constant TEMPLATE_IRIS_ENABLED. */
	public static final String TEMPLATE_IRIS_ENABLED = "IrisEnabled";

	/** The Constant TEMPLATE_IRIS_DISABLED. */
	public static final String TEMPLATE_IRIS_DISABLED = "IrisDisabled";

	/** The Constant TEMPLATE_LEFT_EYE_CAPTURED. */
	public static final String TEMPLATE_LEFT_EYE_CAPTURED = "leftEyeCaptured";

	/** The Constant TEMPLATE_RIGHT_EYE_CAPTURED. */
	public static final String TEMPLATE_RIGHT_EYE_CAPTURED = "rightEyeCaptured";

	/** The Constant TEMPLATE_LEFT_SLAP_CAPTURED. */
	public static final String TEMPLATE_LEFT_SLAP_CAPTURED = "leftSlapCaptured";

	/** The Constant TEMPLATE_RIGHT_SLAP_CAPTURED. */
	public static final String TEMPLATE_RIGHT_SLAP_CAPTURED = "rightSlapCaptured";

	/** The Constant TEMPLATE_THUMBS_CAPTURED. */
	public static final String TEMPLATE_THUMBS_CAPTURED = "thumbsCaptured";

	/** The Constant MODIFY_DEMO_INFO. */
	public static final String MODIFY_DEMO_INFO = "modifyDemographicInfo";

	/** The Constant MODIFY_DOCUMENTS. */
	public static final String MODIFY_DOCUMENTS = "modifyDocuments";

	/** The Constant MODIFY_BIOMETRICS. */
	public static final String MODIFY_BIOMETRICS = "modifyBiometrics";

	/** The Constant CLICK. */
	public static final String CLICK = "click";

	/** The Constant WEB_CAMERA_IMAGE_TYPE. */
	// Web Camera Constants
	public static final String WEB_CAMERA_IMAGE_TYPE = "jpg";

	/** The Constant APPLICANT_PHOTOGRAPH_NAME. */
	public static final String APPLICANT_PHOTOGRAPH_NAME = "Applicant Photograph.jpg";

	/** The Constant EXCEPTION_PHOTOGRAPH_NAME. */
	public static final String EXCEPTION_PHOTOGRAPH_NAME = "Exception Photograph.jpg";

	/** The Constant APPLICANT_IMAGE. */
	public static final String APPLICANT_IMAGE = "Applicant Image";

	/** The Constant EXCEPTION_IMAGE. */
	public static final String EXCEPTION_IMAGE = "Exception Image";

	/** The Constant APPLICANT_PHOTO_PANE. */
	public static final String APPLICANT_PHOTO_PANE = "applicantPhoto";

	/** The Constant EXCEPTION_PHOTO_PANE. */
	public static final String EXCEPTION_PHOTO_PANE = "exceptionPhoto";

	/** The Constant WEB_CAMERA_PAGE_TITLE. */
	public static final String WEB_CAMERA_PAGE_TITLE = "Applicant Biometrics";

	/** The Constant ACKNOWLEDGEMENT_FORM_TITLE. */
	// Acknowledement Form
	public static final String ACKNOWLEDGEMENT_FORM_TITLE = "Registration Acknowledgement";

	/** The Constant DEMOGRAPHIC_DETAILS_LOGO. */
	// logos for new registration
	public static final String DEMOGRAPHIC_DETAILS_LOGO = "file:src/main/resources/images/Pre-Registration.png";

	/** The Constant APPLICANT_BIOMETRICS_LOGO. */
	public static final String APPLICANT_BIOMETRICS_LOGO = "file:src/main/resources/images/ApplicantBiometrics.png";

	/** The Constant OPERATOR_AUTHENTICATION_LOGO. */
	public static final String OPERATOR_AUTHENTICATION_LOGO = "file:src/main/resources/images/OperatorAuthentication.png";

	/** The Constant PACKET_CREATION_EXP_CODE. */
	// Exception Code for Components
	public static final String PACKET_CREATION_EXP_CODE = "PCC-";

	/** The Constant PACKET_UPLOAD_EXP_CODE. */
	public static final String PACKET_UPLOAD_EXP_CODE = "PAU-";

	/** The Constant REG_ACK_EXP_CODE. */
	public static final String REG_ACK_EXP_CODE = "ACK-";

	/** The Constant DEVICE_ONBOARD_EXP_CODE. */
	public static final String DEVICE_ONBOARD_EXP_CODE = "DVO-";

	/** The Constant SYNC_JOB_EXP_CODE. */
	public static final String SYNC_JOB_EXP_CODE = "SYN-";

	/** The Constant USER_REG_IRIS_CAPTURE_EXP_CODE. */
	public static final String USER_REG_IRIS_CAPTURE_EXP_CODE = "IRC-";

	/** The Constant USER_REG_FINGERPRINT_CAPTURE_EXP_CODE. */
	public static final String USER_REG_FINGERPRINT_CAPTURE_EXP_CODE = "FPC-";

	/** The Constant USER_REGISTRATION_EXP_CODE. */
	public static final String USER_REGISTRATION_EXP_CODE = "REG-";

	/** The Constant USER_REG_SCAN_EXP_CODE. */
	public static final String USER_REG_SCAN_EXP_CODE = "SCN-";

	/** The Constant MACHINE_MAPPING_CREATED. */
	// USER CLIENT MACHINE MAPPING
	public static final String MACHINE_MAPPING_CREATED = "created";

	/** The Constant MACHINE_MAPPING_UPDATED. */
	public static final String MACHINE_MAPPING_UPDATED = "updated";

	/** The Constant MACHINE_MAPPING_LOGGER_TITLE. */
	public static final String MACHINE_MAPPING_LOGGER_TITLE = "REGISTRATION - USER CLIENT MACHINE MAPPING";

	/** The Constant DEVICE_MAPPING_LOGGER_TITLE. */
	public static final String DEVICE_MAPPING_LOGGER_TITLE = "REGISTRATION - CENTER MACHINE DEVICE MAPPING";

	/** The Constant SYNC_TRANSACTION_DAO_LOGGER_TITLE. */
	public static final String SYNC_TRANSACTION_DAO_LOGGER_TITLE = "REGISTRATION-SYNC-TRANSACTION DAO";

	/** The Constant SYNC_JOB_CONTROL_DAO_LOGGER_TITLE. */
	public static final String SYNC_JOB_CONTROL_DAO_LOGGER_TITLE = "REGISTRATION-SYNC-JOB_CONTROL DAO";

	/** The Constant DEVICE_MAPPING_SUCCESS_CODE. */
	// CENTER MACHINE DEVICE MAPPING
	public static final String DEVICE_MAPPING_SUCCESS_CODE = "REG-DVO‌-001";

	/** The Constant DEVICE_MAPPING_ERROR_CODE. */
	public static final String DEVICE_MAPPING_ERROR_CODE = "REG-DVO‌-002";

	/** The Constant USER_MACHINE_MAPID. */
	// MAP ID
	public static final String USER_MACHINE_MAPID = "ListOfUserDTO";

	/** The Constant USER_ACTIVE. */
	// ACTIVE INACTIVE USER
	public static final String USER_ACTIVE = "Active";

	/** The Constant USER_IN_ACTIVE. */
	public static final String USER_IN_ACTIVE = "In-Active";

	// Upload Packet

	/** The Constant PACKET_UPLOAD_STATUS. */
	public static final List<String> PACKET_UPLOAD_STATUS = Arrays.asList("SYNCED", "EXPORTED", "RESEND", "E");

	/** The Constant PACKET_UPLOAD. */
	public static final String PACKET_UPLOAD = "packet_upload";

	/** The Constant PACKET_DUPLICATE. */
	public static final String PACKET_DUPLICATE = "duplicate";

	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 */
	public static final Set<String> getRoles() {
		return new HashSet<>(Arrays.asList("*"));
	}

	/** The Constant OPT_TO_REG_GEO_CAP_FREQ. */
	// opt to register constants
	public static final String OPT_TO_REG_GEO_CAP_FREQ = "GEO_CAP_FREQ";

	/** The Constant ICS_CODE_ONE. */
	public static final String ICS_CODE_ONE = "REG-ICS‌-001";

	/** The Constant ICS_CODE_TWO. */
	public static final String ICS_CODE_TWO = "REG-ICS‌-002";

	/** The Constant ICS_CODE_THREE. */
	public static final String ICS_CODE_THREE = "REG-ICS‌-003";

	/** The Constant ICS_CODE_FOUR. */
	public static final String ICS_CODE_FOUR = "REG-ICS‌-004";

	/** The Constant OPT_TO_REG_PAK_MAX_CNT_OFFLINE_FREQ. */
	public static final String OPT_TO_REG_PAK_MAX_CNT_OFFLINE_FREQ = "REG_PAK_MAX_CNT_OFFLINE_FREQ";

	/** The Constant OPT_TO_REG_EARTH_RADIUS. */
	public static final double OPT_TO_REG_EARTH_RADIUS = 6371000;

	/** The Constant OPT_TO_REG_METER_CONVERSN. */
	public static final double OPT_TO_REG_METER_CONVERSN = 1609.00;

	/** The Constant OPT_TO_REG_DIST_FRM_MACHN_TO_CENTER. */
	public static final String OPT_TO_REG_DIST_FRM_MACHN_TO_CENTER = "DIST_FRM_MACHN_TO_CENTER";

	/** The Constant ICS_CODE_FIVE. */
	public static final String ICS_CODE_FIVE = "REG-ICS‌-005";

	/** The Constant ICS_CODE_SIX. */
	public static final String ICS_CODE_SIX = "REG-ICS‌-006";

	/** The Constant ICS_CODE_SEVEN. */
	public static final String ICS_CODE_SEVEN = "REG-ICS‌-007";

	/** The Constant PAK_APPRVL_MAX_CNT. */
	public static final String PAK_APPRVL_MAX_CNT = "REG-ICS‌-008";

	/** The Constant PAK_APPRVL_MAX_TIME. */
	public static final String PAK_APPRVL_MAX_TIME = "REG-ICS‌-009";

	/** The Constant OPT_TO_REG_LAST_CAPTURED_TIME. */
	public static final String OPT_TO_REG_LAST_CAPTURED_TIME = "lastCapturedTime";

	/** The Constant LATITUDE. */
	public static final String LATITUDE = "latitude";

	/** The Constant OPT_TO_REG_MDS_J00001. */
	public static final String OPT_TO_REG_MDS_J00001 = "MDS_J00001";

	/** The Constant OPT_TO_REG_LCS_J00002. */
	public static final String OPT_TO_REG_LCS_J00002 = "LCS_J00002";

	/** The Constant OPT_TO_REG_PDS_J00003. */
	public static final String OPT_TO_REG_PDS_J00003 = "PDS_J00003";

	/** The Constant OPT_TO_REG_RSS_J00004. */
	public static final String OPT_TO_REG_RSS_J00004 = "RSS_J00004";

	/** The Constant OPT_TO_REG_RCS_J00005. */
	public static final String OPT_TO_REG_RCS_J00005 = "RCS_J00005";

	/** The Constant OPT_TO_REG_RPS_J00006. */
	public static final String OPT_TO_REG_RPS_J00006 = "RPS_J00006";

	/** The Constant OPT_TO_REG_URS_J00007. */
	public static final String OPT_TO_REG_URS_J00007 = "URS_J00007";

	/** The Constant OPT_TO_REG_POS_J00008. */
	public static final String OPT_TO_REG_POS_J00008 = "POS_J00008";

	/** The Constant OPT_TO_REG_LER_J00009. */
	public static final String OPT_TO_REG_LER_J00009 = "LER_J00009";

	/** The Constant OPT_TO_REG_RDJ_J00010. */
	public static final String OPT_TO_REG_RDJ_J00010 = "RDJ_J00010";

	/** The Constant OPT_TO_REG_RDJ_J00011. */
	public static final String OPT_TO_REG_RDJ_J00011 = "RDJ_J00011";

	/** The Constant OPT_TO_REG_ADJ_J00012. */
	public static final String OPT_TO_REG_ADJ_J00012 = "ADJ_J00012";

	/** The Constant OPT_TO_REG_DEL_001. */
	public static final String OPT_TO_REG_DEL_001 = "DEL_001";

	/** The Constant OPT_TO_REG_UDM_J00012. */
	public static final String OPT_TO_REG_UDM_J00012 = "UDM_J00012";

	/** The Constant GEO_CAP_FREQ. */
	public static final String GEO_CAP_FREQ = "GEO_CAP_FREQ";

	/** The Constant DIST_FRM_MACHN_TO_CENTER. */
	public static final String DIST_FRM_MACHN_TO_CENTER = "DIST_FRM_MACHN_TO_CENTER";

	/** The Constant REG_PAK_MAX_CNT_OFFLINE_FREQ. */
	public static final String REG_PAK_MAX_CNT_OFFLINE_FREQ = "REG_PAK_MAX_CNT_OFFLINE_FREQ";

	/** Packet Status Sync Constants. */
	public static final String PACKET_STATUS_SYNC_RESPONSE_ENTITY = "registrations";

	/** The Constant PACKET_STATUS_SYNC_SERVICE_NAME. */
	public static final String PACKET_STATUS_SYNC_SERVICE_NAME = "packet_status";

	/** The Constant PACKET_STATUS_READER_URL_PARAMETER. */
	public static final String PACKET_STATUS_READER_URL_PARAMETER = "request";

	/** The Constant PACKET_STATUS_READER_RESPONSE. */
	public static final String PACKET_STATUS_READER_RESPONSE = "response";

	/** The Constant PACKET_STATUS_READER_REGISTRATION_ID. */
	public static final String PACKET_STATUS_READER_REGISTRATION_ID = "registrationId";

	/** The Constant PACKET_STATUS_READER_STATUS_CODE. */
	public static final String PACKET_STATUS_READER_STATUS_CODE = "statusCode";

	/** The Constant PACKET_STATUS_CODE_PROCESSED. */
	public static final String PACKET_STATUS_CODE_PROCESSED = "processed";

	/** The Constant PACKET_STATUS_READER_ID. */
	public static final String PACKET_STATUS_READER_ID = "mosip.registration.status";

	/** The Constant PACKET_SYNC_STATUS_ID. */
	public static final String PACKET_SYNC_STATUS_ID = "mosip.registration.sync";

	/** The Constant PACKET_SYNC_VERSION. */
	public static final String PACKET_SYNC_VERSION = "1.0";

	/** The Constant BIOMETRIC_IMAGE. */
	public static final String BIOMETRIC_IMAGE = "Image";

	/** The Constant BIOMETRIC_TYPE. */
	public static final String BIOMETRIC_TYPE = "Type";

	/** The Constant PACKET_TYPE. */
	// Packet Upload
	public static final String PACKET_TYPE = "file";

	/** The Constant PACKET_STATUS_PRE_SYNC. */
	public static final String PACKET_STATUS_PRE_SYNC = "PRE_SYNC";

	/** The Constant PACKET_STATUS_SYNC_TYPE. */
	public static final String PACKET_STATUS_SYNC_TYPE = "NEW";

	/** The Constant ACKNOWLEDGEMENT_FILE. */
	public static final String ACKNOWLEDGEMENT_FILE = "_Ack";

	/** The Constant PACKET_SYNC_ERROR. */
	public static final String PACKET_SYNC_ERROR = "Error";

	/** The Constant RE_REGISTRATION_STATUS. */
	public static final String RE_REGISTRATION_STATUS = "Re-Register";

	/** The Constant PACKET_SYNC_REF_ID. */
	public static final String PACKET_SYNC_REF_ID = "packetsync";

	/** The Constant PACKET_UPLOAD_REF_ID. */
	public static final String PACKET_UPLOAD_REF_ID = "packetUpload";

	/** The Constant PACKET_UPLOAD_SUCCESS. */
	public static final String PACKET_UPLOAD_SUCCESS = "Uploaded";

	/** The Constant DEVICE_MANUFACTURER_NAME. */
	// Device On-boarding
	public static final String DEVICE_MANUFACTURER_NAME = "manufacturerName";

	/** The Constant DEVICE_MODEL_NAME. */
	public static final String DEVICE_MODEL_NAME = "modelName";

	/** The Constant DEVICE_SERIAL_NO. */
	public static final String DEVICE_SERIAL_NO = "serialNo";

	/** The Constant ONBOARD_AVAILABLE_DEVICES. */
	public static final String ONBOARD_AVAILABLE_DEVICES = "availableDevices";

	/** The Constant ONBOARD_MAPPED_DEVICES. */
	public static final String ONBOARD_MAPPED_DEVICES = "mappedDevices";

	/** The Constant ONBOARD_DEVICES_MAP. */
	public static final String ONBOARD_DEVICES_MAP = "onBoardDevicesMap";

	/** The Constant ONBOARD_DEVICES_REF_ID_TYPE. */
	public static final String ONBOARD_DEVICES_REF_ID_TYPE = "UserID";

	/** The Constant MACHINE_ID. */
	public static final String MACHINE_ID = "machineId";

	/** The Constant ONBOARD_DEVICES_MAP_UPDATED. */
	public static final String ONBOARD_DEVICES_MAP_UPDATED = "updatedDevicesMap";

	/** The Constant DEVICE_TYPES_ALL_OPTION. */
	public static final String DEVICE_TYPES_ALL_OPTION = "All";

	/** The Constant DEVICE_TYPE. */
	public static final String DEVICE_TYPE = "deviceType";

	/** The Constant DONGLE_SERIAL_NUMBER. */
	public static final String DONGLE_SERIAL_NUMBER = "dongleSerialNumber";

	/** The Constant ACKNOWLEDGEMENT_TEMPLATE. */
	// Template Name
	public static final String ACKNOWLEDGEMENT_TEMPLATE = "Ack Template";

	/** The Constant NOTIFICATION_TEMPLATE. */
	public static final String NOTIFICATION_TEMPLATE = "Notification Template";

	/** The Constant EMAIL_SUBJECT. */
	// Notification Service
	public static final String EMAIL_SUBJECT = "MOSIP REGISTRATION NOTIFICATION";

	/** The Constant EMAIL_SERVICE. */
	public static final String EMAIL_SERVICE = "email";

	/** The Constant SMS_SERVICE. */
	public static final String SMS_SERVICE = "sms";

	/** The Constant NOTIFICATION_SERVICE. */
	public static final String NOTIFICATION_SERVICE = "REGISTRATION - NOTIFICATION SERVICE ";

	/** The Constant MODE_OF_COMMUNICATION. */
	public static final String MODE_OF_COMMUNICATION = "mosip.registration.mode_of_communication";

	/** The Constant INVALID_LOGIN_COUNT. */
	// Global configuration parameters
	public static final String INVALID_LOGIN_COUNT = "INVALID_LOGIN_COUNT";

	/** The Constant INVALID_LOGIN_TIME. */
	public static final String INVALID_LOGIN_TIME = "INVALID_LOGIN_TIME";

	/** The Constant REGISTARTION_CENTER. */
	public static final String REGISTARTION_CENTER = "REGISTARTION_CENTER";

	/** The Constant CBEFF_ONLY_UNIQUE_TAGS. */
	public static final String CBEFF_ONLY_UNIQUE_TAGS = "CBEFF_ONLY_UNIQUE_TAGS";

	/** The Constant GLOBAL_CONFIG_TRUE_VALUE. */
	public static final String GLOBAL_CONFIG_TRUE_VALUE = "Y";

	/** The Constant REG_PAK_MAX_CNT_APPRV_LIMIT. */
	public static final String REG_PAK_MAX_CNT_APPRV_LIMIT = "REG_PAK_MAX_CNT_APPRV_LIMIT";

	/** The Constant REG_PAK_MAX_TIME_APPRV_LIMIT. */
	public static final String REG_PAK_MAX_TIME_APPRV_LIMIT = "REG_PAK_MAX_TIME_APPRV_LIMIT";

	/** The Constant FINGERPRINT_DISABLE_FLAG. */
	public static final String FINGERPRINT_DISABLE_FLAG = "FINGERPRINT_DISABLE_FLAG";

	/** The Constant IRIS_DISABLE_FLAG. */
	public static final String IRIS_DISABLE_FLAG = "IRIS_DISABLE_FLAG";

	/** The Constant FACE_DISABLE_FLAG. */
	public static final String FACE_DISABLE_FLAG = "FACE_DISABLE_FLAG";

	/** The Constant DOCUMENT_DISABLE_FLAG. */
	public static final String DOCUMENT_DISABLE_FLAG = "DOCUMENT_DISABLE_FLAG";

	/** The Constant JOB_TRIGGER_STARTED. */
	// Spring Batch-Jobs
	public static final String JOB_TRIGGER_STARTED = "Trigger started";

	/** The Constant JOB_TRIGGER_COMPLETED. */
	public static final String JOB_TRIGGER_COMPLETED = "Trigger completed";

	/** The Constant JOB_EXECUTION_STARTED. */
	public static final String JOB_EXECUTION_STARTED = "Execution started";

	/** The Constant JOB_EXECUTION_COMPLETED. */
	public static final String JOB_EXECUTION_COMPLETED = "Execution completed";

	/** The Constant JOB_EXECUTION_SUCCESS. */
	public static final String JOB_EXECUTION_SUCCESS = "Executed with success";

	/** The Constant JOB_EXECUTION_FAILURE. */
	public static final String JOB_EXECUTION_FAILURE = "Executed with failure";

	/** The Constant JOB_TRIGGER_MIS_FIRED. */
	public static final String JOB_TRIGGER_MIS_FIRED = "Trigger Mis-Fired";

	/** The Constant JOB_EXECUTION_REJECTED. */
	public static final String JOB_EXECUTION_REJECTED = "Execution Rejected";

	/** The Constant RETRIEVED_PRE_REG_ID. */
	public static final String RETRIEVED_PRE_REG_ID = "Retrieved Pre Registration";

	/** The Constant UNABLE_TO_RETRIEVE_PRE_REG_ID. */
	public static final String UNABLE_TO_RETRIEVE_PRE_REG_ID = "Unable to retrieve pre registration";

	/** The Constant JOB_TRIGGER_POINT_SYSTEM. */
	public static final String JOB_TRIGGER_POINT_SYSTEM = "System";

	/** The Constant JOB_TRIGGER_POINT_USER. */
	public static final String JOB_TRIGGER_POINT_USER = "User";

	/** The Constant JOB_SYNC_TO_SERVER. */
	public static final String JOB_SYNC_TO_SERVER = "Server";

	/** The Constant JOB_DETAIL. */
	public static final String JOB_DETAIL = "jobDetail";

	/** The Constant APPLICATION_CONTEXT. */
	public static final String APPLICATION_CONTEXT = "applicationContext";

	/** The Constant SYNC_TRANSACTION. */
	public static final String SYNC_TRANSACTION = "syncTransaction";

}
