package org.mosip.auth.core.spi.otpgen.service;

import org.mosip.auth.core.exception.IdAuthenticationBusinessException;

/**
 * The {@code OTPAuthService} interface to trigger OTP request to core-kernal
 * for generate and build otp.
 * 
 * @author Rakesh Roshan
 */

public interface OTPService {

	public String generateOtp(String otpKey) throws IdAuthenticationBusinessException;

}
