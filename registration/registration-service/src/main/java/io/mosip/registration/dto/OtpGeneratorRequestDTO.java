package io.mosip.registration.dto;

/**
 * This class holds the otp related key
 * 
 * @author Yaswanth S
 *
 * @since 1.0.0
 */

public class OtpGeneratorRequestDTO {

	/**
	 * the key against which the otp needs to be generated.
	 */
	private String key;

	/**
	 * Getter for key.
	 * 
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Setter for key.
	 * 
	 * @param key
	 *            the key to be set.
	 */
	public void setKey(String key) {
		this.key = key;
	}

}
