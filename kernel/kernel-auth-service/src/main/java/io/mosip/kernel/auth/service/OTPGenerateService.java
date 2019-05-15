/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.otp.OtpGenerateResponseDto;
import io.mosip.kernel.auth.entities.otp.OtpUser;

/**
 * @author Ramadurai Pandian
 *
 */
public interface OTPGenerateService {

	OtpGenerateResponseDto generateOTP(MosipUserDto mosipUserDto, String token);
	OtpGenerateResponseDto generateOTPMultipleChannels(MosipUserDto mosipUserDto , OtpUser otpUser,String token);

}
