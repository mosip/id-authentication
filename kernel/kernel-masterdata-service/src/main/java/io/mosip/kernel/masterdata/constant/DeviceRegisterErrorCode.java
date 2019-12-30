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
	DEVICE_REGISTER_DELETED_EXCEPTION("ADM-DPM-003", "Error occured while deleted Device Register details"),
	DATA_NOT_FOUND_EXCEPTION("ADM-DPM-038","Data not found for provided device code"),
	DEVICE_DE_REGISTERED_ALREADY("KER-DPM-002", "Device already de-registered"),
	DEVICE_REGISTER_NOT_FOUND_EXCEPTION("KER-DPM-001", "No register device found");
	

	

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
