/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.dto.AuthNResponseDto;
import io.mosip.kernel.auth.dto.ClientSecret;
import io.mosip.kernel.auth.dto.LoginUser;
import io.mosip.kernel.auth.dto.UserOtp;
import io.mosip.kernel.auth.dto.otp.OtpUser;

/**
 * @author Ramadurai Pandian
 *
 */
public interface AuthNService {

	AuthNResponseDto authenticateUser(LoginUser loginUser) throws Exception;

	AuthNResponseDto authenticateWithOtp(OtpUser otpUser) throws Exception;

	AuthNResponseDto authenticateUserWithOtp(UserOtp loginUser) throws Exception;

	AuthNResponseDto authenticateWithSecretKey(ClientSecret clientSecret) throws Exception;

}
