package io.mosip.authentication.core.constant;

/**
 * The Enum RestServiceContants.
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
	SMS_NOTIFICATION_SERVICE("sms-notification");

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
