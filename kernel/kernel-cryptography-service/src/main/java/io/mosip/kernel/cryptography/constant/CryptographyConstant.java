package io.mosip.kernel.cryptography.constant;

/**
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public enum CryptographyConstant {

	/**
	 * 
	 */
	KEY_SPLITTER("#KEY_SPLITTER#");

	/**
	 * 
	 */
	private final String value;

	/**
	 * @param value
	 */
	private CryptographyConstant(final String value) {
		this.value = value;

	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}

}
