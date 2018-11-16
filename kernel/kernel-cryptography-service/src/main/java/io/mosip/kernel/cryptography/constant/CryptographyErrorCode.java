/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptography.constant;

/**
 * Error Constants for Cryptographic Service
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
public enum CryptographyErrorCode {
	/**
	 * 
	 */
	NO_SUCH_ALGORITHM_EXCEPTION("KER-CRY-001", "No Such algorithm is supported"), 
	/**
	 * 
	 */
	INVALID_SPEC_PUBLIC_KEY("KER-CRY-002", "public key is invalid"),
	/**
	 * 
	 */
	INVALID_DATA_WITHOUT_KEY_BREAKER("KER-CRY-003","data sent to decrypt is without key splitter")
	;

	/**
	 * 
	 */
	private final String errorCode;
	/**
	 * 
	 */
	private final String errorMessage;

	/**
	 * @param errorCode
	 * @param errorMessage
	 */
	private CryptographyErrorCode(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * @return
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @return
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
