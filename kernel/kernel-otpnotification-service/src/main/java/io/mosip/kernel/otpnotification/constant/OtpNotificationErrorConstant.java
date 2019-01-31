package io.mosip.kernel.otpnotification.constant;

public enum OtpNotificationErrorConstant {

	NOTIFIER_SERVER_ERROR("xxx", "Service has replied with following error"),
	NOTIFIER_INVALID_TYPE("xxxx","Invalid notification type found"),
	NOTIFIER_INVALID_TYPES_LIMIT("xxxx","Only two notification types acceptable");

	private String errorCode;

	private String errorMessage;

	OtpNotificationErrorConstant(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
