package io.mosip.kernel.masterdata.constant;

/**
 * Constants for RegistrationCenterDevice related errors.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
public enum RegistrationCenterDeviceErrorCode {

	REGISTRATION_CENTER_DEVICE_CREATE_EXCEPTION("KER-MSD-075",
			"Error occurred while inserting a mapping of Device and Center");

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
