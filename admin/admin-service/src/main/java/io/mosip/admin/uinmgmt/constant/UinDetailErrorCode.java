package io.mosip.admin.uinmgmt.constant;

/**
 * Constant for UINStatus
 * 
 * @autor Megha Tanga
 * 
 */
public enum UinDetailErrorCode {

	REST_SERVICE_EXCEPTION("ADM-UAT-001", "Error occurred while fecthing UIN Details from server"), 
	INVAVIDE_UIN("ADM-UAT-002","UIN does not exist");

	private final String errorCode;
	private final String errorMessage;

	private UinDetailErrorCode(String errorCode, String errorMessage) {
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
