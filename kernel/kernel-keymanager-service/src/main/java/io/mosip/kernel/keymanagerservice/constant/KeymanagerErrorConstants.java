package io.mosip.kernel.keymanagerservice.constant;

/**
 * This ENUM provides all the constant identified for Keymanager Service errors.
 * 
 * @author Dharmesh Khandelwal
 * @version 1.0.0
 *
 */
public enum KeymanagerErrorConstants {
	VALIDITY_CHECK_FAIL("KER-KMS-001","Certificate is not valid"),
	APPLICATIONID_NOT_VALID("KER-KMS-002","Application Id not Valid");
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
	private KeymanagerErrorConstants(String errorCode, String errorMessage) {
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
