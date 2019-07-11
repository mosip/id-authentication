package io.mosip.kernel.applicanttype.constant;

/**
 * Constants for Applicant Type related errors.
 * 
 * @author Bal Vikash Sharma
 *
 */
public enum ApplicantTypeErrorCode {

	NO_APPLICANT_FOUND_EXCEPTION("KER-MSD-147", "Applicant Type data does not exist"),
	INVALID_REQUEST_EXCEPTION("KER-MSD-148", "One or more input parameter is invalid or does not exist");

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
