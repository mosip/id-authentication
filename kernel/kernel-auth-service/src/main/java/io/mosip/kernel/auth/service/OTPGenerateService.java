/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.otp.OtpGenerateResponseDto;

/**
 * @author Ramadurai Pandian
 *
 */
public interface OTPGenerateService {

	OtpGenerateResponseDto generateOTP(MosipUserDto mosipUserDto, String token);

}
