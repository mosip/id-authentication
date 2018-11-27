package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Machine Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public enum MachineDetailErrorCode {
	MACHINE_DETAIL_FETCH_EXCEPTION("KER-MSD-029",
			"Error occured while fetching Machines"), MACHINE_DETAIL_NOT_FOUND_EXCEPTION("KER-MSD-030",
					"Machine not Found");

	private final String errorCode;
	private final String errorMessage;

	private MachineDetailErrorCode(final String errorCode, final String errorMessage) {
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
