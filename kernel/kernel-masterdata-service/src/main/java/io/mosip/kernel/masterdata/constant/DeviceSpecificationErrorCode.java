package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Device Specification
 * 
 * @author Udya Kumar
 * @since 1.0.0
 *
 */
public enum DeviceSpecificationErrorCode {
	DEVICE_SPECIFICATION_NOT_FOUND_EXCEPTION("KER-MSD-012",
			"Device Specification not found"), DEVICE_SPECIFICATION_DATA_FETCH_EXCEPTION("KER-MSD-011",
					"Error occured while fetching Device Specifications"),
	DEVICE_SPECIFICATION_INSERT_EXCEPTION("KER-APP-444", "Error occurred while Device Specification");

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
