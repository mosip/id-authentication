package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Biometric Attribute
 * 
 * @author Udya Kumar
 * @since 1.0.0
 *
 */
public enum BiometricAttributeErrorCode {
	BIOMETRICATTRIBUTE_NOT_FOUND_EXCEPTION("KER-MSD-004",
			"BiometricAttribute not found"), BIOMETRIC_TYPE_FETCH_EXCEPTION("KER-MSD-003",
					"Error occurred while fetching BiometricAttributes"), BIOMETRICATTRIBUTE_INSERT_EXCEPTION(
							"KER-APP-103", "Error occurred while inserting BiometricAttributes");

	private final String errorCode;
	private final String errorMessage;

	private BiometricAttributeErrorCode(final String errorCode, final String errorMessage) {
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
