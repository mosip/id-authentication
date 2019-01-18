package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Device History Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public enum DeviceHistoryErrorCode {
	DEVICE_HISTORY_FETCH_EXCEPTION("KER-MSD-128",
			"Error occured while fetching Device History details"), DEVICE_HISTORY_NOT_FOUND_EXCEPTION("KER-MSD-129",
					"Device History not found"), INVALIDE_EFFECTIVE_DATE_TIME_FORMATE_EXCEPTION("KER-MSD-130",
							"Invalid date format");

	private final String errorCode;
	private final String errorMessage;

	private DeviceHistoryErrorCode(final String errorCode, final String errorMessage) {
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
