package io.mosip.registration.service;

import io.mosip.registration.dto.ResponseDTO;

public interface UserSaltDetailsService {

	/**
	 * Gets the user salt details.
	 *
	 * @param tigger the tigger
	 * @return the user salt details
	 */
	public ResponseDTO getUserSaltDetails(String tigger);

}
