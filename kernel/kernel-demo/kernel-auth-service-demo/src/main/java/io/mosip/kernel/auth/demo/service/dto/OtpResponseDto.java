package io.mosip.kernel.auth.demo.service.dto;

import lombok.Data;

/**
 * The DTO class for sms notification response.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
public class OtpResponseDto {

	/**
	 * The generated OTP.
	 */
	private String otp;
	/**
	 * The status of the corresponding generation request.
	 */
	private String status;

}
