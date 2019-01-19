package io.mosip.authentication.core.spi.indauth.service;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * The interface to Validate OTP request via {OTP Manager}.
 * 
 * 
 * 
 * @author Dinesh Karuppiah.T
 */
@FunctionalInterface
public interface OTPAuthService {

	/**
	 * Validate otp.
	 *
	 * @param authreqdto AuthRequestDTO
	 * @param uin the uin
	 * @return AuthStatusInfo
	 * @throws IdAuthenticationBusinessException exception
	 */
	AuthStatusInfo validateOtp(AuthRequestDTO authreqdto,String uin) throws IdAuthenticationBusinessException;
}
