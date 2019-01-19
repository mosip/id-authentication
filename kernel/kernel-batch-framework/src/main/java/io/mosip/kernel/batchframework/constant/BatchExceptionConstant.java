package io.mosip.kernel.batchframework.constant;

/**
 * This enum provides all the exception constants for batch framework.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public enum BatchExceptionConstant {
	INVALID_URI("KER-BTF-001", "Invalid File Uri"),
	EMPTY_JOB_DESCRIPTION("KER-BTF-002","No Job Description Found"), 
	INVALID_JOB_DESCRIPTION("KER-BTF-003","Invalid Job description Found"),
	DUPLICATE_JOB("KER-BTF-004","Duplicate Job Found"),
	INPUT_OUTPUT("KER-BTF-005");

	/**
	 * The error code.
	 */
	private String errorCode;

	/**
	 * The error message.
	 */
	private String errorMessage;

	/**
	 * @param errorCode
	 *            the errorCode
	 */
	BatchExceptionConstant(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @param errorCode
	 *            The error code to be set.
	 * @param errorMessage
	 *            The error message to be set.
	 */
	BatchExceptionConstant(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @return the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
