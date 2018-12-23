package io.mosip.kernel.masterdata.constant;

/**
 * Constants for RegistrationCenterMachine related errors.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
public enum RegistrationCenterMachineErrorCode {

	REGISTRATION_CENTER_MACHINE_CREATE_EXCEPTION("KER-MSD-074",
			"Error occurred while inserting a mapping of Machine and Center");

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
