
package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Device Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public enum DeviceErrorCode {
	DEVICE_FETCH_EXCEPTION("KER-MSD-000", "Error occured while fetching device details"), DEVICE_MAPPING_EXCEPTION(
			"KER-MSD-000", "Error occured while mapping device details"), DEVICE_NOT_FOUND_EXCEPTION("KER-MSD-000",
					"Required device detail Not Found");

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
