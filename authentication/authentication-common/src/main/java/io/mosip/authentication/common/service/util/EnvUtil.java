package io.mosip.authentication.common.service.util;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEFAULT_AAD_LAST_BYTES_NUM;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEFAULT_HOST_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEFAULT_PARTIAL_MATCH_VALUE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEFAULT_REQUEST_TIME_ADJUSTMENT_SECONDS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEFAULT_SALT_LAST_BYTES_NUM;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.KYC;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.ALLOWED_AUTH_TYPE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.ALLOWED_DOMAIN_URIS;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.ALLOWED_ENVIRONMENTS;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.APPLICATION_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.APPLICATION_NAME;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUDIT_DEFAULT_HOST_NAME;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUTHREQUEST_RECEIVED_TIME_ADJUSTMENT_IN_SECONDS;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUTHREQUEST_RECEIVED_TIME_ALLOWED_IN_SECONDS;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUTH_EMAIL_CONTENT_TEMPLATE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUTH_EMAIL_SUBJECT_TEMPLATE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUTH_SMS_TEMPLATE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.BIO_DATE_TIME_PATTERN;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.BIO_SEGMENT_TIME_DIFF_ALLOWED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.DATE_TIME_PATTERN;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.DEFAULT_MATCH_VALUE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.DEFAULT_TEMPLATE_LANGUAGES;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.DELAY_TO_PULL_MISSING_CREDENTIAL_AFTER_TOPIC_SUBACTIPTION;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.EKYC_ALLOWED_AUTH_TYPE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FRAUD_ANALYSIS_ENABLED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_AAD_LASTBYTES_NUM;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_BIO_HASH_VALIDATION_DISABLED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_SALT_LASTBYTES_NUM;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.INTERNAL_ALLOWED_AUTH_TYPE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.INTERNAL_AUTH_ALLOWED_IDTYPE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.INTERNAL_BIO_REFERENCE_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.INTERNAL_REFERENCE_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.KEY_SPLITTER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_ERRORMESSAGES_DEFAULT_LANG;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_FMR_ENABLED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_IDA_API_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_IDA_AUTH_CLIENTID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_IDTYPE_ALLOWED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_KERNEL_OTP_EXPIRY_TIME;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_MANDATORY_LANGUAGES;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_NOTIFICATIONTYPE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.MOSIP_OPTIONAL_LANGUAGES;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.NOTIFICATION_DATE_FORMAT;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.NOTIFICATION_TIME_FORMAT;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.NOTIFICATION_TIME_ZONE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.OTP_CONTENT_TEMPLATE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.OTP_REQUEST_FLOODING_DURATION;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.OTP_REQUEST_FLOODING_MAX_COUNT;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.OTP_SMS_TEMPLATE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.OTP_SUBJECT_TEMPLATE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.PARTNER_BIO_REFERENCE_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.PARTNER_REFERENCE_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.RESPONSE_TOKEN_ENABLE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.SIGN_RESPONSE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.UIN_MASKING_CHARCOUNT;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.USER_NAME;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.USER_PREFFRRED_LANG_ATTRIBUTE_NAME;
import static io.mosip.idrepository.core.constant.IdRepoConstants.DEFAULT_SALT_KEY_LENGTH;
import static io.mosip.idrepository.core.constant.IdRepoConstants.SALT_KEY_LENGTH;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.KYC_TOKEN_EXPIRE_TIME_ADJUSTMENT_IN_SECONDS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEFAULT_KYC_TOKEN_EXPIRE_TIME_ADJUSTMENT_IN_SECONDS;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.KYC_EXCHANGE_DEFAULT_LANGUAGE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEFAULT_KYC_EXCHANGE_DEFAULT_LANGUAGE;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Manoj SP
 *
 */
@Component
public class EnvUtil {

	@Getter @Setter private static String  dateTimePattern;
	
	@Getter @Setter private static String  biometricDateTimePattern;

	@Getter @Setter private static String  allowedAuthType;

	@Getter @Setter private static String  allowedIdTypes;

	@Getter @Setter private static String  internalAllowedAuthType;

	@Getter @Setter private static String  internalAllowedIdTypes;

