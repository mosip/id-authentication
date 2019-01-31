/**
 * 
 */
package io.mosip.kernel.idvalidator.uin.constant;

/**
 *
 * @author Megha Tanga
 * 
 * @since 1.0.0
 */

public enum UinExceptionConstant {
	
	UIN_VAL_INVALID_NULL("KER-IDV-201" , "UIN should not be Empty or Null."),
	UIN_VAL_ILLEGAL_SEQUENCE_REPEATATIVE("KER-IDV-202", "UIN should not contain any sequential and repeated block of number for 2 or more than two digits and Admin ristricted numbers"), 
	UIN_VAL_ILLEGAL_LENGTH("KER-IDV-203", "UIN length should be 12 digit."), 
	UIN_VAL_INVALID_DIGITS("KER-IDV-204", "UIN should not contain any alphanumeric characters."), 
	UIN_VAL_ILLEGAL_CHECKSUM("KER-IDV-205", "UIN should match checksum."),
	UIN_VAL_INVALID_ZERO_ONE("KER-IDV-206", "UIN should not contain Zero or One as first digit."),
	UIN_VAL_ILLEGAL_REVERSE("KER-IDV-xxx", "UIN First X digits should be different from the reverse of last X digits"),
	UIN_VAL_ILLEGAL_EQUAL_LIMIT("KER-IDV-xxx", "UIN First X digits should be different from the last X digits");
	/**
	 * This variable holds the error code.
	 */
	private String errorCode;

	/**
	 * This variable holds the error message.
	 */
	private String errorMessage;

	/**
	 * Constructor for UINErrorConstants Enum.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	UinExceptionConstant(String errorCode, String errorMessage) {
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

