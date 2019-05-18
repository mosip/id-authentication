/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.dto.MosipUserDto;
import io.mosip.kernel.auth.dto.otp.OtpGenerateResponseDto;
import io.mosip.kernel.auth.dto.otp.OtpUser;

/**
 * @author Ramadurai Pandian
 *
 */
public interface OTPGenerateService {

	OtpGenerateResponseDto generateOTP(MosipUserDto mosipUserDto, String token);
	OtpGenerateResponseDto generateOTPMultipleChannels(MosipUserDto mosipUserDto , OtpUser otpUser,String token);

}
