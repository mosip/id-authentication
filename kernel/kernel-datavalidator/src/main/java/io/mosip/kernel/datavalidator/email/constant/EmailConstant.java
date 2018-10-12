package io.mosip.kernel.datavalidator.email.constant;

/**
 * Error Code for Email Validator
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public enum EmailConstant {

	EMAIL_INVALID_NULL("KER-EMV-001", "Email  should not be empty or null."), EMAIL_INVALID_LENGTH("KER-EMV-002",
			"Email length should be specified number of characters."), EMAIL_INVALID_DOMAIN_LENGTH("KER-EMV-003",
					"Email Domain extension length should be specified number of characters."), EMAIL_INVALID_CHAR(
							"KER-EMV-004", "Invalid Email Id");

	/**
	 * This variable holds the error code.
	 */
	private String errorCode;

	/**
	 * This variable holds the error message.
	 */
	private String errorMessage;

	/**
	 * Constructor for EmailErrorConstants Enum.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	EmailConstant(String errorCode, String errorMessage) {
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
