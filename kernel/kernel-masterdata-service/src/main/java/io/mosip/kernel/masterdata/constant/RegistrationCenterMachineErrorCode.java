package io.mosip.kernel.masterdata.constant;

/**
 * Constants for RegistrationCenterMachine related errors.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
public enum RegistrationCenterMachineErrorCode {

	REGISTRATION_CENTER_MACHINE_CREATE_EXCEPTION("KER-MSD-XX",
			"Error while mapping Registration center and device"), REGISTRATION_CENTER_MACHINE_CONSTRAINT_VOILATION_EXCEPTION(
					"KER-MSD-XX", "Registration center id or Machine id is not correct");

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
