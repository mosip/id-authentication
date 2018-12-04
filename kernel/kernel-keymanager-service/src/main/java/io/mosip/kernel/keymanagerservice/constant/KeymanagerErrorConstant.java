package io.mosip.kernel.keymanagerservice.constant;

/**
 * This ENUM provides all the constant identified for Keymanager Service errors.
 * 
 * @author Dharmesh Khandelwal
 * @version 1.0.0
 *
 */
public enum KeymanagerErrorConstant {
	/**
	 * 
	 */
	VALIDITY_CHECK_FAIL("KER-KMS-001", "Certificate is not valid"),

	/**
	 * 
	 */
	APPLICATIONID_NOT_VALID("KER-KMS-002", "ApplicationId not found in Key Policy"),

	/**
	 * 
	 */
	NO_UNIQUE_ALIAS("KER-KMS-003", "No unique alias is found"),

	/**
	 * 
	 */
	NO_SUCH_ALGORITHM_EXCEPTION("KER-KMS-004", "No Such algorithm is supported"),
	/**
	 * 
	 */
	INVALID_REQUEST("KER-KMS-005", "Invalid request"),
	/**
	 * 
	 */
	DATE_TIME_PARSE_EXCEPTION("KER-KMS-006",
			"timestamp should be in ISO 8601 format yyyy-MM-ddTHH::mm:ss.SZ (e.g. 2019-04-05T14:30)"),

	CRYPTO_EXCEPTION("KER-KMS-007", "Exception occured in cypto library: ");

	/**
	 * The error code.
	 */
	private final String errorCode;

	/**
	 * The error message.
	 */
	private final String errorMessage;

	/**
	 * @param errorCode
	 *            The error code to be set.
	 * @param errorMessage
	 *            The error message to be set.
	 */
	private KeymanagerErrorConstant(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * @return The error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @return The error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
