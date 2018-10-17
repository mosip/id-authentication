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

package io.mosip.kernel.security.cipher.constant;

/**
 * {@link Enum} for exception constants
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum MosipSecurityExceptionCodeConstants {
	MOSIP_INVALID_KEY_EXCEPTION("COK-FSE-001", "key is not valid"),
	MOSIP_INVALID_KEY_SIZE_EXCEPTION("COK-FSE-002", "key size is not valid"),
	MOSIP_INVALID_LENGTH_EXCEPTION("COK-FSE-003",
			"invalid parameters \n 1.if using plain rsa data is invalid \n  2.if using hybrid rsa use larger key with this encoding"),
	MOSIP_INVALID_DATA_LENGTH_EXCEPTION("COK-FSE-013", "check input data length"),
	MOSIP_INVALID_KEY_CORRUPT_EXCEPTION("COK-FSE-004", "key is corrupted"),
	MOSIP_INVALID_ASYMMETRIC_PRIVATE_KEY_EXCEPTION("COK-FSE-005", "use private key instead of public"),
	MOSIP_INVALID_ASYMMETRIC_PUBLIC_KEY_EXCEPTION("COK-FSE-006", "use public key instead of private"),
	MOSIP_INVALID_DATA_EXCEPTION("COK-FSE-007", "data not valid"),
	MOSIP_INVALID_ENCRYPTED_DATA_CORRUPT_EXCEPTION("COK-FSE-008", "encrypted data is corrupted"),
	MOSIP_INVALID_DATA_SIZE_EXCEPTION("COK-FSE-009", "ecrypted data size is not valid"),
	MOSIP_NULL_DATA_EXCEPTION("COK-FSE-010", "data is null"),
	MOSIP_NULL_METHOD_EXCEPTION("COK-FSE-014", "mosip security method is null"),
	MOSIP_NO_SUCH_ALGORITHM_EXCEPTION("COK-FSE-011", "no such algorithm"),
	MISSING_PROVIDER_EXCEPTION_EXCEPTION("COK-FSE-013", "missing provider exception"),
	MOSIP_NULL_KEY_EXCEPTION("COK-FSE-012", "key is null");

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
	private MosipSecurityExceptionCodeConstants(final String errorCode, final String errorMessage) {
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
