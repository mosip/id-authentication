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
	NOTIFIER_INVALID_TYPES_LIMIT("KER-NOT-003","Only two notification types acceptable"),
	NOTIFIER_IO_ERROR("KER-NOT-004","Input output error occur during template merging"),
	NOTIFIER_TEMPLATE_MERGER_ERROR("KER-NOT-005"," template must contains placeholder $otp");

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
