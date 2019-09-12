package io.mosip.kernel.masterdata.constant;

/**
 * Constants for RegistrationCenterMachine related errors.
 * 
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
public enum RegistrationCenterMachineErrorCode {

	REGISTRATION_CENTER_MACHINE_CREATE_EXCEPTION("KER-MSD-074",
			"Error occurred while inserting a mapping of Machine and Center"),
	REGISTRATION_CENTER_MACHINE_DATA_NOT_FOUND("KER-MSD-114", "Mapping for Machine and Center not found"),
	REGISTRATION_CENTER_MACHINE_DELETE_EXCEPTION("KER-MSD-106",
			"Error occurred while deleting a mapping of Machine and Center"),
	REGISTRATION_CENTER_MACHINE_FETCH_EXCEPTION("KER-MSD-xx","Error occurred while fetching Center Machine details"),
	REGISTRATION_CENTER_MACHINE_ZONE_INVALID("KER-MSD-XXX","Either registrationcenter or machine is not mapped to the user-zone"),
	REGISTRATION_CENTER_MACHINE_STATUS("KER-MSD-XXX","Already is in inactive status"),
	REGISTRATION_CENTER_MACHINE_DECOMMISIONED_STATE("KER-MSD-XXX","Registration center mapped to machine is decommisioned"),
	REGISTRATION_CENTER_MACHINE_ALREADY_ACTIVE("KER-MSD-XXX","Registration center already mapped to machine and is active"),
	REGISTRATION_CENTER_MACHINE_NOT_IN_SAME_HIERARCHY("KER-MSD-XXX","Registration center and machine is not in same hierarchy");

	private final String errorCode;
	private final String errorMessage;

	private RegistrationCenterMachineErrorCode(final String errorCode, final String errorMessage) {
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
