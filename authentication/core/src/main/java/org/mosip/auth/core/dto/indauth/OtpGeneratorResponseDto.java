package org.mosip.auth.core.dto.indauth;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * The DTO class for OTP generation response.
 * 
 * @author Rakesh Roshan
 */

@Data
@NotNull
public class OtpGeneratorResponseDto {

	/**
	 * Variable to hold the generated otp.
	 */

	@NotEmpty
	private String otp;
}
