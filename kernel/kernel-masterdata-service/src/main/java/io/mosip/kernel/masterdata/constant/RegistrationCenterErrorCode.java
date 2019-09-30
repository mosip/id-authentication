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
	REGISTRATION_CENTER_ALL_Lang("KER-MSD-XXX","For the given ID data is present in all supported Language code of the MOSIP, So go for Edit"),
	
	

	LANGUAGE_EXCEPTION("KER-MSD-382", "Received language code %s is not a configured language"),
	REGISTRATION_CENTER_ID("KER-MSD-381","Center ID %s does not exist for primary language"),
	
	ID_NOT_UNIQUE("KER-MSD-362","Please make sure that all entered IDs are same-%s"),
	CENTER_TYPE_CODE_NOT_UNIQUE("KER-MSD-363","Please make sure that all entered Center-Type-Code are same-%s"),
	LATITUDE_NOT_UNIQUE("KER-MSD-364","Please make sure that all entered Latitude are same-%s"),
	LONGITUDE_NOT_UNIQUE("KER-MSD-364","Please make sure that all entered Longitude are same-%s"),
	CONTACT_PHONE_NOT_UNIQUE("KER-MSD-366","Please make sure that all entered Contact-Phone are same-%s"),
	WORKING_HOURS_NOT_UNIQUE("KER-MSD-367","Please make sure that all entered Working-Hours are same-%s"),
	CENTER_STRART_TIME_NOT_UNIQUE("KER-MSD-368","Please make sure that all entered Center-Start-Time are same-%s"),
	CENTER_END_TIME_NOT_UNIQUE("KER-MSD-369","Please make sure that all entered Center-End-Time are same-%s"),
	LUNCH_START_TIME_NOT_UNIQUE("KER-MSD-370","Please make sure that all entered Lunch-Start-Time are same-%s"),
	LUNCH_END_TIME_NOT_UNIQUE("KER-MSD-371","Please make sure that all entered Lunch-End-Time are same-%s"),
	TIME_ZONE_NOT_UNIQUE("KER-MSD-342","Please make sure that all entered Time-Zone are same-%s"),
	HOLIDAY_LOCATION_CODE_NOT_UNIQUE("KER-MSD-373","Please make sure that all entered Holiday-Location-Code are same-%s"),
	ZONE_CODE_NOT_UNIQUE("KER-MSD-344","Please make sure that all entered Zone-Code are same-%s"),
    PERKIOSKPROCESSTIME_NOT_UNIQUE("KER-MSD-375","Please make sure that all entered Per-Kiosk-Process-Time are same-%s"),
    IS_ACTIVE_NOT_UNIQUE("KER-MSD-376","Please make sure that all entered Is-Active Status are same-%s"),
	ID_SIZE("KER-MSD-377","Please make sure that ID size must be between 1-10 charectors-%s"),
	ID_LANGUAGE("KER-MSD-378","Cannot activate as the Center against ID %s is not present in all the configured languages "),
	IS_ACTIVE("KER-MSD-379", "Center against the ID %d Received is already Active"),
	IS_IN_ACTIVE("KER-MSD-380", "Center against the ID %d Received is already Inactive"),
	DEFAULT_LANGUAGE("KER-MSD-383", "Recived data dones not contain data for Primary Langauge"),
	
	

	INVALIDE_ZONE("KER-MSD-346", "Received Zone Code does not belong to the User"),
	USER_ZONE_NOT_FOUND("KER-MSD-341","No zone assigned to the user"),
	MAPPED_TO_DEVICE("KER-MSD-350","Cannot Decommission the Registration Center, as some devices are mapped to the Registration Center"),
	MAPPED_TO_USER("KER-MSD-352","Cannot Decommission the Registration Center, as some users are mapped to the Registration Center"),
	MAPPED_TO_MACHINE("KER-MSD-351","Cannot Decommission the Registration Center, as some machines are mapped to the Registration Center"),
	DECOMMISSION_FAILED("KER-MSD-354","Internal Server Error"),
	INVALID_RCID_LENGTH("KER-MSD-353","Center ID specified for decommission has invalid length"),
	REG_CENTER_INVALIDE_ZONE("KER-MSD-441", "Admin not authorized to access this Registration Center for this Zone"),
	DECOMMISSIONED("KER-MSD-442", "Registration center has been already decommissioned or Registration center not found"),
	HOLIDAY_NOT_FOUND("KER-MSD-440", "Holiday Not Found");


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
