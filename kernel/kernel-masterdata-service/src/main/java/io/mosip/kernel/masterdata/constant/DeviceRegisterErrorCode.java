package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Device History Details
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
public enum DeviceRegisterErrorCode {
	DEVICE_REGISTER_FETCH_EXCEPTION("ADM-DPM-038", "Error occured while fetching Device Register details"),
	INVALID_STATUS_CODE("ADM-DPM-037","Invalid status received"),
	DEVICE_REGISTER_UPDATE_EXCEPTION("ADM-DPM-038","Error occured while updating Device Register details"),
	DEVICE_REGISTER_CREATE_EXCEPTION("ADM-DPM-038","Error occured while create Device Register details"),
	DATA_NOT_FOUND_EXCEPTION("ADM-DPM-038","Data not found for provided device code"),
	STATUS_CODE_ALREADY_EXISTS("ADM-DPM-039","Status code already exists for the device");

	private final String errorCode;
	private final String errorMessage;

	private DeviceRegisterErrorCode(final String errorCode, final String errorMessage) {
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
