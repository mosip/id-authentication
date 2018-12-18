package io.mosip.authentication.core.spi.otpgen.facade;

import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * Facade service to generate OTP.
 * 
 * @author Rakesh Roshan
 */
@FunctionalInterface
public interface OTPFacade {
	/**
	 * 
	 * @param otpRequest OtpRequestDTO request.
	 * @return OtpResponseDTO object return.
	 * @throws IdAuthenticationBusinessException exception
	 */
	
	public OtpResponseDTO generateOtp(OtpRequestDTO otpRequest) throws IdAuthenticationBusinessException;
}
