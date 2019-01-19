package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Biometric Type
 * 
 * @author Neha
 * @since 1.0.0
 */
public enum BiometricTypeErrorCode {
	BIOMETRIC_TYPE_FETCH_EXCEPTION("KER-MSD-005", "Error occurred while fetching Biometric Types"), 
	BIOMETRIC_TYPE_INSERT_EXCEPTION("KER-MSD-105", "Error occurred while inserting biometric type details"),
	BIOMETRIC_TYPE_NOT_FOUND("KER-MSD-006", "Biometric Type not found");

	private final String errorCode;
	private final String errorMessage;

	private BiometricTypeErrorCode(final String errorCode, final String errorMessage) {
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
