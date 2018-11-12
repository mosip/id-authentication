package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Machine Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public enum MachineDetailErrorCode {
	MACHINE_DETAIL_FETCH_EXCEPTION("KER-MSD-008","Error occured while fetching machine details"),
	MACHINE_DETAIL_MAPPING_EXCEPTION("KER-MSD-009","Error occured while mapping machine details"),
	MACHINE_DETAIL_NOT_FOUND_EXCEPTION("KER-MSD-010","Required Machine detail Not Found");

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
