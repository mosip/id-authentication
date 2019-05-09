package io.mosip.registration.service;

import io.mosip.registration.dto.ResponseDTO;

/**
 * Interface to get user details data from server to client
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface UserDetailService {

	/**
	 * Gets the user detail.
	 *
	 * @param triggerpoint the triggerpoint
	 * @return the user detail
	 */
	public ResponseDTO save(String triggerpoint);

}
