package io.mosip.kernel.otpnotification.constant;

/**
 * This ENUM provides all the constant identified for OTP notification errors.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public enum OtpNotificationErrorConstant {

	NOTIFIER_SERVER_ERROR("KER-NOT-001", "Service has replied with following error"), 
	NOTIFIER_INVALID_TYPE("KER-NOT-002","Invalid notification type found"),
	NOTIFIER_IO_ERROR("KER-NOT-003","Input output error occur during template merging"),
	NOTIFIER_TEMPLATE_MERGER_ERROR("KER-NOT-004"," template must contains placeholder $otp"),
	NOTIFIER_SMS_TEMPLATE_ERROR("KER-NOT-005","Sms template cannot be empty or null"),
	NOTIFIER_SMS_NUMBER_ERROR("KER-NOT-006","Number cannot be empty or null"),
	NOTITFIER_EMAIL_ID_ERROR("KER-NOT-007","Email id cannot be empty or null"),
	NOTITFIER_EMAIL_SUBJECT_ERROR("KER-NOT-008","Email subject cannot be empty or null"),
	NOTIFIER_EMAIL_BODY_TEMPLATE_ERROR("KER-NOT-009","Email body template cannot be empty or null");

	/**
	 * The error code.
	 */
	private String errorCode;

	/**
	 * The error message.
	 */
	private String errorMessage;

	/**
	 * Constructor for OtpNotificationErrorConstant.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	OtpNotificationErrorConstant(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for error code.
	 * 
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for error message.
	 * 
	 * @return the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
