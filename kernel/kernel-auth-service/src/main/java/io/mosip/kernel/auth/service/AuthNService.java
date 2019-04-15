/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.entities.AuthNResponseDto;
import io.mosip.kernel.auth.entities.ClientSecret;
import io.mosip.kernel.auth.entities.LoginUser;
import io.mosip.kernel.auth.entities.UserOtp;
import io.mosip.kernel.auth.entities.otp.OtpUser;

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
