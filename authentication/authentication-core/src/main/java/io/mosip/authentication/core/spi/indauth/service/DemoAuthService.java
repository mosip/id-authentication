package io.mosip.authentication.core.spi.indauth.service;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;


/**
 * 
 * This interface is used to authenticate Individual based on Demo attributes.
 * 
 * @author Gurpreet Bagga
 */
public interface DemoAuthService {
	
	/**
	 * Gets the demo status.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the demo status
	 */
	public AuthStatusInfo getDemoStatus(AuthRequestDTO authRequestDTO);
	
	

}