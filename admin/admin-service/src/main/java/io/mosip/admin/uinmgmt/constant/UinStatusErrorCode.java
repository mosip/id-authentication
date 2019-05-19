package io.mosip.admin.uinmgmt.constant;
/**
 * Constant for UINStatus
 * 
 * @autor Megha Tanga
 * 
 */
public enum UinStatusErrorCode {
	
	UIN_STATUS_EXCEPTION("XXXX","Error occurred while fecthing the UIN Details from server"),
	UIN_PROPERTY_NOT_FOUND("XXXXX", "Error occurred while fecthing UIN Properties from server");
	
	private final String errorCode;
	private final String errorMessage;

	private UinStatusErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

}
