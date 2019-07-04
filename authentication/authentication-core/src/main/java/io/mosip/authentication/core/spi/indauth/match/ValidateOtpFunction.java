package io.mosip.authentication.core.spi.indauth.match;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * To validate OTP against Otpvalue and Otpkey
 * 
 * @author Dinesh Karuppiah.T
 */
public interface ValidateOtpFunction {

	/**
	 * Validates OTP.
	 *
	 * @param otpValue the otp value
	 * @param otpKey the otp key
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public boolean validateOtp(String otpValue, String otpKey) throws IdAuthenticationBusinessException;

}
