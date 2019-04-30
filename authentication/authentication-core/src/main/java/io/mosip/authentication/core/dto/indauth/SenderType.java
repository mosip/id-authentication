package io.mosip.authentication.core.dto.indauth;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
public enum SenderType {

	/**
	 * Enum for Auth
	 */
	AUTH("auth"),
	/**
	 * Enum for Otp
	 */
	OTP("otp");

	/**
	 * Variable to hold name
	 */
	private String name;

	SenderType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
