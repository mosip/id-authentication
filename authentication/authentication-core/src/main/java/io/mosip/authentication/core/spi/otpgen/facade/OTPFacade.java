package io.mosip.authentication.core.spi.otpgen.facade;

import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * Facade service to generate OTP.
 * 
 * @author Rakesh Roshan
 */
public interface OTPFacade {
	/**
	 * 
	 * @param otpRequest
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	
	public OtpResponseDTO generateOtp(OtpRequestDTO otpRequest) throws IdAuthenticationBusinessException;
}
