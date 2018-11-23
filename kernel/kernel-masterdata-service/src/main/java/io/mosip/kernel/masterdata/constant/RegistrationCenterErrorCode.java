package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Registration Center
 * 
 * @author Dharmesh Khandelwal
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
public enum RegistrationCenterErrorCode {
	REGISTRATION_CENTER_FETCH_EXCEPTION("KER-MSD-041",
			"Error occured while fetching Registration Centers"), REGISTRATION_CENTER_NOT_FOUND("KER-MSD-042",
					"Registration Center not found"), DATE_TIME_PARSE_EXCEPTION("KER-MSD-043", "Invalid date format");

	private final String errorCode;
	private final String errorMessage;

	private RegistrationCenterErrorCode(final String errorCode, final String errorMessage) {
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
