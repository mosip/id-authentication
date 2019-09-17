package io.mosip.kernel.masterdata.constant;

/**
 * Constants for RegistrationCenterDevice related errors.
 * 
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
public enum RegistrationCenterDeviceErrorCode {

	REGISTRATION_CENTER_DEVICE_CREATE_EXCEPTION("KER-MSD-075",
			"Error occurred while inserting a mapping of Device and Center"),
	REGISTRATION_CENTER_DEVICE_DATA_NOT_FOUND("KER-MSD-115", "Mapping for Device and Center not found"),
	REGISTRATION_CENTER_DEVICE_DELETE_EXCEPTION("KER-MSD-105",
			"Error occurred while deleting a mapping of Device and Center"),
	REGISTRATION_CENTER_DEVICE_FETCH_EXCEPTION("KER-MSD-xxxxx","Error occurred while fetching Center Device details"),
	REGISTRATION_CENTER_DEVICE_ALREADY_UNMAPPED_EXCEPTION("KER-MSD-xxxX", "Device-User unmapping already exist"),
	DEVICE_AND_REG_CENTER_MAPPING_NOT_FOUND_EXCEPTION("KER-MSD-xxxx","Device Id %s - Center Id %s mapping does not exist"),
	INVALIDE_DEVICE_ZONE("KER-MSD-415", "Admin not authorized to map/un-map this Device"),
	INVALIDE_CENTER_ZONE("KER-MSD-411", "Admin not authorized to map/un-map this Device"),
	REGISTRATION_CENTER_DEVICE_MAPPING_EXCEPTION("KER-MSD-XXXx", "Error occurred while mapping Device to Registration Center"),
	REGISTRATION_CENTER_DEVICE_NOT_IN_SAME_HIERARCHY("KER-MSD-416","Device cannot be mapped to the Center as Center and Device does not belong to the same Administrative Zone"),
	REGISTRATION_CENTER_ALREADY_MAPPED("KER-MSD-419","Registration center is already mapped to a device"),
	REGISTRATION_CENTER_DEVICE_ZONE_INVALID("KER-MSD-411","Either Registration center or Device zone does not match user's zone"),
	REGISTATION_CENTER_DEVICE_DECOMMISIONED_STATE("KER-MSD-418","Cannot map as the Registration Center/Device is Decommissioned");
	private final String errorCode;
	private final String errorMessage;

	private RegistrationCenterDeviceErrorCode(final String errorCode, final String errorMessage) {
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
