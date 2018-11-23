package io.mosip.kernel.masterdata.constant;

/**
 * This enum contains error codes for exceptions generated during fetching
 * gender data
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public enum GenderTypeErrorCode {
	GENDER_TYPE_FETCH_EXCEPTION("KER-MSD-017",
			"Error occured while fetching gender types"), GENDER_TYPE_NOT_FOUND("KER-MSD-018", "Gender Type not found");

	private final String errorCode;
	private final String errorMessage;

	private GenderTypeErrorCode(final String errorCode, final String errorMessage) {
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
