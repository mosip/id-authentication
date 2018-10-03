/**
 * 
 */
package io.mosip.kernel.idvalidator.uinvalidator.constants;

/**
 *
 * @author Megha Tanga
 * 
 * @since 1.0.0
 */

public enum MosipIDExceptionCodeConstants {
	
	UIN_VAL_INVALID_NULL("1111111111", "Entered UIN should not be empty or null."),
	UIN_VAL_ILLEGAL_SEQUENCE_REPEATATIVE("222222222", "Entered UIN should not contain any sequential and repeated block of number for 2 or more than two digits"), 
	UIN_VAL_ILLEGAL_LENGTH("333333333", "Entered UIN length should be 12 digit."), 
	UIN_VAL_INVALID_DIGITS("44444444", "Entered UIN should not contain any alphanumeric characters."), 
	UIN_VAL_ILLEGAL_CHECKSUM("6666666666", "Entered UIN should match checksum."),
	UIN_VAL_INVALID_ZERO_ONE("7777777777", "Entered UIN should not contain Zero or One as first Digit.");
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
	MosipIDExceptionCodeConstants(String errorCode, String errorMessage) {
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


