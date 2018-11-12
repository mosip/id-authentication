package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Device Specification
 * 
 * @author Udya Kumar
 * @since 1.0.0
 *
 */
public enum DeviceSpecificationErrorCode {
	DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION("KER-MSD-000",
			"No device specification found for specified  language code"), DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION(
					"KER-MSD-001", "exception during fatching data from db");

	private final String errorCode;
	private final String errorMessage;

	private DeviceSpecificationErrorCode(final String errorCode, final String errorMessage) {
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
