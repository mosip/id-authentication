package io.mosip.authentication.core.constant;

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
	public static final String IDA_ID_ATTRIBUTE_SEPARATOR_PREFIX = "ida.id.attribute.separator.";

	private IdAuthConfigKeyConstants() {

	}

	public static final String APPLICATION_ID = "application.id";
	public static final String DATE_TIME_PATTERN = "datetime.pattern";
	public static final String BIO_DATE_TIME_PATTERN = "biometrics.datetime.pattern";
	public static final String REST_URI = ".rest.uri";
	public static final String REST_HTTP_METHOD = ".rest.httpMethod";
	public static final String REST_TIMEOUT = ".rest.timeout";
	public static final String REST_HEADERS_MEDIA_TYPE = ".rest.headers.mediaType";
	public static final String RESPONSE_TOKEN_ENABLE = "static.token.enable";
	public static final String MOSIP_KERNEL_OTP_DEFAULT_LENGTH = "mosip.kernel.otp.default-length";
	public static final String AUTHREQUEST_RECEIVED_TIME_ALLOWED_IN_SECONDS = "authrequest.received-time-allowed.seconds";
	public static final String AUTHREQUEST_RECEIVED_TIME_ADJUSTMENT_IN_SECONDS = "authrequest.received-time-adjustment.seconds";
	public static final String AUTH_SMS_TEMPLATE = "ida.auth.sms.template";
	public static final String OTP_SMS_TEMPLATE = "ida.otp.sms.template";
	public static final String AUTH_EMAIL_SUBJECT_TEMPLATE = "ida.auth.mail.subject.template";
	public static final String AUTH_EMAIL_CONTENT_TEMPLATE = "ida.auth.mail.content.template";
	public static final String OTP_SUBJECT_TEMPLATE = "ida.otp.mail.subject.template";
	public static final String OTP_CONTENT_TEMPLATE = "ida.otp.mail.content.template";
	public static final String NOTIFICATION_DATE_FORMAT = "notification.date.format";
	public static final String NOTIFICATION_TIME_FORMAT = "notification.time.format";
	public static final String NOTIFICATION_TIME_ZONE = "mosip.notification.timezone";
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
	public static final String DEFAULT_MATCH_VALUE = "ida.default.match.value";
	public static final String MOSIP_PHONETIC_LANG = "mosip.phonetic.lang.";
	public static final String EKYC_ALLOWED_AUTH_TYPE = "ekyc.auth.types.allowed";
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
	public static final String MOSIP_IDA_API_ID_KYC = "ida.api.id.kyc";
	public static final String MOSIP_IDA_API_VERSION = "ida.api.version.";
	public static final String IDA_BASIC_NORMALISER = "ida.demo.%s.normalization.regex.%s[%s]";
	public static final String IDA_NORMALISER_SEP = "ida.norm.sep";
	public static final String AUTH_TRANSACTION = "auth.transactions";
	public static final String MOSIP_UTC_TIME= "mosip.utc-datetime-pattern";
	
	public static final String MOSIP_IDA_AUTH_CLIENTID="mosip.ida.auth.clientId";
	public static final String MOSIP_IDA_AUTH_APPID="mosip.ida.auth.appId";
	public static final String OTP_INTERNAL_ID_SUFFIX = "otp.internal";
	public static final String ID_TYPE_ALIAS = "mosip.%s.alias";
	public static final String IDA_AAD_LASTBYTES_NUM = "ida.aad.lastbytes.num";
	public static final String IDA_SALT_LASTBYTES_NUM = "ida.salt.lastbytes.num";
	
	public static final String MOSIP_FMR_ENABLED="mosip.fingerprint.fmr.enabled";	

	public static final String IDA_WEBSUB_CA_CERT_CALLBACK_SECRET = "ida-websub-ca-certificate-callback-secret";
	public static final String IDA_WEBSUB_HOTLIST_CALLBACK_SECRET = "ida-websub-hotlist-callback-secret";
	public static final String IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET = "ida-websub-authtype-callback-secret";
	public static final String IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET = "ida-websub-partner-service-callback-secret";
	public static final String IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET = "ida-websub-credential-issue-callback-secret";
	public static final String IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_SECRET = "ida-websub-masterdata-templates-callback-secret";
	public static final String IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_SECRET = "ida-websub-masterdata-titles-callback-secret";
	public static final String IDA_WEBSUB_HUB_URL = IdRepoConstants.WEB_SUB_HUB_URL;
	public static final String IDA_WEBSUB_PUBLISHER_URL = IdRepoConstants.WEB_SUB_PUBLISH_URL;
	public static final String IDA_WEBSUB_AUTH_TYPE_CALLBACK_URL = "ida-websub-auth-type-callback-url";
	public static final String IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_URL = "ida-websub-partner-service-callback-url";
	public static final String IDA_WEBSUB_IDCHANGE_CALLBACK_URL = "ida-websub-idchage-callback-url";
	public static final String IDA_WEBSUB_CA_CERT_CALLBACK_URL = "ida-websub-ca-cert-callback-url";
	public static final String IDA_WEBSUB_HOTLIST_CALLBACK_URL = "ida-websub-hotlist-callback-url";
	public static final String IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_URL = "ida-websub-masterdata-templates-callback-url";
	public static final String IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_URL = "ida-websub-masterdata-titles-callback-url";
	
	public static final String IDA_ZERO_KNOWLEDGE_UNENCRYPTED_CREDENTIAL_ATTRIBUTES = "ida-zero-knowledge-unencrypted-credential-attributes";

	public static final String IDA_AUTH_PARTNER_ID = "ida-auth-partner-id";
	public static final String SUBSCRIPTIONS_DELAY_ON_STARTUP = "subscriptions-delay-on-startup_millisecs";
	public static final String DELAY_TO_PULL_MISSING_CREDENTIAL_AFTER_TOPIC_SUBACTIPTION = "delay-to-pull-missing-credential-after-topic-subscription_millisecs";
	public static final String DATA_SHARE_GET_DECRYPT_REF_ID = "data-share-get-decrypt-ref-id";
	
	public static final String IDA_WEBSUB_TOPIC_PMP_MISP_LICENSE_GENERATED = "ida-topic-pmp-misp-license-generated";
	public static final String IDA_WEBSUB_TOPIC_PMP_MISP_LICENSE_UPDATED = "ida-topic-pmp-misp-license-updated";
	public static final String IDA_WEBSUB_TOPIC_PMP_OIDC_CLIENT_CREATED = "ida-topic-pmp-oidc-client-created";
	public static final String IDA_WEBSUB_TOPIC_PMP_OIDC_CLIENT_UPDATED = "ida-topic-pmp-oidc-client-updated";

	public static final String IDA_WEBSUB_TOPIC_PMP_PARTNER_UPDATED = "ida-topic-pmp-partner-updated";
	public static final String IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_APPROVED = "ida-topic-pmp-partner-api-key-approved";
	public static final String IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_UPDATED = "ida-topic-pmp-partner-api-key-updated";
	public static final String IDA_WEBSUB_TOPIC_PMP_POLICY_UPDATED = "ida-topic-pmp-policy-updated";
	public static final String IDA_WEBSUB_CA_CERT_TOPIC = "ida-topic-pmp-ca-certificate-uploaded";
	public static final String IDA_WEBSUB_HOTLIST_TOPIC = "ida-topic-hotlist";
	public static final String IDA_WEBSUB_MASTERDATA_TEMPLATES_TOPIC = "ida-topic-masterdata-templates";
	public static final String IDA_WEBSUB_MASTERDATA_TITLES_TOPIC = "ida-topic-masterdata-titles";
	
	
	public static final String CREDENTIAL_STORE_JOB_DELAY = "ida.batch.credential.store.job.delay";
	public static final String CREDENTIAL_STORE_CHUNK_SIZE = "ida.batch.credential.store.chunk.size";
	public static final String CREDENTIAL_STORE_RETRY_MAX_LIMIT = "ida.credential.store.retry.max.limit";
	public static final String CREDENTIAL_STORE_RETRY_BACKOFF_INTERVAL_MILLISECS = "ida.credential.store.retry.backoff.interval.millisecs";
	public static final String CREDENTIAL_STORE_RETRY_BACKOFF_EXPONENTIAL_MULTIPLIER = "ida.credential.store.retry.backoff.exponential.multiplier";
	public static final String CREDENTIAL_STORE_RETRY_BACKOFF_EXPONENTIAL_MAX_INTERVAL_MILLISECS = "ida.credential.store.retry.backoff.exponential.max.interval.millisecs";
	
	public static final String IDA_DEFAULT_IDENTITY_FILTER_ATTRIBUTES = "ida-default-identity-filter-attributes";

	public static final String CREDENTIAL_STATUS_UPDATE_TOPIC = "ida-topic-credential-status-update";
	public static final String AUTH_TYPE_STATUS_ACK_TOPIC = "ida-topic-auth-type-status-update-acknowledge";
	public static final String AUTH_TRANSACTION_STATUS_TOPIC = "ida-topic-auth-transaction-status";
	public static final String AUTH_ANONYMOUS_PROFILE_TOPIC = "ida-topic-auth-anonymous-profile";
	public static final String AUTH_FRAUD_ANALYSIS_TOPIC = "ida-topic-fraud-analysis";
	public static final String ON_DEMAND_TEMPLATE_EXTRACTION_TOPIC = "ida-topic-on-demand-template-extraction";


	public static final String IDA_MAX_CREDENTIAL_PULL_WINDOW_DAYS = "ida-max-credential-pull-window-days";
	public static final String IDA_MAX_WEBSUB_MSG_PULL_WINDOW_DAYS = "ida-max-websub-messages-pull-window-days";
	
	public static final String IDA_BIO_HASH_VALIDATION_DISABLED = "ida.bio.hash.validation.disabled";
	
	public static final String IDA_FETCH_FAILED_WEBSUB_MESSAGES_CHUNK_SIZE = "ida.fetch.failed.websub.messages.chunk.size";
	
	public static final String ALLOWED_ENVIRONMENTS="mosip.ida.allowed.enviromemnts";
	
	public static final String ALLOWED_DOMAIN_URIS="mosip.ida.allowed.domain.uris";

	public static final String DEFAULT_TEMPLATE_LANGUAGES = "mosip.default.template-languages";	
	public static final String MOSIP_MANDATORY_LANGUAGES="mosip.mandatory-languages";	
	public static final String MOSIP_OPTIONAL_LANGUAGES ="mosip.optional-languages"; 

	public static final String IDA_MOSIP_EXTERNAL_AUTH_FILTER_CLASSES_IN_EXECUTION_ORDER = "ida.mosip.external.auth.filter.classes.in.execution.order";
	public static final String IDA_MOSIP_INTERNAL_AUTH_FILTER_CLASSES_IN_EXECUTION_ORDER = "ida.mosip.internal.auth.filter.classes.in.execution.order";
  
	public static final String USER_PREFFRRED_LANG_ATTRIBUTE_NAME="mosip.default.user-preferred-language-attribute";

	public static final String BIO_SEGMENT_TIME_DIFF_ALLOWED = "authrequest.biometrics.allowed-segment-time-difference-in-seconds";
	public static final String PREFERRED_LANG_ATTRIB_NAME = "mosip.preferred.language.attribute.name";
	public static final String LOCATION_PROFILE_ATTRIB_NAME = "mosip.location.profile.attribute.name";
	
	public static final String MOSIP_DATE_OF_BIRTH_PATTERN = "mosip.date-of-birth.pattern";
	
	public static final String MOSIP_DATE_OF_BIRTH_ATTRIBUTE_NAME = "mosip.date-of-birth.attribute.name";

	public static final String FRAUD_ANALYSIS_ENABLED = "mosip.ida.fraud-analysis-enabled";

	public static final String IDA_MISSING_CREDENTIAL_RETRIGGER_ENABLED = "ida-missing-credential-retrigger-enabled";

	public static final String KYC_TOKEN_EXPIRE_TIME_ADJUSTMENT_IN_SECONDS = "mosip.ida.kyc.token.expire.time.adjustment.seconds";

	public static final String KYC_EXCHANGE_DEFAULT_LANGUAGE = "mosip.ida.kyc.exchange.default.lang";

	public static final String IDP_AMR_ACR_IDA_MAPPING_SOURCE = "idp.amr-acr.ida.mapping.property.source";
}
