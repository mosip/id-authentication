package io.mosip.kernel.masterdata.constant;

/**
 * Error code constants
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
public enum ApplicantTypeErrorCode {
	APPLICANT_TYPE_FETCH_EXCEPTION("KER-MSD-149",
			"Error occurred while fetching Applicant type"), APPLICANT_TYPE_NOT_FOUND_EXCEPTION("KER-MSD-150",
					"No records found");

	private final String errorCode;
	private final String errorMessage;

	private ApplicantTypeErrorCode(String errorCode, String errorMessage) {
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
