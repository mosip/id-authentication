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

package org.mosip.kernel.core.security.constants;

/**
 * {@link Enum} for exception constants
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum MosipSecurityExceptionCodeConstants {
	MOSIP_INVALID_KEY_EXCEPTION("COK-FSE-FSE-001"), 
	MOSIP_INVALID_DATA_EXCEPTION("COK-FSE-FSE-002"), 
	MOSIP_NO_SUCH_ALGORITHM_EXCEPTION("COK-FSE-FSE-003"), 
	MOSIP_NULL_KEY_EXCEPTION("COK-FSE-FSE-004"), 
	MOSIP_NULL_DATA_EXCEPTION("COK-FSE-FSE-005"),

	MOSIP_INVALID_KEY_EXCEPTION_MESSAGE("key is not valid"), 
	MOSIP_INVALID_KEY_SIZE_EXCEPTION_MESSAGE("key size is not valid"), 
	MOSIP_INVALID_KEY_EXCEPTION_LARGER_KEY_MESSAGE("use a larger key size with this padding"), 
	MOSIP_INVALID_KEY_CORRUPT_EXCEPTION_MESSAGE("key is corrupted"), 
	MOSIP_INVALID_ASYMMETRIC_PRIVATE_KEY_EXCEPTION_MESSAGE("use private key instead of public"),
	MOSIP_INVALID_ASYMMETRIC_PUBLIC_KEY_EXCEPTION_MESSAGE("use public key instead of private"),

	MOSIP_NULL_KEY_EXCEPTION_MESSAGE("key is null"),

	MOSIP_INVALID_DATA_EXCEPTION_MESSAGE("data not valid"), 
	MOSIP_INVALID_ENCRYPTED_DATA_CORRUPT_EXCEPTION_MESSAGE("encrypted data is corrupted"), 
	MOSIP_INVALID_DATA_SIZE_EXCEPTION_MESSAGE("ecrypted data size is not valid"), 
	MOSIP_NULL_DATA_EXCEPTION_MESSAGE("data is null"),

	MOSIP_NO_SUCH_METHOD_EXCEPTION_MESSAGE("no such algorithm");

	/**
	 * Constant {@link Enum} value
	 */
	private final String value;

	/**
	 * Constructor for this class
	 * 
	 * @param value
	 *            set {@link Enum} value
	 */
	private MosipSecurityExceptionCodeConstants(final String value) {
		this.value = value;
	}

	/**
	 * Getter for value
	 * 
	 * @return get errorCode value
	 */
	public String getValue() {
		return value;
	}
}
