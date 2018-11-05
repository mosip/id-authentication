package io.mosip.kernel.otpmanager.dto;

import lombok.Data;

/**
 * The DTO class for OTP Generation response.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 * 
 */
@Data
public class OtpGeneratorResponseDto {
	/**
	 * The generated OTP.
	 */
	private String otp;
	/**
	 * The status of the corresponding generation request.
	 */
	private String status;
}
