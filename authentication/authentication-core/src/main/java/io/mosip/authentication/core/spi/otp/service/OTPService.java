package io.mosip.authentication.core.spi.otp.service;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.otp.dto.OtpResponseDTO;

/**
 * The {@code OTPAuthService} interface to trigger OTP request to core-kernal
 * for generate and build otp.
 * 
 * @author Rakesh Roshan
 */
@FunctionalInterface
public interface OTPService {

	/**
	 * Generate otp.
	 *
	 * @param otpRequest OtpRequestDTO request.
	 * @param partnerID the partner ID
	 * @return OtpResponseDTO object return.
	 * @throws IdAuthenticationBusinessException exception
	 */

	public OtpResponseDTO generateOtp(OtpRequestDTO otpRequest, String partnerID)
			throws IdAuthenticationBusinessException;

}
