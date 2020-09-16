package io.mosip.authentication.core.constant;

import java.util.function.Predicate;

import org.springframework.core.env.Environment;

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
	public static final String PARTNER_REFERENCE_ID = "partner.reference.id";
	public static final String PARTNER_BIO_REFERENCE_ID = "partner.biometric.reference.id";
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
	public static final String INTERNAL_BIO_REFERENCE_ID = "internal.biometric.reference.id";
	public static final String SIGN_RESPONSE = "mosip.signed.response.header";
	public static final String MOSIP_IDA_API_ID = "ida.api.id.";
	public static final String MOSIP_IDA_API_VERSION = "ida.api.version.";
	public static final String IDA_BASIC_NORMALISER = "ida.demo.%s.normalization.regex.%s[%s]";
	public static final String IDA_NORMALISER_SEP = "ida.norm.sep";
	public static final String AUTH_TRANSACTION = "auth.transactions";
	public static final String AUTH_TYPE_READ = "authtype.status.read";
	public static final String AUTH_TYPE_UPDATE = "authtype.status.update";
	public static final String EVENT_NOTIFY = "event.notify";
	public static final String UIN_SALT_MODULO = "ida.uin.salt.modulo";
	public static final String MOSIP_UTC_TIME= "mosip.utc-datetime-pattern";
	
	public static final String MOSIP_IDA_AUTH_CLIENTID="mosip.ida.auth.clientId";
	public static final String MOSIP_IDA_AUTH_APPID="mosip.ida.auth.appId";
	public static final String FINGERPRINT_PROVIDER = "ida.fingerprint.provider";
	public static final String FINGERPRINT_PROVIDER_ARGS = "ida.fingerprint.provider.args";
	public static final String FACE_PROVIDER = "ida.face.provider";
	public static final String FACE_PROVIDER_ARGS = "ida.face.provider.args";
	public static final String IRIS_PROVIDER = "ida.iris.provider";
	public static final String IRIS_PROVIDER_ARGS = "ida.iris.provider.args";
	public static final String BIO_SDK_INTEGRATOR = "ida.bio.sdk.integrator";
	public static final String BIO_SDK_INTEGRATOR_ARGS = "ida.bio.sdk.integrator.args";
	public static final String COMPOSITE_BIO_PROVIDER = "ida.composite.biometric.provider";
	public static final String COMPOSITE_BIO_PROVIDER_ARGS = "ida.composite.biometric.provider.args";
	public static final String OTP_INTERNAL_ID_SUFFIX = "otp.internal";
	public static final String ID_TYPE_ALIAS = "mosip.%s.alias";
	public static final String IDA_AAD_LASTBYTES_NUM = "ida.aad.lastbytes.num";
	public static final String IDA_SALT_LASTBYTES_NUM = "ida.salt.lastbytes.num";
	
	public static final String MOSIP_FMR_ENABLED="mosip.fingerprint.fmr.enabled";	

	public static final String IDA_SIGN_REFID="ida.sign.refid";
	public static final String IDA_SIGN_APPID="ida.sign.applicationid";
	
	public static final Predicate<Environment> FMR_ENABLED_TEST = env -> env.getProperty(IdAuthConfigKeyConstants.MOSIP_FMR_ENABLED, boolean.class, false);

	public static final String IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET = "ida-websub-authtype-callback-secret";
	public static final String IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET = "ida-websub-credential-issue-callback-secret";
	public static final String IDA_WEBSUB_HUB_URL = "ida-websub-subscriber-hub-url";
	public static final String IDA_WEBSUB_PUBLISHER_URL = "ida-websub-publisher-url";
	public static final String IDA_WEBSUB_AUTH_TYPE_CALLBACK_URL = "ida-websub-auth-type-callback-url";
	public static final String IDA_WEBSUB_CREDENTIAL_ISSUE_CALLBACK_URL = "ida-websub-credential-issue-callback-url";
	
	public static final String IDA_ZERO_KNOWLEDGE_ENCRYPTED_CREDENTIAL_ATTRIBUTES = "ida-zero-knowledge-encrypted-credential-attributes";

	public static final String IDA_AUTH_PARTNER_ID = "ida-auth-partner-id";
	public static final String SUBSCRIPTIONS_DELAY_ON_STARTUP = "subscriptions-delay-on-startup";
	public static final String DATA_SHARE_GET_DECRYPT_REF_ID = "data-share-get-decrypt-ref-id";

	
}
