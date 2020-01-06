/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.dto.MosipUserDto;
import io.mosip.kernel.auth.dto.otp.OtpUser;

/**
 * @author Ramadurai Pandian
 *
 */
public interface UinService {

	MosipUserDto getDetailsFromUin(OtpUser otpUser) throws Exception;
	
	MosipUserDto getDetailsForValidateOtp(String uin) throws Exception;
}
