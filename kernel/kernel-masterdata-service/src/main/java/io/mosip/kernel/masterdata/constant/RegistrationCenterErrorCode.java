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
	REGISTRATION_CENTER_FETCH_EXCEPTION("KER-MSD-001",
			"Error occured while fetching registration centers"), 
	REGISTRATION_CENTER_MAPPING_EXCEPTION("KER-MSD-002",
					"Error occured while mapping registration centers"),
    REGISTRATION_CENTER_NOT_FOUND("KER-MSD-003",
							"No Registration center found"),
	NUMBER_FORMAT_EXCEPTION("KER-MSD-004",
			"Number Format Exception");

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
