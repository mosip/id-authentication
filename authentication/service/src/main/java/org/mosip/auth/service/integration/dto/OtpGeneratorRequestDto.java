package org.mosip.auth.service.integration.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * The DTO class for generating an OTP.
 * 
 * @author Rakesh Roshan
 */
@Data
public class OtpGeneratorRequestDto {

	/**
	 * the key against which the otp needs to be generated.
	 */
	@NotNull
	@Size(min = 3, max = 255)
	private String key;
}
