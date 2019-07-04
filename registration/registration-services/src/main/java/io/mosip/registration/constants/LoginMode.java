package io.mosip.registration.constants;

/**
 * The Enum LoginMode contains the different modes of login.
 */
public enum LoginMode {

	OTP("OTP"),
	PASSWORD("PASSWORD"),
	CLIENTID("CLIENTID_SECRETKEY");

	/**
	 * Instantiates a new login mode.
	 *
	 * @param code the code
	 */
	private LoginMode(String code) {
		this.code=code;
	}
	
	/** The code. */
	private final String code;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
}
