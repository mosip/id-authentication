/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.entities.MosipUserDto;

/**
 * @author Ramadurai Pandian
 *
 */
public interface OTPSendService {
	
	void sendOTP(MosipUserDto mosipUserDto, String channel,String message);

}
