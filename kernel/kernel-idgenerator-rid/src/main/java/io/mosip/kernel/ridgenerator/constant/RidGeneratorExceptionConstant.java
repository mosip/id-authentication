package io.mosip.kernel.ridgenerator.constant;

/**
 * This enum provide all the exception constants for rid generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public enum RidGeneratorExceptionConstant {
	MOSIP_EMPTY_INPUT_ERROR_CODE("KER-RIG-002", "Empty input entered"), 
	MOSIP_INPUT_LENGTH_ERROR_CODE("KER-RIG-003","input length is not sufficient"),
	MOSIP_NULL_VALUE_ERROR_CODE("KER-RIG-001", "Null value entered");

	/**
	 * The errorCode.
	 */
	public final String errorCode;

	/**
	 * The errorMessage.
	 */
	public final String errorMessage;

	/**
	 * The constructor to set exception errorCode and errorMessage.
	 * 
	 * @param errorCode
	 *            The error code to be set.
	 * @param errorMessage
	 *            The error message to be set.
	 */
	private RidGeneratorExceptionConstant(String errorCode, String errorMessage) {
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
