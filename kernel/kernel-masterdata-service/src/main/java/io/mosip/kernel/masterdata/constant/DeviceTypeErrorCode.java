package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Device Type Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public enum DeviceTypeErrorCode {
	DEVICE_TYPE_INSERT_EXCEPTION("KER-MSD-001","Exception while inserting data to the db"),
	DEVICE_TYPE_MAPPING_EXCEPTION("KER-MSD-002","Error occured while mapping Device Type details"), 
	DEVICE_TYPE_NOT_FOUND_EXCEPTION("KER-MSD-003","Required Device Type detail Not Found");

	private final String errorCode;
	private final String errorMessage;

	private DeviceTypeErrorCode(final String errorCode, final String errorMessage) {
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
