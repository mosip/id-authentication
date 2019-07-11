package io.mosip.kernel.masterdata.constant;

/**
 * 
 * @author Srinivasan
 *
 */
public enum RegistrationCenterMachineDeviceHistoryErrorCode {

	REGISTRATION_CENTER_MACHINE_DEVICE_HISTORY_CREATE_EXCEPTION("KER-MSD-XXX",
			"Error occurred while inserting a mapping for Center, Machine and Device");

	private final String errorCode;
	private final String errorMessage;

	private RegistrationCenterMachineDeviceHistoryErrorCode(final String errorCode, final String errorMessage) {
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
