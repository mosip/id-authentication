/**
 * 
 */
package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Machine History Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public enum MachineHistoryErrorCode {
	MACHINE_HISTORY_FETCH_EXCEPTION("KER-MSD-031",
			"Error occured while fetching Machine History details"), MACHINE_HISTORY_NOT_FOUND_EXCEPTION("KER-MSD-032",
					"Machine History not found"), INVALIDE_EFFECTIVE_DATE_TIME_FORMATE_EXCEPTION("KER-MSD-033",
							"Invalid date format");

	private final String errorCode;
	private final String errorMessage;

	private MachineHistoryErrorCode(final String errorCode, final String errorMessage) {
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
