package io.mosip.authentication.core.spi.indauth.service;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * The interface to Validate OTP request via {OTP Manager}.
 * 
 * 
 * 
 * @author Dinesh Karuppiah.T
 */


public interface OTPAuthService {

	/**
	 * 
	 * @param pinValue - pin value
	 * @param UIN      - Unique Number
	 * @return
	 */

	public boolean validateOtp(AuthRequestDTO authreqdto, String txnId) throws IdAuthenticationBusinessException;

}
