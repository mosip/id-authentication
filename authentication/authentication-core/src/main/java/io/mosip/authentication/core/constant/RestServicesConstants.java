package io.mosip.authentication.core.constant;

/**
 * The Enum RestServiceContants - contains service names based on which {@link RestServiceBuilder}
 * will build rest requests from properties.
 *
 * @author Manoj SP
 */
public enum RestServicesConstants {

	/** The audit manager service. */
	AUDIT_MANAGER_SERVICE("audit"),

	/** The otp generate service. */
	OTP_GENERATE_SERVICE("otp-generate"),

	/** The otp validate service. */
	OTP_VALIDATE_SERVICE("otp-validate"),

	/** Mail notification service. */
	MAIL_NOTIFICATION_SERVICE("mail-notification"),

	/** SMS notification service. */
	SMS_NOTIFICATION_SERVICE("sms-notification"),

	ID_REPO_SERVICE("id-repo-service"),

	ID_MASTERDATA_TEMPLATE_SERVICE("id-masterdata-template-service"),
	
	ID_MASTERDATA_TEMPLATE_SERVICE_MULTILANG("id-masterdata-template-service-multilang"),
	
	GENDER_TYPE_SERVICE("id-masterdata-gender-service"),

	DECRYPTION_SERVICE("decrypt-service"),
	
	TITLE_SERVICE("id-masterdata-title-service"),
	
	TOKEN_ID_GENERATOR("token-id-generator");

	/** The service name. */
	private final String serviceName;

	/**
	 * Instantiates a new rest service contants.
	 *
	 * @param serviceName the service name
	 */
	private RestServicesConstants(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	public String getServiceName() {
		return serviceName;
	}
}
