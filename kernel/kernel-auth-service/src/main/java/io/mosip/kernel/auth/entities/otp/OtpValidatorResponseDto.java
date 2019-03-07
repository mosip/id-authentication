package io.mosip.kernel.auth.entities.otp;

import lombok.Data;

/**
 * The DTO class for OTP Validation response.
 * 
 * @author Ramadurai Pandian
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
