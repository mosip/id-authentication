package io.mosip.kernel.masterdata.constant;

/**
 * Constants for RegistrationCenterMachineDevice related errors.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
public enum RegistrationCenterMachineDeviceErrorCode {

	REGISTRATION_CENTER_MACHINE_DEVICE_CREATE_EXCEPTION("KER-MSD-076",
			"Error occurred while inserting a mapping of Center, Machine and Device"),
	
	REGISTRATION_CENTER_MACHINE_DEVICE_DELETE_EXCEPTION("KER-MSD-107",
			"Error occurred while deleting a mapping of Center, Machine and Device"),
	REGISTRATION_CENTER_MACHINE_DEVICE_DATA_NOT_FOUND_EXCEPTION("KER-MSD-116","Mapping for Center, Machine and Device not found");
	
	private final String errorCode;
	private final String errorMessage;

	private RegistrationCenterMachineDeviceErrorCode(final String errorCode, final String errorMessage) {
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
