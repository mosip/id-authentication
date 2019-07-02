package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Registration Center
 * 
 * @author Dharmesh Khandelwal
 * @author Abhishek Kumar
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public enum RegistrationCenterErrorCode {
	REGISTRATION_CENTER_FETCH_EXCEPTION("KER-MSD-041", "Error occured while fetching Registration Centers"),
	REGISTRATION_CENTER_UPDATE_EXCEPTION("KER-MSD-111", "Error occurred while updating Registration Center details"),
	REGISTRATION_CENTER_DELETE_EXCEPTION("KER-MSD-112", "Error occurred while deleting Registration Center details"),
	REGISTRATION_CENTER_NOT_FOUND("KER-MSD-042", "Registration Center not found"),
	DEPENDENCY_EXCEPTION("KER-MSD-149", "Cannot delete as dependency found"),

	DATE_TIME_PARSE_EXCEPTION("KER-MSD-043", "Invalid date format"),

	DATA_TO_BE_VALIDATED_WITH_NOT_FOUND("KER-MSD-XXX", "start/end time Data not configured in database"),
	NO_LOCATION_DATA_AVAILABLE("KER-MSD-XXX","No Location found for value %s");

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
