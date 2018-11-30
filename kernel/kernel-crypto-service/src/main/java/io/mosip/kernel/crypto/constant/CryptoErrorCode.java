/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.constant;

/**
 * Error Constants for Cryptographic Service
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
public enum CryptoErrorCode {
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
	INVALID_DATA_WITHOUT_KEY_BREAKER("KER-CRY-003","data sent to decrypt is without key splitter"),
	/**
	 * 
	 */
	INVALID_REQUEST("KER-CRY-004","Invalid request"),
	/**
	 * 
	 */
	CANNOT_CONNECT_TO_SOFTHSM_SERVICE("KER-CRY-005","cannot connect to softhsm service"),
	/**
	 * 
	 */
	KEYMANAGER_SERVICE_ERROR("KER-CRY-006","Keymanager Service has replied with following error")
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
	private CryptoErrorCode(final String errorCode, final String errorMessage) {
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
