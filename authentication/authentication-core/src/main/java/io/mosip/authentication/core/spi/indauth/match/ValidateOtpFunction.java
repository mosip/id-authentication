package io.mosip.authentication.core.spi.indauth.match;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * To validate OTP against Otpvalue and Otpkey
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