	@Getter @Setter private static String  ekycAllowedAuthType;
	
	@Getter @Setter private static String  allowedEnv;

	@Getter @Setter private static String  allowedDomainUri;

	@Getter @Setter private static Boolean isFmrEnabled;

	@Getter @Setter private static String  auditDefaultHostName;

	@Getter @Setter private static String  appId;

	@Getter @Setter private static String  appName;

	@Getter @Setter private static String  username;

	@Getter @Setter private static String  mandatoryLanguages;
	
	@Getter @Setter private static String  optionalLanguages;

	@Getter @Setter private static Boolean authTokenRequired;

	@Getter @Setter private static Long bioSegmentTimeDiffAllowed;

	@Getter @Setter private static Long authRequestReceivedTimeAllowedInSeconds;

	@Getter @Setter private static Long requestTimeAdjustmentSeconds;
	
	@Getter @Setter private static Boolean isFraudAnalysisEnabled;

	@Getter @Setter private static Long delayToPullMissingCredAfterTopicSub;
	
	@Getter @Setter private static String  signResponse;
	
	@Getter @Setter private static String  errorMsgDefaultLang;
	
	@Getter @Setter private static String  idaAuthClientId;
	
	@Getter @Setter private static Integer saltKeyLength;
	
	@Getter @Setter private static String  defaultTemplateLang;
	
	@Getter @Setter private static String  userPrefLangAttrName;
	
	@Getter @Setter private static String  internalAuthAllowedType;
	
	@Getter @Setter private static String  authEmailContentTemplate;
	
	@Getter @Setter private static String  authEmailSubjectTemplate;
	
	@Getter @Setter private static String  otpSubjectTemplate;
	
	@Getter @Setter private static String  otpContentTemplate;
	
	@Getter @Setter private static String  authSmsTemplate;
	
	@Getter @Setter private static String  otpSmsTemplate;
	
	@Getter @Setter private static Integer defaultMatchValue;
	
	@Getter @Setter private static String  uinMaskingCharCount;
	
	@Getter @Setter private static String  notificationType;
	
	@Getter @Setter private static Integer otpExpiryTime;
	
	@Getter @Setter private static String  notificationDateFormat;
	
	@Getter @Setter private static String  notificationTimeFormat;
	
	@Getter @Setter private static String  notificationTimeZone;
	
	@Getter @Setter private static String  keySplitter;
	
	@Getter @Setter private static Integer otpRequestFloodingDuration;
	
	@Getter @Setter private static Integer otpRequestFloodingMaxCount;
	
	@Getter @Setter private static Boolean isBioHashValidationDisabled;
	
	@Getter @Setter private static Integer saltLastBytesSum;
	
	@Getter @Setter private static Integer aadLastBytesSum;
	
	@Getter @Setter private static String  partnerBioRefId;

	@Getter @Setter private static String  partnerRefId;

	@Getter @Setter private static String  idaApiIdWithKyc;

	@Getter @Setter private static Boolean internalAuthSigningRequired;

	@Getter @Setter private static Boolean internalAuthSignatureVerificationRequired;

	@Getter @Setter private static Boolean internalAuthTrustValidationRequired;

	@Getter @Setter private static Boolean internalAuthBioHashValidationDisabled;

	@Getter @Setter private static String  internalAuthInternalRefId;

	@Getter @Setter private static String  internalAuthInternalBioRefId;
	
	@Getter @Setter private static Integer activeAsyncThreadCount;
	
	@Getter @Setter private static String monitorAsyncThreadQueue;
	
	@Getter @Setter private static Integer asyncThreadQueueThreshold;
	
	@Getter @Setter private static Long kycTokenExpireTimeAdjustmentSeconds;

	@Getter @Setter private static String kycExchangeDefaultLanguage;

	@Autowired
	private Environment env;

