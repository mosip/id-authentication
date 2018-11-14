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
	MACHINE_HISTORY_FETCH_EXCEPTION("KER-MSD-013","Error occured while fetching machine history details"), 
	MACHINE_HISTORY_MAPPING_EXCEPTION("KER-MSD-014","Error occured while mapping machine history details"), 
	MACHINE_HISTORY_NOT_FOUND_EXCEPTION("KER-MSD-015","Required Machine history detail Not Found"), 
	INVALIDE_EFFECTIVE_DATE_TIME_FORMATE_EXCEPTION("KER-MSD-016", "Effective date and time format should be yyyy-mm-ddThh:mm:ss format");

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
