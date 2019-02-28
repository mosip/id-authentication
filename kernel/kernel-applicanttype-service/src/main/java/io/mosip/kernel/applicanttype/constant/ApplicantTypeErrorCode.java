package io.mosip.kernel.applicanttype.constant;

/**
 * Constants for Applicant Type related errors.
 * 
 * @author Bal Vikash Sharma
 *
 */
public enum ApplicantTypeErrorCode {

	NO_APPLICANT_FOUND_EXCEPTION("KER-MSD-147", "Applicant type not found"), INVALID_REQUEST_EXCEPTION("KER-MSD-148",
			"Invalid query passed to get applicant type");

	private final String errorCode;
	private final String errorMessage;

	private ApplicantTypeErrorCode(final String errorCode, final String errorMessage) {
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