	@PostConstruct
	public void init() {
		this.merge((ConfigurableEnvironment) getEnvironment());
		setIsFmrEnabled(this.getProperty(MOSIP_FMR_ENABLED, boolean.class, false));
		setAuditDefaultHostName(this.getProperty(AUDIT_DEFAULT_HOST_NAME, DEFAULT_HOST_NAME));
		setDateTimePattern(this.getProperty(DATE_TIME_PATTERN));
		setBiometricDateTimePattern(this.getProperty(BIO_DATE_TIME_PATTERN));
		setAllowedAuthType(this.getProperty(ALLOWED_AUTH_TYPE));
		setAllowedIdTypes(this.getProperty(MOSIP_IDTYPE_ALLOWED));
		setInternalAllowedAuthType(this.getProperty(INTERNAL_ALLOWED_AUTH_TYPE));
		setInternalAllowedIdTypes(this.getProperty(INTERNAL_AUTH_ALLOWED_IDTYPE));
		setEkycAllowedAuthType(this.getProperty(EKYC_ALLOWED_AUTH_TYPE));
		setAllowedEnv(this.getProperty(ALLOWED_ENVIRONMENTS));
		setAllowedDomainUri(this.getProperty(ALLOWED_DOMAIN_URIS));
		setAppId(this.getProperty(APPLICATION_ID));
		setAppName(this.getProperty(APPLICATION_NAME));
		setUsername(this.getProperty(USER_NAME));
		setMandatoryLanguages(this.getProperty(MOSIP_MANDATORY_LANGUAGES));
		setOptionalLanguages(this.getProperty(MOSIP_OPTIONAL_LANGUAGES));
		setAuthTokenRequired(this.getProperty(RESPONSE_TOKEN_ENABLE, Boolean.class));
		setBioSegmentTimeDiffAllowed(this.getProperty(BIO_SEGMENT_TIME_DIFF_ALLOWED, Long.class, 120L));
		setAuthRequestReceivedTimeAllowedInSeconds(this.getProperty(AUTHREQUEST_RECEIVED_TIME_ALLOWED_IN_SECONDS,
				Long.class));
		setRequestTimeAdjustmentSeconds(this.getProperty(AUTHREQUEST_RECEIVED_TIME_ADJUSTMENT_IN_SECONDS, Long.class,
				DEFAULT_REQUEST_TIME_ADJUSTMENT_SECONDS));
		setIsFraudAnalysisEnabled(this.getProperty(FRAUD_ANALYSIS_ENABLED, Boolean.class, true));
		setDelayToPullMissingCredAfterTopicSub(this.getProperty(DELAY_TO_PULL_MISSING_CREDENTIAL_AFTER_TOPIC_SUBACTIPTION, Long.class, 60000l));
		setSignResponse(this.getProperty(SIGN_RESPONSE));
		setErrorMsgDefaultLang(this.getProperty(MOSIP_ERRORMESSAGES_DEFAULT_LANG));
		setIdaAuthClientId(this.getProperty(MOSIP_IDA_AUTH_CLIENTID));
		setSaltKeyLength(this.getProperty(SALT_KEY_LENGTH, Integer.class, DEFAULT_SALT_KEY_LENGTH));
		setDefaultTemplateLang(this.getProperty(DEFAULT_TEMPLATE_LANGUAGES));
		setUserPrefLangAttrName(this.getProperty(USER_PREFFRRED_LANG_ATTRIBUTE_NAME));
		setInternalAuthAllowedType(this.getProperty(INTERNAL_ALLOWED_AUTH_TYPE));
		setAuthEmailContentTemplate(this.getProperty(AUTH_EMAIL_CONTENT_TEMPLATE));
		setAuthEmailSubjectTemplate(this.getProperty(AUTH_EMAIL_SUBJECT_TEMPLATE));
		setOtpSubjectTemplate(this.getProperty(OTP_SUBJECT_TEMPLATE));
		setOtpContentTemplate(this.getProperty(OTP_CONTENT_TEMPLATE));
		setAuthSmsTemplate(this.getProperty(AUTH_SMS_TEMPLATE));
		setOtpSmsTemplate(this.getProperty(OTP_SMS_TEMPLATE));
		setDefaultMatchValue(this.getProperty(DEFAULT_MATCH_VALUE, int.class, DEFAULT_PARTIAL_MATCH_VALUE));
		setUinMaskingCharCount(this.getProperty(UIN_MASKING_CHARCOUNT));
		setNotificationType(this.getProperty(MOSIP_NOTIFICATIONTYPE));
		setOtpExpiryTime(this.getProperty(MOSIP_KERNEL_OTP_EXPIRY_TIME, Integer.class));
		setNotificationDateFormat(this.getProperty(NOTIFICATION_DATE_FORMAT));
		setNotificationTimeFormat(this.getProperty(NOTIFICATION_TIME_FORMAT));
		setNotificationTimeZone(this.getProperty(NOTIFICATION_TIME_ZONE));
		setKeySplitter(this.getProperty(KEY_SPLITTER));
		setOtpRequestFloodingDuration(this.getProperty(OTP_REQUEST_FLOODING_DURATION, Integer.class));
		setOtpRequestFloodingMaxCount(this.getProperty(OTP_REQUEST_FLOODING_MAX_COUNT, Integer.class));
		setIsBioHashValidationDisabled(this.getProperty(IDA_BIO_HASH_VALIDATION_DISABLED, Boolean.class, false));
		setInternalAuthBioHashValidationDisabled(this.getProperty(IDA_BIO_HASH_VALIDATION_DISABLED, Boolean.class, true));
		setSaltLastBytesSum(this.getProperty(
				IDA_SALT_LASTBYTES_NUM, Integer.class, DEFAULT_SALT_LAST_BYTES_NUM));
		setAadLastBytesSum(this.getProperty(
				IDA_AAD_LASTBYTES_NUM, Integer.class, DEFAULT_AAD_LAST_BYTES_NUM));
		setPartnerBioRefId(this.getProperty(PARTNER_BIO_REFERENCE_ID));
		setPartnerRefId(this.getProperty(PARTNER_REFERENCE_ID));
		setIdaApiIdWithKyc(this.getProperty(MOSIP_IDA_API_ID + KYC));
		setInternalAuthSigningRequired(this.getProperty("mosip.ida.internal.signing-required", Boolean.class, true));
		setInternalAuthSignatureVerificationRequired(this.getProperty("mosip.ida.internal.signature-verification-required", Boolean.class, false));
		setInternalAuthTrustValidationRequired(this.getProperty("mosip.ida.internal.trust-validation-required", Boolean.class, false));
		setInternalAuthInternalRefId(this.getProperty(INTERNAL_REFERENCE_ID));
		setInternalAuthInternalBioRefId(this.getProperty(INTERNAL_BIO_REFERENCE_ID));
		setActiveAsyncThreadCount(this.getProperty("mosip.ida.active-async-thread-count", Integer.class));
		setMonitorAsyncThreadQueue(this.getProperty("mosip.ida.monitor-thread-queue-in-ms"));
		setAsyncThreadQueueThreshold(this.getProperty("mosip.ida.max-thread-queue-threshold", Integer.class, 0));
		setKycTokenExpireTimeAdjustmentSeconds(this.getProperty(KYC_TOKEN_EXPIRE_TIME_ADJUSTMENT_IN_SECONDS, Long.class,
			DEFAULT_KYC_TOKEN_EXPIRE_TIME_ADJUSTMENT_IN_SECONDS));
		setKycExchangeDefaultLanguage(this.getProperty(KYC_EXCHANGE_DEFAULT_LANGUAGE, DEFAULT_KYC_EXCHANGE_DEFAULT_LANGUAGE));
			
	}
	
	public String getProperty(String key) {
		return getEnvironment().getProperty(key);
	}

	public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
		return getEnvironment().getProperty(key, targetType, defaultValue);
	}

	public String getProperty(String key, String defaultValue) {
		return getEnvironment().getProperty(key, defaultValue);
	}

	public <T> T getProperty(String key, Class<T> targetType) {
		return getEnvironment().getProperty(key, targetType);
	}

	public void merge(ConfigurableEnvironment parent) {
		((ConfigurableEnvironment) getEnvironment()).merge(parent);
	}

	public boolean containsProperty(String key) {
		return getEnvironment().containsProperty(key);
	}

	public Environment getEnvironment() {
		return env;
	}
}