package io.mosip.registration.service;

import io.mosip.registration.dto.ResponseDTO;

/**
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface UserMachineMappingService {
	/**
	 * global config details are synched from server to client
	 * 
	 * @return responseDto
	 */
	public ResponseDTO syncUserDetails();
}
