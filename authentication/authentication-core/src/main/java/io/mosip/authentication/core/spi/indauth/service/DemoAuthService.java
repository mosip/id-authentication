package io.mosip.authentication.core.spi.indauth.service;

import java.util.List;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;


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
	public boolean getDemoStatus(AuthRequestDTO authRequestDTO);
	
	

}