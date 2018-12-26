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
	REGISTRATION_CENTER_DEVICE_DEPENDENCY_EXCEPTION("KER-MSD-075",
			"Error occurred while inserting a mapping of Device and Center"),
	REGISTRATION_CENTER_DEVICE_DATA_NOT_FOUND("KER-MSD-075",
			"registration center device mapping not found"),
	REGISTRATION_CENTER_DEVICE_DELETE_EXCEPTION("KER-MSD-XX", "Exception during deletion");;

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
