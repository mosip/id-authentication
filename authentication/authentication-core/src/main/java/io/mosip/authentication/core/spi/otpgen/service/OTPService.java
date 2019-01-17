package io.mosip.authentication.core.spi.otpgen.service;

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
	 * Method to generate Otp.
	 *
	 * @param otpKey the otp key
	 * @return the string
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public String generateOtp(String otpKey) throws IdAuthenticationBusinessException;

}
