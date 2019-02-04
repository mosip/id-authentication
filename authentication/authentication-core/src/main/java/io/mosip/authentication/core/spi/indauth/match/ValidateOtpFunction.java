package io.mosip.authentication.core.spi.indauth.match;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
public interface ValidateOtpFunction {

	/**
	 * Validates OTP
	 * 
	 * @param otpValue
	 * @param otpKey
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	public boolean validateOtp(String otpValue, String otpKey) throws IdAuthenticationBusinessException; 

}
