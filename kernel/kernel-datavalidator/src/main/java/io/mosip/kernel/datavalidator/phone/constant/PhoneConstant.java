/**
 * 
 */
package io.mosip.kernel.datavalidator.phone.constant;

/**
 *
 * @author Megha Tanga
 * 
 * @since 1.0.0
 */

public enum PhoneConstant {

	PHONE_NUM_INVALID_NULL("KER-MOV-001",
			"Phone number should not be empty or null."), PHONE_NUM_INVALID_MIN_MAX_LENGTH("KER-MOV-002",
					"Phone number length should be specified number of digits."), PHONE_NUM_INVALID_DIGITS("KER-MOV-003",
							"Phone number should not contain any special characters except specified characters.");
	/**
	 * This variable holds the error code.
	 */
	private String errorCode;

	/**
	 * This variable holds the error message.
	 */
	private String errorMessage;

	/**
	 * Constructor for PhoneNumberErrorConstants Enum.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	PhoneConstant(String errorCode, String errorMessage) {
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
