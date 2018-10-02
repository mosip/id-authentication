package io.mosip.kernel.otpmanagerservice.service;

import io.mosip.kernel.otpmanagerservice.dto.OtpGeneratorRequestDto;
import io.mosip.kernel.otpmanagerservice.dto.OtpGeneratorResponseDto;

/**
 * This interface provides the methods which can be used for OTP generation.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public interface OtpGeneratorService {
	/**
	 * This method can be used to generate OTP against a particular key. OTP against
	 * a particular key is generated only if the key is not freezed.
	 * 
	 * @param otpDto
	 *            The OTP generation DTO.
	 * @return The generated OTP.
	 */
	public OtpGeneratorResponseDto getOtp(OtpGeneratorRequestDto otpDto);
}
