package io.mosip.authentication.core.spi.indauth.service;

/**
 * 
 * The interface to Validate OTP request via {OTP Manager}.
 * 
 * 
 * 
 * @author Dinesh Karuppiah.T
 */
public interface OTPAuthService extends AuthService{

	/**
	 * Validate otp.
	 *
	 * @param authreqdto AuthRequestDTO
	 * @param uin the uin
	 * @return AuthStatusInfo
	 * @throws IdAuthenticationBusinessException exception
	 *//*
	AuthStatusInfo validateOtp(AuthRequestDTO authreqdto,String uin) throws IdAuthenticationBusinessException;*/
}
