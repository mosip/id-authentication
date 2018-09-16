package org.mosip.kernel.uingenerator.constants;

/**
 * Error Code for Uin generator
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public enum UinGeneratorErrorCodes {
	UIN_NOT_FOUND("KER-IDG-UIN-001", "Uin could not be found"), UIN_GENERATION_JOB_EXCEPTION("KER-IDG-UIN-002",
			"Error occured in Uin generation job");

	private final String errorCode;
	private final String errorMessage;

	private UinGeneratorErrorCodes(final String errorCode, final String errorMessage) {
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
