/**
 * 
 */
package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Registration center device History
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
public enum RegistrationCenterDeviceHistoryErrorCode {
	REGISTRATION_CENTER_DEVICE_HISTORY_FETCH_EXCEPTION("KER-MSD-xxx",
			"Error occured while fetching registration center device history details"), REGISTRATION_CENTER_DEVICE_HISTORY_NOT_FOUND_EXCEPTION(
					"KER-MSD-xxx",
					" registration center device history not found"), INVALIDE_EFFECTIVE_DATE_TIME_FORMATE_EXCEPTION(
							"KER-MSD-033", "Invalid date format");

	private final String errorCode;
	private final String errorMessage;

	private RegistrationCenterDeviceHistoryErrorCode(final String errorCode, final String errorMessage) {
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
