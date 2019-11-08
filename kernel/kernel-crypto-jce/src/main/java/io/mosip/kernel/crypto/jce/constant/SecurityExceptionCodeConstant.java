/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */

package io.mosip.kernel.crypto.jce.constant;

/**
 * {@link Enum} for exception constants
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum SecurityExceptionCodeConstant {
	MOSIP_INVALID_KEY_EXCEPTION("KER-FSE-001",
			"invalid Key (key is null or empty or has invalid encoding, wronglength, and uninitialized, etc)."),
	MOSIP_INVALID_DATA_LENGTH_EXCEPTION("KER-FSE-002", "check input data length"),
	MOSIP_INVALID_DATA_EXCEPTION("KER-FSE-003", "data not valid (currupted,length is not valid etc.)"),
	MOSIP_INVALID_ENCRYPTED_DATA_CORRUPT_EXCEPTION("KER-FSE-004", "encrypted data is corrupted"),
	MOSIP_INVALID_DATA_SIZE_EXCEPTION("KER-FSE-005", "ecrypted data size is not valid"),
	MOSIP_NULL_DATA_EXCEPTION("KER-FSE-006", "data is null or length is 0"),
	MOSIP_NULL_METHOD_EXCEPTION("KER-FSE-007", "mosip security method is null"),
	MOSIP_NO_SUCH_ALGORITHM_EXCEPTION("KER-FSE-008", "no such algorithm"),
	MOSIP_INVALID_PARAM_SPEC_EXCEPTION("KER-FSE-009", "invalid param spec"),
	MOSIP_SIGNATURE_EXCEPTION("KER-FSE-010", "invalid signature, maybe null or empty"), 
	SALT_PROVIDED_IS_NULL_OR_EMPTY("KER-FSE-011", "salt provided is null or empty");

	/**
	 * Constant {@link Enum} errorCode
	 */
	private final String errorCode;

	/**
	 * Getter for errorMessage
	 * 
	 * @return get errorMessage value
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Constant {@link Enum} errorMessage
	 */
	private final String errorMessage;

	/**
	 * Constructor for this class
	 * 
	 * @param value set {@link Enum} value
	 */
	private SecurityExceptionCodeConstant(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for errorCode
	 * 
	 * @return get errorCode value
	 */
	public String getErrorCode() {
		return errorCode;
	}
}
