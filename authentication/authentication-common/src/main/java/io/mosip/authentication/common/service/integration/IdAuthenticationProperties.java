package io.mosip.authentication.common.service.integration;

/**
 * This Class will provide constants for all Configured properties.
 * 
 * @author Prem Kumar
 *
 */
public enum IdAuthenticationProperties {
	APPLICATION_ID("application.id"),
	DATE_TIME_PATTERN("datetime.pattern"),
	REST_URI(".rest.uri"),
	REST_HTTP_METHOD(".rest.httpMethod"),
	REST_TIMEOUT(".rest.timeout"),
	REST_HEADERS_MEDIA_TYPE(".rest.headers.mediaType"),
	MOSIP_TSP_ORGANIZATION("mosip.jws.certificate.organization"),
	MOSIP_JWS_CERTIFICATE_ALGM("mosip.jws.certificate.algo"),
	MOSIP_IDA_API_IDS("mosip.ida.api.ids."),
	POLICY("policy."),
	LICENSE_KEY("licenseKey."),
	MISP_PARTNER_MAPPING("misp.partner.mapping."),
	PARTNER_KEY("partner."),
	MOSIP_IDENTITY_VID("mosip.identity.vid"),
	MOSIP_VID_VALIDITY_HOURS("mosip.vid.validity.hours"),
	STATIC_TOKEN_ENABLE("static.token.enable"),
	MOSIP_PRIMARY_LANGUAGE("mosip.primary-language"),
	EKYC_TTL_HOURS("ekyc.ttl.hours"),
	MOSIP_SECONDARY_LANGUAGE("mosip.secondary-language"),
	DOB_REQ_DATE_PATTERN("dob.req.date.pattern"),
	MOSIP_KERNEL_OTP_DEFAULT_LENGTH("mosip.kernel.otp.default-length"),
	MOSIP_ID_VALIDATION_IDENTITY_EMAIL("mosip.id.validation.identity.email"),
	MOSIP_ID_VALIDATION_IDENTITY_PHONE("mosip.id.validation.identity.phone"),
	AUTHREQUEST_RECEIVED_TIME_ALLOWED_IN_HOURS("authrequest.received-time-allowed.in-hours"),
	OTP_CONTENT_TEMPLATE("mosip.otp.mail.content.template"),
	AUTH_SMS_TEMPLATE("mosip.auth.sms.template"),
	OTP_SMS_TEMPLATE("mosip.otp.sms.template"),
	AUTH_EMAIL_SUBJECT_TEMPLATE("mosip.auth.mail.subject.template"),
	AUTH_EMAIL_CONTENT_TEMPLATE("mosip.auth.mail.content.template"),
	OTP_SUBJECT_TEMPLATE("mosip.otp.mail.subject.template"),
	UIN_MASKING_REQUIRED("uin.masking.required"),
	NOTIFICATION_DATE_FORMAT("notification.date.format"),
	NOTIFICATION_TIME_FORMAT("notification.time.format"),
	UIN_MASKING_CHARCOUNT("uin.masking.charcount"),
	MOSIP_NOTIFICATIONTYPE("mosip.notificationtype"),
	MOSIP_KERNEL_OTP_EXPIRY_TIME("mosip.kernel.otp.expiry-time"),
	OTP_REQUEST_FLOODING_DURATION("otp.request.flooding.duration"),
	OTP_REQUEST_FLOODING_MAX_COUNT("otp.request.flooding.max-count"),
	OTPREQUEST_RECEIVED_TIME_ALLOWED_IN_MINUTES("otprequest.received-time-allowed.in-minutes"),
	MOSIP_IDTYPE_ALLOWED("mosip.idtype.allowed"),
	AUDIT_DEFAULT_HOST_NAME("audit.defaultHostName"),
	AUDIT_DEFAULT_HOST_ADDRESS("audit.defaultHostAddress"),
	APPLICATION_NAME("application.name"),
	MOSIP_ERRORMESSAGES_DEFAULT_LANG("mosip.errormessages.default-lang"),
	USER_NAME("user.name"),
	MOSIP_SUPPORTED_LANGUAGES("mosip.supported-languages"),
	DEFAULT_MATCH_VALUE("demo.threshold"),
	MOSIP_PHONETIC_LANG("mosip.phonetic.lang."),
	DOB_ENTITY_DATE_PATTERN("dob.entity.date.pattern"),
	EKYC_ALLOWED_AUTH_TYPE("ekyc.auth.types.allowed"),
	MOSIP_KERNEL_IDREPO_STATUS_REGISTERED("mosip.kernel.idrepo.status.registered"),
	MOSIP_NOTIFICATION_LANGUAGE_TYPE("mosip.notification.language-type"),
	MOSIP_IDA_PUBLICKEY("mosip.ida.publickey");
	private final String key;
	
	private IdAuthenticationProperties(String key) {
		this.key=key;
	}
	/**
	 * Gets the property name
	 * 
	 * @return propertyName
	 */
	public String getkey() {
		return key;
	}
}
