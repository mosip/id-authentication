package io.mosip.kernel.pinvalidator.constant;

/**
 * Exception Code for Pin Validator
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
public enum PinExceptionConstant {

	PIN_INVALID_LENGTH("KER-IDV-501", "Static PIN length Must be "), PIN_INVALID_CHAR("KER-IDV-502",
			"Static PIN length must be numeric"), PIN_INVALID_NULL("KER-IDV-503", "Input parameter is missing.");

	/**
	 * This variable holds the error code.
	 */
	private String errorCode;

	/**
	 * This variable holds the error message.
	 */
	private String errorMessage;

	/**
	 * Constructor for PinExceptionConstant Enum.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	PinExceptionConstant(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for errorCode.
	 * 
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for errorMessage.
	 * 
	 * @return the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
