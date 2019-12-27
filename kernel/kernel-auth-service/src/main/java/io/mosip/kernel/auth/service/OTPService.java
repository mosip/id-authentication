/**
 * 
 */
package io.mosip.kernel.auth.service;

import java.util.List;

import io.mosip.kernel.auth.dto.AuthNResponseDto;
import io.mosip.kernel.auth.dto.MosipUserDto;
import io.mosip.kernel.auth.dto.MosipUserTokenDto;
import io.mosip.kernel.auth.dto.otp.OtpUser;

/**
 * @author Ramadurai Pandian
 *
 */

public interface OTPService {

	AuthNResponseDto sendOTP(MosipUserDto mosipUserDto, List<String> channel, String appId);

	MosipUserTokenDto validateOTP(MosipUserDto mosipUser, String otp);

	AuthNResponseDto sendOTPForUin(MosipUserDto mosipUserDto, OtpUser otpUser, String appId);
	
	AuthNResponseDto sendOTP(MosipUserDto mosipUser, OtpUser otpUser) throws Exception;

}
