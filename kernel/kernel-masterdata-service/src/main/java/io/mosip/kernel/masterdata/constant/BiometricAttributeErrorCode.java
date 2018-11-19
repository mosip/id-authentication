package io.mosip.kernel.masterdata.constant;
/**
 * Constants for Biometric Attribute
 * 
 * @author Udya Kumar
 * @since 1.0.0
 *
 */
public enum BiometricAttributeErrorCode {
	BIOMETRICATTRIBUTE_NOT_FOUND_EXCEPTION("KER-MSD-031",
			"No biometric attributes found for specified biometric code type and language code"), BIOMETRIC_TYPE_FETCH_EXCEPTION(
					"KER-MSD-031", "exception during fatching data from db");

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
