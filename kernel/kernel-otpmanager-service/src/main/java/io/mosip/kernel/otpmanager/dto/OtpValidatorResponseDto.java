package io.mosip.kernel.otpmanager.dto;

import lombok.Data;

/**
 * The DTO class for OTP Validation response.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 * 
 */
@Data
public class OtpValidatorResponseDto {
	/**
	 * The validation request status.
	 */
	private String status;
	/**
	 * The validation request message.
	 */
	private String message;
}
