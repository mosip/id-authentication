package io.mosip.kernel.idgenerator.tokenid.constant;

/**
 * Exception constant ENUM for Token ID Generator.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh sinha
 * @since 1.0.0
 *
 */
public enum TokenIDExceptionConstant {
	TOKENID_FETCH_EXCEPTION("KER-TIG-001", "Error occur while fetching counter and value details"),
	TOKENID_INSERTION_EXCEPTION("KER-TIG-002", "Error occur while updating counter details");

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
	 * @param errorCode    the error code.
	 * @param errorMessage the error message.
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
