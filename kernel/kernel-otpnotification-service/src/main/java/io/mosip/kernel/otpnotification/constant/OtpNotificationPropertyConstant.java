package io.mosip.kernel.otpnotification.constant;

/**
 * This ENUM provides all the property constant.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public enum OtpNotificationPropertyConstant {

	NOTIFICATION_TYPE_SMS("sms"),
	NOTIFICATIPON_TYPE_EMAIL("email"),
	NOTIFICATION_RESPONSE_MESSAGE("Otp notification sent successfully"),
	NOTIFICATION_RESPONSE_STATUS("success"),
	NOTIFICATION_OTP_TEMPLATE_PLACEHOLDER("$otp"),
	NOTIFICATION_TEMPLATE_OTP_VALUE("otp"),
	NOTIFICATION_EMAIL_TO("mailTo"),
	NOTIFICATION_EMAIL_CC("mailCC"),
	NOTIFICATION_EMAIL_SUBJECT("mailSubject"),
	NOTIFICATION_EMAIL_CONTENT("mailContent");

	/**
	 * The property.
	 */
	private String property;

	/**
	 * Constructor for OtpNotificationPropertyConstant.
	 * 
	 * @param property
	 *            the property.
	 */
	OtpNotificationPropertyConstant(String property) {
		this.property = property;
	}

	/**
	 * Getter for property.
	 * 
	 * @return the property.
	 */
	public String getProperty() {
		return property;
	}

}
