package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Machine Type Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public enum MachineTypeErrorCode {

	MACHINE_TYPE_INSERT_EXCEPTION("KER-MSD-061", "Error occurred while inserting Machine Type details"),
	MACHINE_TYPE_FETCH_EXCEPTION("KER-MSD-062","Error occurred while fetching Machine Type details"),
	MACHINE_TYPE_NOT_FOUND("KER-MSD-063","Machine Type Not Found");

	private final String errorCode;
	private final String errorMessage;

	private MachineTypeErrorCode(final String errorCode, final String errorMessage) {
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
