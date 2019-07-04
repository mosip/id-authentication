package io.mosip.authentication.core.constant;

/**
 * This Class will provide constants for all Configured properties.
 * 
 * @author Prem Kumar
 *
 */
public final class IdAuthConfigKeyConstants {

	private IdAuthConfigKeyConstants() {

	}

	public static final String APPLICATION_ID = "application.id";
	public static final String API_VERSION = "ida.api.version";
	public static final String DATE_TIME_PATTERN = "datetime.pattern";
	public static final String REST_URI = ".rest.uri";
	public static final String REST_HTTP_METHOD = ".rest.httpMethod";
	public static final String REST_TIMEOUT = ".rest.timeout";
	public static final String REST_HEADERS_MEDIA_TYPE = ".rest.headers.mediaType";
	public static final String MOSIP_TSP_ORGANIZATION = "ida.jws.certificate.organization";
	public static final String MOSIP_JWS_CERTIFICATE_ALGM = "ida.jws.certificate.algo";
	public static final String MOSIP_IDA_API_IDS = "mosip.ida.api.ids.";
	public static final String POLICY = "policy.";
	public static final String LICENSE_KEY = "licenseKey.";
	public static final String MISP_PARTNER_MAPPING = "misp.partner.mapping.";
	public static final String PARTNER_KEY = "partner.";
	public static final String MOSIP_IDENTITY_VID = "mosip.identity.vid";
	public static final String MOSIP_VID_VALIDITY_HOURS = "ida.vid.validity.hours";
	public static final String STATIC_TOKEN_ENABLE = "static.token.enable";
	public static final String MOSIP_PRIMARY_LANGUAGE = "mosip.primary-language";
	public static final String MOSIP_SECONDARY_LANGUAGE = "mosip.secondary-language";
	public static final String MOSIP_KERNEL_OTP_DEFAULT_LENGTH = "mosip.kernel.otp.default-length";
	public static final String MOSIP_ID_VALIDATION_IDENTITY_EMAIL = "mosip.id.validation.identity.email";
	public static final String MOSIP_ID_VALIDATION_IDENTITY_PHONE = "mosip.id.validation.identity.phone";
	public static final String AUTHREQUEST_RECEIVED_TIME_ALLOWED_IN_MINUTES = "authrequest.received-time-allowed.minutes";
	public static final String OTP_CONTEXT = "ida.otp.context";
	public static final String AUTH_SMS_TEMPLATE = "ida.auth.sms.template";
	public static final String OTP_SMS_TEMPLATE = "ida.otp.sms.template";
	public static final String AUTH_EMAIL_SUBJECT_TEMPLATE = "ida.auth.mail.subject.template";
	public static final String AUTH_EMAIL_CONTENT_TEMPLATE = "ida.auth.mail.content.template";
	public static final String OTP_SUBJECT_TEMPLATE = "ida.otp.mail.subject.template";
	public static final String OTP_CONTENT_TEMPLATE = "ida.otp.mail.content.template";
	public static final String NOTIFICATION_DATE_FORMAT = "notification.date.format";
	public static final String NOTIFICATION_TIME_FORMAT = "notification.time.format";
	public static final String UIN_MASKING_CHARCOUNT = "notification.uin.masking.charcount";
	public static final String MOSIP_NOTIFICATIONTYPE = "mosip.notificationtype";
	public static final String MOSIP_KERNEL_OTP_EXPIRY_TIME = "mosip.kernel.otp.expiry-time";
	public static final String OTP_REQUEST_FLOODING_DURATION = "otp.request.flooding.duration";
	public static final String OTP_REQUEST_FLOODING_MAX_COUNT = "otp.request.flooding.max-count";
	public static final String MOSIP_IDTYPE_ALLOWED = "request.idtypes.allowed";
	public static final String AUDIT_DEFAULT_HOST_NAME = "audit.defaultHostName";
	public static final String AUDIT_DEFAULT_HOST_ADDRESS = "audit.defaultHostAddress";
	public static final String APPLICATION_NAME = "application.name";
	public static final String MOSIP_ERRORMESSAGES_DEFAULT_LANG = "ida.errormessages.default-lang";
	public static final String USER_NAME = "user.name";
	public static final String MOSIP_SUPPORTED_LANGUAGES = "mosip.supported-languages";
	public static final String DEFAULT_MATCH_VALUE = "demo.threshold";
	public static final String MOSIP_PHONETIC_LANG = "mosip.phonetic.lang.";
	public static final String EKYC_ALLOWED_AUTH_TYPE = "ekyc.auth.types.allowed";
	public static final String MOSIP_NOTIFICATION_LANGUAGE_TYPE = "mosip.notification.language-type";
	public static final String CRYPTO_PARTNER_ID = "cryptomanager.partner.id";
	public static final String IRIS_IMG_RIGHT_VALUE = ".irisimg.right.match.value";
	public static final String IRIS_IMG_LEFT_VALUE = ".irisimg.left.match.value";
	public static final String FACE_IMG_VALUE = ".faceimg.match.value";
	public static final String SERVER_PORT = "server.port";
	public static final String APP_ENVIRONMENT_LOCAL = "application.env.local";
	public static final String SWAGGER_BASE_URL = "swagger.base-url";
	public static final String KEY_SPLITTER = "mosip.kernel.data-key-splitter";
	public static final String ALLOWED_AUTH_TYPE = "auth.types.allowed";
	public static final String INTERNAL_AUTH_ALLOWED_IDTYPE = "request.idtypes.allowed.internalauth";
	public static final String INTERNAL_ALLOWED_AUTH_TYPE = "internal.auth.types.allowed";
	public static final String INTERNAL_APPLICATION_ID = "internal.application.id";
	public static final String INTERNAL_REFERENCE_ID = "internal.reference.id";
	public static final String SIGN_RESPONSE = "mosip.signed.response.header";
	public static final String MOSIP_IDA_API_ID = "ida.api.id.";
	public static final String MOSIP_IDA_API_VERSION = "ida.api.version.";
	public static final String IDA_BASIC_NORMALISER = "ida.demo.%s.normalization.regex.%s[%s]";
	public static final String IDA_NORMALISER_SEP="ida.norm.sep";
}
