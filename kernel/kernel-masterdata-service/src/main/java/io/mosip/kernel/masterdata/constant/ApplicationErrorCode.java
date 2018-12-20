package io.mosip.kernel.masterdata.constant;

/**
 * Error code constants
 * 
 * @author Neha Sinha
 * @since 1.0.0
 */
public enum ApplicationErrorCode {
	APPLICATION_FETCH_EXCEPTION("KER-MSD-001",
			"Error occurred while fetching Applications"), APPLICATION_INSERT_EXCEPTION("KER-MSD-101",
					"Error occurred while inserting application details"), APPLICATION_NOT_FOUND_EXCEPTION(
							"KER-MSD-002",
							"Application not found"), APPLICATION_REQUEST_EXCEPTION("KER-MSD-201", "Bad Request Found");

	private final String errorCode;
	private final String errorMessage;

	private ApplicationErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
}
