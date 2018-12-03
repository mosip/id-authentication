package io.mosip.kernel.masterdata.constant;

/**
 * Constants for device Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public enum DeviceErrorCode {
	DEVICE_FETCH_EXCEPTION("KER-MSD-009", "Error occured while fetching Devices"), DEVICE_NOT_FOUND_EXCEPTION(
			"KER-MSD-010", "Device not  Found"), DEVICE_CREATE_EXCEPTION("111", "222");

	private final String errorCode;
	private final String errorMessage;

	private DeviceErrorCode(final String errorCode, final String errorMessage) {
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
