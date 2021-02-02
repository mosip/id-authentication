package io.mosip.authentication.core.constant;

import java.util.function.Predicate;

import org.springframework.core.env.Environment;

import io.mosip.idrepository.core.constant.IdRepoConstants;

/**
 * This Class will provide constants for all Configured properties.
 * 
 * @author Prem Kumar
 *
 */
public final class IdAuthConfigKeyConstants {

	public static final String IDA_BDB_PROCESSED_LEVEL = "ida-bdb-processed-level";
	public static final String IDA_DATASHARE_THUMBPRINT_VALIDATION_REQUIRED = "mosip.ida.datashare.thumbprint-validation-required";

	private IdAuthConfigKeyConstants() {

	}

	public static final String APPLICATION_ID = "application.id";
	public static final String DATE_TIME_PATTERN = "datetime.pattern";
	public static final String REST_URI = ".rest.uri";
	public static final String REST_HTTP_METHOD = ".rest.httpMethod";
	public static final String REST_TIMEOUT = ".rest.timeout";
	public static final String REST_HEADERS_MEDIA_TYPE = ".rest.headers.mediaType";
	public static final String RESPONSE_TOKEN_ENABLE = "static.token.enable";
	public static final String MOSIP_PRIMARY_LANGUAGE = "mosip.primary-language";
	public static final String MOSIP_SECONDARY_LANGUAGE = "mosip.secondary-language";
	public static final String MOSIP_KERNEL_OTP_DEFAULT_LENGTH = "mosip.kernel.otp.default-length";
	public static final String AUTHREQUEST_RECEIVED_TIME_ALLOWED_IN_MINUTES = "authrequest.received-time-allowed.minutes";
	public static final String AUTHREQUEST_RECEIVED_TIME_ADJUSTMENT_IN_MINUTES = "authrequest.received-time-adjustment.minutes";
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
	public static final String DEFAULT_MATCH_VALUE = "ida.default.match.value";
	public static final String MOSIP_PHONETIC_LANG = "mosip.phonetic.lang.";
	public static final String EKYC_ALLOWED_AUTH_TYPE = "ekyc.auth.types.allowed";
	public static final String MOSIP_NOTIFICATION_LANGUAGE_TYPE = "mosip.notification.language-type";
	public static final String PARTNER_REFERENCE_ID = "partner.reference.id";
	public static final String PARTNER_BIO_REFERENCE_ID = "partner.biometric.reference.id";
	public static final String SERVER_PORT = "server.port";
	public static final String APP_ENVIRONMENT_LOCAL = "application.env.local";
	public static final String SWAGGER_BASE_URL = "swagger.base-url";
	public static final String KEY_SPLITTER = "mosip.kernel.data-key-splitter";
	public static final String ALLOWED_AUTH_TYPE = "auth.types.allowed";
	public static final String INTERNAL_AUTH_ALLOWED_IDTYPE = "request.idtypes.allowed.internalauth";
	public static final String INTERNAL_ALLOWED_AUTH_TYPE = "internal.auth.types.allowed";
	public static final String INTERNAL_REFERENCE_ID = "internal.reference.id";
	public static final String INTERNAL_BIO_REFERENCE_ID = "internal.biometric.reference.id";
	public static final String SIGN_RESPONSE = "mosip.signed.response.header";
	public static final String MOSIP_IDA_API_ID = "ida.api.id.";
	public static final String MOSIP_IDA_API_VERSION = "ida.api.version.";
	public static final String IDA_BASIC_NORMALISER = "ida.demo.%s.normalization.regex.%s[%s]";
	public static final String IDA_NORMALISER_SEP = "ida.norm.sep";
	public static final String AUTH_TRANSACTION = "auth.transactions";
	public static final String UIN_SALT_MODULO = "ida.uin.salt.modulo";
	public static final String MOSIP_UTC_TIME= "mosip.utc-datetime-pattern";
	
	public static final String MOSIP_IDA_AUTH_CLIENTID="mosip.ida.auth.clientId";
	public static final String MOSIP_IDA_AUTH_APPID="mosip.ida.auth.appId";
	public static final String OTP_INTERNAL_ID_SUFFIX = "otp.internal";
	public static final String ID_TYPE_ALIAS = "mosip.%s.alias";
	public static final String IDA_AAD_LASTBYTES_NUM = "ida.aad.lastbytes.num";
	public static final String IDA_SALT_LASTBYTES_NUM = "ida.salt.lastbytes.num";
	
	public static final String MOSIP_FMR_ENABLED="mosip.fingerprint.fmr.enabled";	

	public static final Predicate<Environment> FMR_ENABLED_TEST = env -> env.getProperty(IdAuthConfigKeyConstants.MOSIP_FMR_ENABLED, boolean.class, false);


	public static final String IDA_WEBSUB_CA_CERT_CALLBACK_SECRET = "ida-websub-ca-certificate-callback-secret";
	public static final String IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET = "ida-websub-authtype-callback-secret";
	public static final String IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET = "ida-websub-partner-service-callback-secret";
	public static final String IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET = "ida-websub-credential-issue-callback-secret";
	public static final String IDA_WEBSUB_HUB_URL = IdRepoConstants.WEB_SUB_HUB_URL;
	public static final String IDA_WEBSUB_PUBLISHER_URL = IdRepoConstants.WEB_SUB_PUBLISH_URL;
	public static final String IDA_WEBSUB_AUTH_TYPE_CALLBACK_URL = "ida-websub-auth-type-callback-url";
	public static final String IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_URL = "ida-websub-partner-service-callback-url";
	public static final String IDA_WEBSUB_CREDENTIAL_ISSUE_CALLBACK_URL = "ida-websub-credential-issue-callback-url";
	public static final String IDA_WEBSUB_CA_CERT_CALLBACK_URL = "ida-websub-ca-cert-callback-url";
	
	public static final String IDA_ZERO_KNOWLEDGE_ENCRYPTED_CREDENTIAL_ATTRIBUTES = "ida-zero-knowledge-encrypted-credential-attributes";

	public static final String IDA_AUTH_PARTNER_ID = "ida-auth-partner-id";
	public static final String SUBSCRIPTIONS_DELAY_ON_STARTUP = "subscriptions-delay-on-startup";
	public static final String DATA_SHARE_GET_DECRYPT_REF_ID = "data-share-get-decrypt-ref-id";
	
	public static final String IDA_WEBSUB_TOPIC_PMP_MISP_UPDATED = "ida-topic-pmp-misp-updated";
	public static final String IDA_WEBSUB_TOPIC_PMP_PARTNER_UPDATED = "ida-topic-pmp-partner-updated";
	public static final String IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_UPDATED = "ida-topic-pmp-partner-api-key-updated";
	public static final String IDA_WEBSUB_TOPIC_PMP_POLICY_UPDATED = "ida-topic-pmp-policy-updated";
	public static final String IDA_WEBSUB_CA_CERT_TOPIC = "ida-topic-pmp-ca-certificate-uploaded";

	public static final String CREDENTIAL_STORE_JOB_DELAY = "ida.batch.credential.store.job.delay";
	public static final String CREDENTIAL_STORE_CHUNK_SIZE = "ida.batch.credential.store.chunk.size";
	public static final String CREDENTIAL_STORE_RETRY_MAX_LIMIT = "ida.credential.store.retry.max.limit";
	public static final String CREDENTIAL_STORE_RETRY_RETRY_INTERVAL_MILLISECS = "ida.credential.store.retry.interval.millisecs";

}
