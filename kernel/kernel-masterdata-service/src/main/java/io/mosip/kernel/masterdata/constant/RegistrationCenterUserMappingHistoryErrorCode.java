package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Registration Center
 * 
 * @author Dharmesh Khandelwal
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
public enum RegistrationCenterUserMappingHistoryErrorCode {

	REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_FETCH_EXCEPTION("KER-MSD-037",
			"Error occured while fetching Registration Center, Machine, User mappings"), REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_NOT_FOUND(
					"KER-MSD-038", "Registration Center, Machine, User not found"), DATE_TIME_PARSE_EXCEPTION(
							"KER-MSD-039", "Invalid date format");

	/**
	 * The error code
	 */
	private final String errorCode;
	/**
	 * The error message
	 */
	private final String errorMessage;

	/**
	 * Constructor to set error code and message
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	private RegistrationCenterUserMappingHistoryErrorCode(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Function to get error code
	 * 
	 * @return {@link #errorCode}
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Function to get the error message
	 * 
	 * @return {@link #errorMessage}r
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
