package org.mosip.auth.core.spi.indauth.service;

import org.mosip.auth.core.dto.indauth.AuthRequestDTO;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.springframework.stereotype.Service;

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
