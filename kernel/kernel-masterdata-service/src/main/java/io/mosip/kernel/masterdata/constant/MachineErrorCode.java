package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Machine Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public enum MachineErrorCode {
	MACHINE_FETCH_EXCEPTION("KER-MSD-029", "Error occured while fetching Machines"), MACHINE_NOT_FOUND_EXCEPTION(
			"KER-MSD-030", "Machine not Found"), MACHINE_INSERT_EXCEPTION("KER-APP-000",
					"Error occurred while inserting Machine details");

	private final String errorCode;
	private final String errorMessage;

	private MachineErrorCode(final String errorCode, final String errorMessage) {
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
