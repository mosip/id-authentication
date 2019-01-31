package io.mosip.kernel.otpnotification.constant;

public enum OtpNotificationPropertyConstant {

	NOTIFICATION_TYPE_SMS("sms"),
	NOTIFICATIPON_TYPE_EMAIL("email"),
	NOTIFICATION_RESPONSE_MESSAGE("Otp notification sent successfully"),
	NOTIFICATION_RESPONSE_STATUS("success");

	private String property;

	OtpNotificationPropertyConstant(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}

}
