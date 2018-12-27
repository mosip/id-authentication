package io.mosip.authentication.service.integration.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The DTO class for OTP generation response.
 * 
 * @author Rakesh Roshan
 */

@Data
@NotNull
@AllArgsConstructor
@NoArgsConstructor
public class OtpGeneratorResponseDto {

	/**
	 * Variable to hold the generated otp.
	 */

	@NotEmpty
	private String otp;
	
	/**
	 * Variable to hold the otp response status.
	 */
	private String status;

	/**
	 * Variable to hold the otp response message.
	 */
	private String message;
}
