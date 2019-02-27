/**
 * 
 */
package io.mosip.kernel.auth.service.impl;

import org.springframework.stereotype.Component;

import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.service.OTPSendService;

/**
 * @author Ramadurai Pandian
 *
 */
@Component
public class OTPSendServiceImpl implements OTPSendService {

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.OTPSendService#sendOTP(io.mosip.kernel.auth.entities.MosipUserDto, java.lang.String, java.lang.String)
	 */
	@Override
	public void sendOTP(MosipUserDto mosipUserDto, String channel, String message) {
		// TODO Auto-generated method stub

	}

}
