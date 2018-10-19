package io.mosip.kernel.otpmanager.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.stereotype.Component;

/**
 * The DTO class for OTP Generation request.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 * 
 */
@Component
public class OtpGeneratorRequestDto {
	/**
	 * The key against which OTP is to be generated.
	 */
	@NotNull
	@Size(min = 3, max = 255)
	private String key;

	/**
	 * Getter for key.
	 * 
	 * @return The key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Setter for key.
	 * 
	 * @param key
	 *            The key to be set.
	 */
	public void setKey(String key) {
		this.key = key;
	}
}
