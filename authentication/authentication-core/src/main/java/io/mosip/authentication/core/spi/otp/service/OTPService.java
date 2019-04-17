package io.mosip.authentication.core.spi.otp.service;

import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * The {@code OTPAuthService} interface to trigger OTP request to core-kernal
 * for generate and build otp.
 * 
 * @author Rakesh Roshan
 */
@FunctionalInterface
public interface OTPService {

	/**
	 * 
	 * @param otpRequest OtpRequestDTO request.
	 * @return OtpResponseDTO object return.
	 * @throws IdAuthenticationBusinessException exception
	 */

	public OtpResponseDTO generateOtp(OtpRequestDTO otpRequest, String partnerID)
			throws IdAuthenticationBusinessException;

}
