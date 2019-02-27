/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.MosipUserDtoToken;
import io.mosip.kernel.auth.entities.MosipUserWithToken;

/**
 * @author Ramadurai Pandian
 *
 */

public interface OTPService {
	
	boolean sendOTP(MosipUserDto mosipUserDto,String channel);

	MosipUserDtoToken validateOTP(MosipUserDto mosipUser, String otp);

}
