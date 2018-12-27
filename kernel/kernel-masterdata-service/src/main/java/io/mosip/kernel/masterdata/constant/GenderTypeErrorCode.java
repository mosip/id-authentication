package io.mosip.kernel.masterdata.constant;

/**
 * This enum contains error codes for exceptions generated during fetching
 * gender data
 * 
 * @author Sidhant Agarwal
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
public enum GenderTypeErrorCode {

	GENDER_TYPE_FETCH_EXCEPTION("KER-MSD-017", "Error occured while fetching gender types"),

	GENDER_TYPE_NOT_FOUND("KER-MSD-018", "Gender Type not found"),

	GENDER_TYPE_INSERT_EXCEPTION("KER-MSD-068", "Could not insert Gender Data"),

	GENDER_TYPE_UPDATE_EXCEPTION("KER-MSD-101", "Error occurred while updating Gender Type details "),

	GENDER_TYPE_DELETE_EXCEPTION("KER-MSD-102", "Error occurred while deleting Gender Type details ");

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
