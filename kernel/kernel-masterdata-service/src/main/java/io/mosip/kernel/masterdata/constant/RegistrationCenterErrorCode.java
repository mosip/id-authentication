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
	NO_ZONE_AVAILABLE("KER-MSD-340","Zone %s doesn't exist"),

	
	PRIMARY_LANGUAGE_EXCEPTION("KER-MSD-XXX", "Received data is not present in Primary Language supported by MOSIP"),
	SECONDARY_LANGUAGE_EXCEPTION("KER-MSD-XXX", "%s Language does not supported by MOSIP"),
	REGISTRATION_CENTER_ID("KER-MSD-XXX","%s gievn Registartion Center Id is not present in DB"),
	REGISTRATION_CENTER_ALL_Lang("KER-MSD-XXX","For the given ID data is present in all supported Language code of the MOSIP, So go for Edit"),
	
	
	ID_NOT_UNIQUE("KER-MSD-xx2","Please make sure that all entered IDs are same-%s"),
	CENTER_TYPE_CODE_NOT_UNIQUE("KER-MSD-xx2","Please make sure that all entered Center-Type-Code are same-%s"),
	LATITUDE_NOT_UNIQUE("KER-MSD-xx3","Please make sure that all entered Latitude are same-%s"),
	LONGITUDE_NOT_UNIQUE("KER-MSD-xx4","Please make sure that all entered Longitude are same-%s"),
	CONTACT_PHONE_NOT_UNIQUE("KER-MSD-xx5","Please make sure that all entered Contact-Phone are same-%s"),
	WORKING_HOURS_NOT_UNIQUE("KER-MSD-xx6","Please make sure that all entered Working-Hours are same-%s"),
	CENTER_STRART_TIME_NOT_UNIQUE("KER-MSD-xx7","Please make sure that all entered Center-Start-Time are same-%s"),
	CENTER_END_TIME_NOT_UNIQUE("KER-MSD-xx8","Please make sure that all entered Center-End-Time are same-%s"),
	LUNCH_START_TIME_NOT_UNIQUE("KER-MSD-xx9","Please make sure that all entered Lunch-Start-Time are same-%s"),
	LUNCH_END_TIME_NOT_UNIQUE("KER-MSD-x10","Please make sure that all entered Luncg-End-Time are same-%s"),
	TIME_ZONE_NOT_UNIQUE("KER-MSD-x11","Please make sure that all entered Time-Zone are same-%s"),
	HOLIDAY_LOCATION_CODE_NOT_UNIQUE("KER-MSD-x14","Please make sure that all entered Holiday-Location-Code are same-%s"),
	ZONE_CODE_NOT_UNIQUE("KER-MSD-x15","Please make sure that all entered Zone-Code are same-%s"),
	ID_SIZE("KER-MSD-x16","Please make sure that ID size must be between 1-10 charectors-%s"),
	ID_LANGUAGE("KER-MSD-x16","for the given %s-id records not in MOSIP supported language"),
	IS_ACTIVE("KER-MSD-x17", "%d- id is already Active"),
	

	INVALIDE_ZONE("KER-MSD-346", "Received Zone Code does not belong to the User"),
	USER_ZONE_NOT_FOUND("KER-MSD-341","No zone assigned to the user"),
	MAPPED_TO_DEVICE("KER-MSD-350","Cannot Decommission the Registration Center, as some devices are mapped to the Registration Center"),
	MAPPED_TO_USER("KER-MSD-352","Cannot Decommission the Registration Center, as some users are mapped to the Registration Center"),
	MAPPED_TO_MACHINE("KER-MSD-351","Cannot Decommission the Registration Center, as some machines are mapped to the Registration Center"),
	DECOMMISSION_FAILED("KER-MSD-354","Internal Server Error"),
	INVALID_RCID_LENGTH("KER-MSD-353","Center ID specified for decommission has invalid length");


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
