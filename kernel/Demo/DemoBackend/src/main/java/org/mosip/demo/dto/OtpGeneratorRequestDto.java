package org.mosip.demo.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The Dto class for generating an OTP.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public class OtpGeneratorRequestDto {

	/**
	 * the key against which the otp needs to be generated.
	 */
	@NotNull
	@Size(min = 3, max = 255)
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
