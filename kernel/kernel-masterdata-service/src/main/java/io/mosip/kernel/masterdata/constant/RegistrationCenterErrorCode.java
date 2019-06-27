package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Registration Center
 * 
 * @author Dharmesh Khandelwal
 * @author Abhishek Kumar
 * @author Sagar Mahapatra
 * @author MeghaTanga
 * @since 1.0.0
 *
 */
public enum RegistrationCenterErrorCode {
	REGISTRATION_CENTER_FETCH_EXCEPTION("KER-MSD-041", "Error occured while fetching Registration Centers"),
	REGISTRATION_CENTER_INSERT_EXCEPTION("KER-MSD-XXX","Error occurred while Inserting Registration Center details"),
	REGISTRATION_CENTER_UPDATE_EXCEPTION("KER-MSD-111", "Error occurred while updating Registration Center details"),
	REGISTRATION_CENTER_DELETE_EXCEPTION("KER-MSD-112", "Error occurred while deleting Registration Center details"),
	REGISTRATION_CENTER_NOT_FOUND("KER-MSD-042", "Registration Center not found"),
	REGISTRATION_CENTER_REQUEST_EXCEPTION("KER-MSD-YYY", "Bad Request Found"),
	REGISTRATION_CENTER_LANGUAGE_EXCEPTION("KER-MSD-ZZZ", "Please make sure that entered Data is persent in all Languges supportted by MOSIP"),
	REGISTRATION_CENTER_ID_EXCEPTION("KER-MSD-NNN", "Please make sure that enter All IDs must be same"),
	REGISTRATION_CENTER_ID_LANGUAGECODE_EXCEPTION("KER-MSD-NNN", "ID and LangCode Combination is duplicate"),
	DEPENDENCY_EXCEPTION("KER-MSD-149", "Cannot delete as dependency found"),

	DATE_TIME_PARSE_EXCEPTION("KER-MSD-043", "Invalid date format"),

	DATA_TO_BE_VALIDATED_WITH_NOT_FOUND("KER-MSD-XXX", "start/end time Data not configured in database");

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
