package org.mosip.auth.core.spi.otpgen.facade;

import org.mosip.auth.core.dto.otpgen.OtpRequestDTO;
import org.mosip.auth.core.dto.otpgen.OtpResponseDTO;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;

/**
 * Facade service to generate OTP.
 * 
 * @author Rakesh Roshan
 */
public interface OTPFacade {
	/**
	 * 
	 * @param otpKey
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	public OtpResponseDTO generateOtp(OtpRequestDTO otpKey) throws IdAuthenticationBusinessException;
}
