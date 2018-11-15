package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Biometric Type
 * 
 * @author Neha
 * @since 1.0.0
 */
public enum BiometricTypeErrorCode {
	BIOMETRIC_TYPE_FETCH_EXCEPTION("KER-MSD-029",
			"Error ocurred while fetching biometric types"), BIOMETRIC_TYPE_MAPPING_EXCEPTION("KER-MSD-030",
					"Error occured while mapping biometric types"), BIOMETRIC_TYPE_NOT_FOUND("KER-MSD-045",
							"No biometric types found.");

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
