package io.mosip.kernel.idgenerator.tokenid.constant;

/**
 * Utility methods for Token ID Generator.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public enum TokenIDExceptionConstant {
	EMPTY_OR_NULL_VALUES("KER-TIG-001", "Input values should not be empty or null");

	/**
	 * The error code.
	 */
	private String errorCode;
	/**
	 * The error message.
	 */
	private String errorMessage;

	/**
	 * TokenIDExceptionConstantr constructor with errorCode and errorMessage as the
	 * arguments.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	TokenIDExceptionConstant(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for error code.
	 * 
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for error message.
	 * 
	 * @return the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
