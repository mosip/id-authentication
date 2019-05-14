package io.mosip.authentication.common.service.integration.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
@Data
public class OtpValidateRequestDTO {

	/**
	 * Variable to hold the key.
	 */
	@NotNull
	private String key;

	/**
	 * Variable to hold the otp.
	 */
	@NotNull
	private String Otp;

}
