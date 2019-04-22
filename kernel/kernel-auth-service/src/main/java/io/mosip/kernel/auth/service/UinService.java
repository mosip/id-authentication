/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.otp.OtpUser;

/**
 * @author Ramadurai Pandian
 *
 */
public interface UinService {

	MosipUserDto getDetailsFromUin(OtpUser otpUser) throws Exception;

}
