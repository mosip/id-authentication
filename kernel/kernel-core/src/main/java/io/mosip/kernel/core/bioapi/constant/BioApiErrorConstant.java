package io.mosip.kernel.core.bioapi.constant;

/**
 * Enum containing custom error codes and the respective messages.
 * 
 * @author Manoj SP
 *
 */
public enum BioApiErrorConstant {

	/** Thrown when data provided as input is invalid. */
	INVALID_INPUT_PARAMETER("KER-BIO-001", "Invalid Input Parameter - %s"),
	
	/** Thrown when data required as input is missing. */
	MISSING_INPUT_PARAMETER("KER-BIO-002", "Missing Input Parameter - %s"),
	
	/** Thrown when data provided is valid but quality check cannot be performed. */
	QUALITY_CHECK_FAILED("KER-BIO-003", "Quality check of Biometric data failed"),
	
	/** Thrown when data provided is valid but matching cannot be performed. */
	MATCHING_FAILED("KER-BIO-004", "Matching of Biometric data failed"),
	
	/** Thrown when some other error occurred. */
	UNKNOWN_ERROR("KER-BIO-005", "Unknown error occurred");

	/** The error code. */
	private final String errorCode;

	/** The message. */
	private final String message;

	/**
	 * Instantiates a new error constant.
	 *
	 * @param errorCode the error code
	 * @param message the message
	 */
	BioApiErrorConstant(final String errorCode, final String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
