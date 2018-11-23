package io.mosip.kernel.smsnotification.constant;

/**
 * This enum provides all the constant for sms notification.
 * 
 * @author Ritesh sinha
 * @since 1.0.0
 *
 */
public enum SmsPropertyConstant {

	AUTH_KEY("authkey"), 
	SMS_MESSAGE("message"),
	RECIPIENT_NUMBER("mobiles"),
	COUNTRY_CODE("country"),
	ROUTE("route"),
	SENDER_ID("sender"),
	VENDOR_RESPONSE_SUCCESS("success"),
	SUCCESS_RESPONSE("Sms Request Sent"), 
	PROJECT_NAME("kernel-notification-sms"),
	EXCEPTION_DIGITS(" digits");

	/**
	 * The property for sms notification.
	 */
	private String property;

	/**
	 * The constructor to set sms property.
	 * 
	 * @param property
	 *            the property to set.
	 */
	private SmsPropertyConstant(String property) {
		this.property = property;
	}

	/**
	 * Getter for sms property.
	 * 
	 * @return the sms property.
	 */
	public String getProperty() {
		return property;
	}

}
