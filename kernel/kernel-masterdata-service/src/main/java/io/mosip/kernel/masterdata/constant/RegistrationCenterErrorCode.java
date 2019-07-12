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
	REGISTRATION_CENTER_INSERT_EXCEPTION("KER-MSD-060","Error occurred while Inserting Registration Center details"),
	REGISTRATION_CENTER_UPDATE_EXCEPTION("KER-MSD-111", "Error occurred while updating Registration Center details"),
	REGISTRATION_CENTER_DELETE_EXCEPTION("KER-MSD-112", "Error occurred while deleting Registration Center details"),
	REGISTRATION_CENTER_NOT_FOUND("KER-MSD-042", "Registration Center not found"),
	REGISTRATION_CENTER_LANGUAGE_EXCEPTION("KER-MSD-303", "Received data is not present in all Languages supported by MOSIP"),
	REGISTRATION_CENTER_ID_EXCEPTION("KER-MSD-304", "Center IDs received for all languages is not same"),
	REGISTRATION_CENTER_ID_LANGUAGECODE_EXCEPTION("KER-MSD-305", "Center ID and Language Code combination is not unique in the request received"),
	REGISTRATION_CENTER_LANGUAGECODE_EXCEPTION("KER-MSD-306", "Records with duplicate language code found"),
	DEPENDENCY_EXCEPTION("KER-MSD-149", "Cannot delete as dependency found"),
	DATE_TIME_PARSE_EXCEPTION("KER-MSD-043", "Invalid date format"),
	REGISTRATION_CENTER_START_END_EXCEPTION("KER-MSD-309", "Center Start Time must be smaller than Center End Time"),
	REGISTRATION_CENTER_LUNCH_START_END_EXCEPTION("KER-MSD-308", "Center Lunch Start Time must be smaller than Center Lunch End Time"),
	REGISTRATION_CENTER_FORMATE_EXCEPTION("KER-MSD-307", "Latitude or Longitude must have minimum 4 digits after decimal"),
	DATA_TO_BE_VALIDATED_WITH_NOT_FOUND("KER-MSD-XXX", "start/end time Data not configured in database"),
	NO_LOCATION_DATA_AVAILABLE("KER-MSD-320","No Location found for value %s"),
	NO_CENTERTYPE_AVAILABLE("KER-MSD-321","No Registration Center type found for value %s"),
	NO_ZONE_AVAILABLE("KER-MSD-XXX","No Zone found for value %s"),
	USER_ZONE_NOT_FOUND("KER-MSD-XXX","User's zone not found");

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
