/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.entities.ClientSecret;
import io.mosip.kernel.auth.entities.LoginUser;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.UserOtp;
import io.mosip.kernel.auth.entities.otp.OtpUser;

/**
 * @author Ramadurai Pandian
 *
 */
public interface AuthNDataService {

	MosipUserDto authenticateUser(LoginUser loginUser) throws Exception;

	MosipUserDto authenticateWithOtp(OtpUser otpUser) throws Exception;

	MosipUserDto authenticateUserWithOtp(UserOtp loginUser) throws Exception;

	MosipUserDto authenticateWithSecretKey(ClientSecret clientSecret) throws Exception;

}
